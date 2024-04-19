package com.xiaou.pan.server.modules.user.controller;


import com.xiaou.pan.core.response.R;
import com.xiaou.pan.core.utils.IdUtil;
import com.xiaou.pan.server.common.annotation.LoginIgnore;
import com.xiaou.pan.server.common.utils.UserIdUtil;
import com.xiaou.pan.server.modules.user.context.*;
import com.xiaou.pan.server.modules.user.converter.UserConverter;
import com.xiaou.pan.server.modules.user.po.*;
import com.xiaou.pan.server.modules.user.service.IUserService;
import com.xiaou.pan.server.modules.user.vo.UserInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 该类是用户模块的controller层
 */
@RestController
@Slf4j
@RequestMapping("user")

public class UserController {
    @Resource
    private IUserService userService;

    @Resource
    private UserConverter userConverter;


    /**
     * 用户注册接口
     *
     * @param userRegisterPo
     * @return
     */
    @LoginIgnore
    @PostMapping("register")
    public R register(@Validated @RequestBody UserRegisterPo userRegisterPo) {
        UserRegisterContext userRegisterContext = userConverter.userRegisterPo2UserRegisterContext(userRegisterPo);
        Long userId = userService.register(userRegisterContext);
        return R.data(IdUtil.encrypt(userId));
    }

    /**
     * 用户登陆接口
     *
     * @param userLoginPo
     * @return
     */
    @LoginIgnore
    @PostMapping("login")
    public R login(@Validated @RequestBody UserLoginPo userLoginPo) {
        UserLoginContext userLoginContext = userConverter.userLoginPO2UserLoginContext(userLoginPo);
        String accessToken = userService.login(userLoginContext);
        return R.data(accessToken);
    }

    /**
     * 用户登出接口
     *
     * @return
     */
    @PostMapping("logout")
    public R logout() {
        userService.logout(UserIdUtil.get());
        return R.success();
    }


    /**
     * 用户忘记密码-校验用户名
     *
     * @param checkUserNamePo
     * @return
     */
    @LoginIgnore
    @PostMapping("username/check")
    public R checkUserName(@Validated @RequestBody CheckUserNamePo checkUserNamePo) {
        CheckUserNameContext checkUserNameContext = userConverter.CheckUserNamePo2CheckUserNameContext(checkUserNamePo);
        String question = userService.checkUserName(checkUserNameContext);
        return R.data(question);
    }


    /**
     * 用户忘记密码-校验密保答案
     *
     * @param checkAnswerPo
     * @return
     */
    @LoginIgnore
    @PostMapping("answer/check")
    public R checkAnswer(@Validated @RequestBody CheckAnswerPo checkAnswerPo) {
        CheckAnswerContext checkAnswerContext = userConverter.CheckAnswerPo2CheckAnswerContext(checkAnswerPo);
        String token = userService.checkAnswer(checkAnswerContext);
        return R.data(token);
    }

    /**
     * 重置密码
     *
     * @param resetPasswordPo
     * @return
     */
    @LoginIgnore
    @PostMapping("password/reset")
    public R resetPassword(@Validated @RequestBody ResetPasswordPo resetPasswordPo) {
        ResetPasswordContext resetPasswordContext = userConverter.ResetPasswordPo2ResetPasswordContext(resetPasswordPo);
        userService.resetPassword(resetPasswordContext);
        return R.success();
    }

    /**
     * 用户在线修改密码
     */
    @PostMapping("password/change")
    public R changePassword(@Validated @RequestBody ChangePasswordPo changePasswordPo) {
        ChangePasswordContext changePasswordContext = userConverter.ChangePasswordPo2ChangePasswordContext(changePasswordPo);
        changePasswordContext.setUserId(UserIdUtil.get());
        userService.changePassword(changePasswordContext);
        return R.success();
    }

    /**
     * 查询用户信息
     *
     * @return
     */
    @GetMapping("/")
    public R<UserInfoVo> info() {
        UserInfoVo userInfoVo = userService.info(UserIdUtil.get());
        return R.data(userInfoVo);
    }

}
