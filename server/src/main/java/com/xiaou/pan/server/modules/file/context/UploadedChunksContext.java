package com.xiaou.pan.server.modules.file.context;

import lombok.Data;

import java.io.Serializable;

@Data
public class UploadedChunksContext implements Serializable {

    private static final long serialVersionUID = 60333022939231340L;

    private String identifier;

    private Long userId;
}
