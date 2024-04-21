package com.xiaou.pan.server.modules.user.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.xiaou.pan.web.serializer.IdEncryptSerializer;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoVO implements Serializable {

    private static final long serialVersionUID = 1226886631190798234L;

    private String username;

    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long rootFileId;


    private String rootFilename;
}
