package com.xiaou.pan.server.modules.file.vo;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.xiaou.pan.web.serializer.IdEncryptSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
/**
 * 文件夹树节点VO
 */
public class FolderTreeNodeVO implements Serializable {

    private static final long serialVersionUID = -2075569872641035619L;

    private String label;

    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long id;

    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long parentId;

    private List<FolderTreeNodeVO> children;

    public void print(){
        String jsonString = JSON.toJSONString(this);
        System.out.println(jsonString);
    }
}
