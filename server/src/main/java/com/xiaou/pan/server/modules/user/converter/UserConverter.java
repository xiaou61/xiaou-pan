package com.xiaou.pan.server.modules.user.converter;


import com.xiaou.pan.server.modules.file.domain.UPanUserFile;
import com.xiaou.pan.server.modules.user.context.*;
import com.xiaou.pan.server.modules.user.domain.UPanUser;
import com.xiaou.pan.server.modules.user.po.*;
import com.xiaou.pan.server.modules.user.vo.UserInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用户模块实体转换工具类
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    /**
     * 用户注册PO转换为用户注册上下文
     *
     * @param userRegisterPo
     * @return
     */
    UserRegisterContext userRegisterPo2UserRegisterContext(UserRegisterPo userRegisterPo);

    /**
     * 用户注册上下文转换为用户注册PO
     *
     * @param userRegisterContext
     * @return
     */
    @Mapping(target = "password", ignore = true)
    UPanUser UserRegisterContext2UpanUser(UserRegisterContext userRegisterContext);

    /**
     * 用户登录PO转换为用户登录上下文
     *
     * @param userLoginPo
     * @return
     */
    UserLoginContext userLoginPO2UserLoginContext(UserLoginPo userLoginPo);

    /**
     * 用户名校验上下文转换为用户名校验PO
     *
     * @param checkUserNamePo
     * @return
     */
    CheckUserNameContext CheckUserNamePo2CheckUserNameContext(CheckUserNamePo checkUserNamePo);

    /**
     * CheckAnswerPo转CheckAnswerContext
     *
     * @param checkAnswerPo
     * @return
     */
    CheckAnswerContext CheckAnswerPo2CheckAnswerContext(CheckAnswerPo checkAnswerPo);


    ResetPasswordContext ResetPasswordPo2ResetPasswordContext(ResetPasswordPo resetPasswordPo);

    ChangePasswordContext ChangePasswordPo2ChangePasswordContext(ChangePasswordPo changePasswordPo);


    /**
     * 拼装用户基本信息返回实体
     * @param uPanUser
     * @param uPanUserFile
     * @return
     */
    @Mapping(source = "uPanUser.username", target = "username")
    @Mapping(source = "uPanUserFile.fileId", target = "rootFileId")
    @Mapping(source = "uPanUserFile.fileName", target = "rootFilename")
    UserInfoVO assembleUserInfoVo(UPanUser uPanUser, UPanUserFile uPanUserFile);
}
