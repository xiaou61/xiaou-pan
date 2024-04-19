package com.xiaou.pan.server.modules.file;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.google.common.collect.Lists;

import com.xiaou.pan.core.exception.RPanBusinessException;
import com.xiaou.pan.core.utils.IdUtil;
import com.xiaou.pan.server.UPanServerLauncher;
import com.xiaou.pan.server.modules.file.context.CreateFolderContext;
import com.xiaou.pan.server.modules.file.context.QueryFileListContext;
import com.xiaou.pan.server.modules.file.enums.DelFlagEnum;
import com.xiaou.pan.server.modules.file.service.IUserFileService;
import com.xiaou.pan.server.modules.file.vo.UPanUserFileVO;
import com.xiaou.pan.server.modules.user.context.UserLoginContext;
import com.xiaou.pan.server.modules.user.context.UserRegisterContext;
import com.xiaou.pan.server.modules.user.service.IUserService;
import com.xiaou.pan.server.modules.user.vo.UserInfoVo;
import lombok.AllArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 文件模块单元测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = UPanServerLauncher.class)
@Transactional
public class FileTest {

    private static final Logger log = LoggerFactory.getLogger(FileTest.class);
    @Autowired
    private IUserFileService iUserFileService;

    @Autowired
    private IUserService iUserService;


    /**
     * 测试用户查询文件列表成功
     */
    @Test
    public void testQueryUserFileListSuccess() {
        Long userId = register();
        UserInfoVo userInfoVO = info(userId);

        QueryFileListContext context = new QueryFileListContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFileTypeArray(null);
        context.setDelFlag(DelFlagEnum.NOT_DELETE.getCode());

        List<UPanUserFileVO> result = iUserFileService.getFileList(context);
        Assert.isTrue(CollectionUtils.isEmpty(result));
    }


    /**
     * 生成模拟的网络文件实体
     *
     * @return
     */
    private static MultipartFile genarateMultipartFile() {
        MultipartFile file = null;
        try {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < 1024 * 1024; i++) {
                stringBuffer.append("a");
            }
            file = new MockMultipartFile("file", "test.txt", "multipart/form-data", stringBuffer.toString().getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 用户注册
     *
     * @return 新用户的ID
     */
    private Long register() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = iUserService.register(context);
        Assert.isTrue(register.longValue() > 0L);
        return register;
    }

    /**
     * 查询登录用户的基本信息
     *
     * @param userId
     * @return
     */
    private UserInfoVo info(Long userId) {
        UserInfoVo userInfoVO = iUserService.info(userId);
        Assert.notNull(userInfoVO);
        return userInfoVO;
    }

    private final static String USERNAME = "imooc";
    private final static String PASSWORD = "123456789";
    private final static String QUESTION = "question";
    private final static String ANSWER = "answer";

    /**
     * 构建注册用户上下文信息
     *
     * @return
     */
    private UserRegisterContext createUserRegisterContext() {
        UserRegisterContext context = new UserRegisterContext();
        context.setUsername(USERNAME);
        context.setPassword(PASSWORD);
        context.setQuestion(QUESTION);
        context.setAnswer(ANSWER);
        return context;
    }

    /**
     * 构建用户登录上下文实体
     *
     * @return
     */
    private UserLoginContext createUserLoginContext() {
        UserLoginContext userLoginContext = new UserLoginContext();
        userLoginContext.setUsername(USERNAME);
        userLoginContext.setPassword(PASSWORD);
        return userLoginContext;
    }


}
