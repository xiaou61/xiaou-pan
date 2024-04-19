package com.xiaou.pan.server.modules.file.context;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 删除文件context
 */
@Data
public class DeleteFileContext implements Serializable {


    private static final long serialVersionUID = 8214583752370577630L;
    private String fileIds;

    /**
     * 要删除的文件id集合
     */
    private List<Long> fileIdList;

    /**
     * 当前的登陆用户id
     */
    private Long userId;
}
