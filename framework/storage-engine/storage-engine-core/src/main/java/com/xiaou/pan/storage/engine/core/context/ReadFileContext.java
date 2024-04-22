package com.xiaou.pan.storage.engine.core.context;

import lombok.Data;

import java.io.OutputStream;
import java.io.Serializable;

@Data
public class ReadFileContext implements Serializable {

    private static final long serialVersionUID = 2669430817051230215L;

    private String realPath;

    private OutputStream outputStream;
}
