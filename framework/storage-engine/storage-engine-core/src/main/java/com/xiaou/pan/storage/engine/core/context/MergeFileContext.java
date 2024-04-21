package com.xiaou.pan.storage.engine.core.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MergeFileContext implements Serializable {

    private static final long serialVersionUID = -6819223435348652027L;

    private String filename;

    private String identifier;

    private Long userId;

    private List<String> realPathList;

    private String realPath;
}
