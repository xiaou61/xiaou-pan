package com.xiaou.pan.server.modules.file.context;

import lombok.Data;

import java.io.Serializable;

@Data
public class QueryFolderTreeContext implements Serializable {

    private static final long serialVersionUID = -144734478146864676L;
    private Long userId;
}
