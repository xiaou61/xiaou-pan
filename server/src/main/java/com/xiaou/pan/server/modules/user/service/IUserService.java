package com.xiaou.pan.server.modules.user.service;

import com.xiaou.pan.server.modules.user.context.*;
import com.xiaou.pan.server.modules.user.domain.UPanUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaou.pan.server.modules.user.vo.UserInfoVO;

/**
 * @author Lenovo
 * @description 针对表【u_pan_user(用户信息表)】的数据库操作Service
 * @createDate 2024-04-13 15:24:24
 */
public interface IUserService extends IService<UPanUser> {

    /**
     * 用户注册业务
     *
     * @param userRegisterContext
     * @return
     */
    Long register(UserRegisterContext userRegisterContext);

    /**
     * 用户登录业务
     *
     * @param userLoginContext
     * @return
     */
    String login(UserLoginContext userLoginContext);

    /**
     * 用户退出登陆
     */
    void logout(Long userId);

    /**
     * 用户忘记密码-校验用户名
     * @param checkUserNameContext
     * @return
     */
    String checkUserName(CheckUserNameContext checkUserNameContext);

    /**
     * 用户忘记密码-校验密保答案
     * @param checkAnswerContext
     * @return
     */
    String checkAnswer(CheckAnswerContext checkAnswerContext);

    /**
     * 重置用户密码
     * @param resetPasswordContext
     */
    void resetPassword(ResetPasswordContext resetPasswordContext);

    /**
     * 在线修改密码
     * @param changePasswordContext
     */
    void changePassword(ChangePasswordContext changePasswordContext);

    /**
     * 查询在线用户的基本信息
     * @param userId
     * @return
     */
    UserInfoVO info(Long userId);
}
