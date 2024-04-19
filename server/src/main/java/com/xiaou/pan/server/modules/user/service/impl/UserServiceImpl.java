package com.xiaou.pan.server.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaou.pan.core.constants.CacheConstants;
import com.xiaou.pan.core.exception.RPanBusinessException;
import com.xiaou.pan.core.response.R;
import com.xiaou.pan.core.response.ResponseCode;
import com.xiaou.pan.core.utils.IdUtil;
import com.xiaou.pan.core.utils.JwtUtil;
import com.xiaou.pan.core.utils.PasswordUtil;
import com.xiaou.pan.server.common.utils.UserIdUtil;
import com.xiaou.pan.server.modules.file.constants.FileConstants;
import com.xiaou.pan.server.modules.file.context.CreateFolderContext;
import com.xiaou.pan.server.modules.file.domain.UPanUserFile;
import com.xiaou.pan.server.modules.file.service.IUserFileService;
import com.xiaou.pan.server.modules.user.constants.UserConstants;
import com.xiaou.pan.server.modules.user.context.*;
import com.xiaou.pan.server.modules.user.converter.UserConverter;
import com.xiaou.pan.server.modules.user.domain.UPanUser;
import com.xiaou.pan.server.modules.user.mapper.UPanUserMapper;
import com.xiaou.pan.server.modules.user.service.IUserService;
import com.xiaou.pan.server.modules.user.vo.UserInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * @author Lenovo
 * @description 针对表【u_pan_user(用户信息表)】的数据库操作Service实现
 * @createDate 2024-04-13 15:24:24
 */
@Service("userService")
@Slf4j
public class UserServiceImpl extends ServiceImpl<UPanUserMapper, UPanUser>
        implements IUserService {

    @Resource
    private UserConverter userConverter;

    @Resource
    private IUserFileService userFileService;

    @Resource
    private CacheManager cacheManager;

    /**
     * 注册用户
     * 需要实现的功能点：
     * 1.注册用户信息
     * 2.创建新用户的根目录信息
     * <p>
     * 需要实现的技术难点：
     * 1，该业务是幂等的
     * 2.要保证用户名全局唯一
     * <p>
     * 实现技术难点的处理方案：
     * 1.幂等性通过数据库表对于用户名字段添加唯一索引，我们上有业务捕获对应的冲突异常，转换返回
     *
     * @param userRegisterContext
     * @return
     */
    @Override
    public Long register(UserRegisterContext userRegisterContext) {
        UPanUser entity = assembleUserEntity(userRegisterContext);
        doRegister(userRegisterContext);
        createUserRootFolder(userRegisterContext);
        return userRegisterContext.getEntity().getUserId();
    }

    /**
     * 用户登录业务
     * 需要实现的功能点：
     * 1.用户的登陆信息校验
     * 2.生产一个具有时效性的accessToken
     * 3.将accessToken缓存起来，去实现单机登陆
     *
     * @param userLoginContext
     * @return
     */
    @Override
    public String login(UserLoginContext userLoginContext) {
        checkLoginInfo(userLoginContext);
        generateAndSaveAccessToken(userLoginContext);
        return userLoginContext.getAccessToken();
    }

    /**
     * 用户退出登陆
     * 1.清除用户的登陆凭证缓存
     */
    @Override
    public void logout(Long userid) {
        try {
            Cache cache = cacheManager.getCache(CacheConstants.U_PAN_CACHE_NAME);
            cache.evict(UserConstants.USER_LOGIN_PREFIX + userid);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RPanBusinessException("退出登陆失败");
        }
    }

    /**
     * 校验用户名
     *
     * @param checkUserNameContext
     * @return
     */
    @Override
    public String checkUserName(CheckUserNameContext checkUserNameContext) {
        String question = baseMapper.selectQuestionByUsername(checkUserNameContext.getUsername());
        if (StringUtils.isBlank(question)) {
            throw new RPanBusinessException("没有此用户");
        }
        return question;
    }

    /**
     * 校验密保答案
     *
     * @param checkAnswerContext
     * @return
     */
    @Override
    public String checkAnswer(CheckAnswerContext checkAnswerContext) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", checkAnswerContext.getUsername());
        queryWrapper.eq("question", checkAnswerContext.getQuestion());
        queryWrapper.eq("answer", checkAnswerContext.getAnswer());
        int count = count(queryWrapper);

        if (count == 0) {
            throw new RPanBusinessException("密保答案错误");
        }
        return generateAndCheckAnswerToken(checkAnswerContext);
    }

    /**
     * 重置用户密码
     * 1.校验token是不是有效
     * 2.重置密码
     *
     * @param resetPasswordContext
     */
    @Override
    public void resetPassword(ResetPasswordContext resetPasswordContext) {
        checkForgetPasswordToken(resetPasswordContext);
        checkAndResetUserPassword(resetPasswordContext);
    }

    /**
     * 在线修改密码
     * 1.校验旧密码
     * 2.重置新密码
     * 3.退出当前的登陆状态
     *
     * @param changePasswordContext
     */
    @Override
    public void changePassword(ChangePasswordContext changePasswordContext) {
        checkOldPassword(changePasswordContext);
        doChangePassword(changePasswordContext);
        exitLoginStatus(changePasswordContext);
    }

    /**
     * 查询在线用户的基本信息
     * 1.查询用户的基本信息实体
     * 2.查询用户的根文件夹信息
     * 3.拼装Vo对象，返回
     *
     * @param userId
     * @return
     */
    @Override
    public UserInfoVo info(Long userId) {
        UPanUser entity = getById(userId);
        if (Objects.isNull(entity)) {
            throw new RPanBusinessException("用户信息不存在");
        }

        UPanUserFile uPanUserFile = getUserRootFileInfo(userId);
        if (Objects.isNull(uPanUserFile)) {
            throw new RPanBusinessException("用户根目录信息不存在");
        }
        return userConverter.assembleUserInfoVo(entity, uPanUserFile);
    }


    /******************************************************private*****************************************************/


    /**
     * 获取用户根文件夹信息实体
     *
     * @param userId
     * @return
     */
    private UPanUserFile getUserRootFileInfo(Long userId) {
        return userFileService.getUserRootFile(userId);
    }


    /**
     * 退出用户的登陆状态
     *
     * @param changePasswordContext
     */
    private void exitLoginStatus(ChangePasswordContext changePasswordContext) {
        logout(changePasswordContext.getUserId());
    }

    /**
     * 修改新密码
     *
     * @param changePasswordContext
     */
    private void doChangePassword(ChangePasswordContext changePasswordContext) {
        String newPassword = changePasswordContext.getNewPassword();
        UPanUser entity = changePasswordContext.getEntity();
        String salt = entity.getSalt();
        String encNewPassword = PasswordUtil.encryptPassword(salt, newPassword);
        entity.setPassword(encNewPassword);
        entity.setUpdateTime(new Date());
        if (!updateById(entity)) {
            throw new RPanBusinessException("修改密码失败");
        }
    }


    /**
     * 校验用户的旧密码
     * 该步骤会查询并且封装实体信息的上下文中
     *
     * @param changePasswordContext
     */
    private void checkOldPassword(ChangePasswordContext changePasswordContext) {
        Long userId = changePasswordContext.getUserId();
        String oldPassword = changePasswordContext.getOldPassword();

        UPanUser entity = getById(userId);
        if (Objects.isNull(entity)) {
            throw new RPanBusinessException("用户信息不存在");
        }
        changePasswordContext.setEntity(entity);
        String encOldPassword = PasswordUtil.encryptPassword(entity.getSalt(), oldPassword);
        String dbOldPassword = entity.getPassword();
        if (!Objects.equals(encOldPassword, dbOldPassword)) {
            throw new RPanBusinessException("旧密码错误");
        }

    }

    /**
     * 校验以及重置用户密码
     *
     * @param resetPasswordContext
     */
    private void checkAndResetUserPassword(ResetPasswordContext resetPasswordContext) {
        String username = resetPasswordContext.getUsername();
        String password = resetPasswordContext.getPassword();
        UPanUser entity = getUPanUserByUsername(username);
        if (Objects.isNull(entity)) {
            throw new RPanBusinessException("用户信息不存在");
        }
        String newDbPassword = PasswordUtil.encryptPassword(entity.getSalt(), password);
        entity.setPassword(newDbPassword);
        entity.setUpdateTime(new Date());
        if (!updateById(entity)) {
            throw new RPanBusinessException("重置密码失败");
        }
    }

    /**
     * 验证忘记密码的token是否有效
     *
     * @param resetPasswordContext
     */
    private void checkForgetPasswordToken(ResetPasswordContext resetPasswordContext) {
        String token = resetPasswordContext.getToken();

        Object value = JwtUtil.analyzeToken(token, UserConstants.FORGET_USER_NAME);
        if (Objects.isNull(value)) {
            throw new RPanBusinessException(ResponseCode.TOKEN_EXPIRE);
        }
        String tokenUsername = (String) value;
        if (!Objects.equals(tokenUsername, resetPasswordContext.getUsername())) {
            throw new RPanBusinessException("token错误");
        }
    }

    /**
     * 生成用户忘记密码-校验密码答案通过的临时token
     * token的失效时间为五分钟
     *
     * @param checkAnswerContext
     * @return
     */
    private String generateAndCheckAnswerToken(CheckAnswerContext checkAnswerContext) {
        String token = JwtUtil.generateToken(checkAnswerContext.getUsername(), UserConstants.FORGET_USER_NAME, checkAnswerContext.getUsername(), UserConstants.FIVE_MINUTES_LONG);
        return token;
    }

    /**
     * 生成并保存登陆之后的凭证
     *
     * @param userLoginContext
     */
    private void generateAndSaveAccessToken(UserLoginContext userLoginContext) {
        UPanUser entity = userLoginContext.getEntity();
        String accessToken = JwtUtil.generateToken(entity.getUsername(), UserConstants.LOGIN_USER_ID, entity.getUserId(), UserConstants.ONE_DAY_LONG);
        Cache cache = cacheManager.getCache(CacheConstants.U_PAN_CACHE_NAME);
        cache.put(UserConstants.USER_LOGIN_PREFIX + entity.getUserId(), accessToken);
        userLoginContext.setAccessToken(accessToken);
    }


    /**
     * 校验用户名和密码
     *
     * @param userLoginContext
     */
    private void checkLoginInfo(UserLoginContext userLoginContext) {
        String username = userLoginContext.getUsername();
        String password = userLoginContext.getPassword();

        UPanUser entity = getUPanUserByUsername(username);
        if (Objects.isNull(entity)) {
            throw new RPanBusinessException("用户名不存在");
        }

        String salt = entity.getSalt();
        String encPassword = PasswordUtil.encryptPassword(salt, password);
        String dbPassword = entity.getPassword();
        if (!Objects.equals(encPassword, dbPassword)) {
            throw new RPanBusinessException("用户名或密码错误");
        }
        userLoginContext.setEntity(entity);
    }

    /**
     * 通过用户名称获取用户实体信息
     *
     * @param username
     * @return
     */
    private UPanUser getUPanUserByUsername(String username) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return getOne(queryWrapper);
    }


    /**
     * 创建用户的根目录信息
     *
     * @param userRegisterContext
     */
    private void createUserRootFolder(UserRegisterContext userRegisterContext) {
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setParentId(FileConstants.TOP_PARENT_ID);
        createFolderContext.setUserId(userRegisterContext.getEntity().getUserId());
        createFolderContext.setFolderName(FileConstants.ALL_FILE_CN_STR);
        userFileService.createFolder(createFolderContext);
    }

    /**
     * 实现注册用户的业务
     * 需要捕获数据库的唯一索引冲突异常，来实现全局用户名称唯一
     *
     * @param userRegisterContext
     */
    private void doRegister(UserRegisterContext userRegisterContext) {
        UPanUser entity = userRegisterContext.getEntity();
        if (Objects.isNull(entity)) {
            throw new RPanBusinessException(ResponseCode.ERROR);
        }
        try {
            if (!save(entity)) {
                throw new RPanBusinessException("注册用户失败");
            }
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new RPanBusinessException("用户名已存在，请更换用户名");
        }
    }

    /**
     * 实体转换
     * 由上下文信息转换为用户实体，封装金上下文
     *
     * @param userRegisterContext
     * @return
     */
    private UPanUser assembleUserEntity(UserRegisterContext userRegisterContext) {
        UPanUser entity = userConverter.UserRegisterContext2UpanUser(userRegisterContext);
        String salt = PasswordUtil.getSalt(),
                dbPassword = PasswordUtil.encryptPassword(salt, userRegisterContext.getPassword());
        entity.setUserId(IdUtil.get());
        entity.setSalt(salt);
        entity.setPassword(dbPassword);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        userRegisterContext.setEntity(entity);
        return null;
    }
}




