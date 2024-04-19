package com.xiaou.pan.server.modules.file.mapper;

import com.xiaou.pan.server.modules.file.context.QueryFileListContext;
import com.xiaou.pan.server.modules.file.domain.UPanUserFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaou.pan.server.modules.file.vo.UPanUserFileVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author Lenovo
* @description 针对表【u_pan_user_file(用户文件信息表)】的数据库操作Mapper
* @createDate 2024-04-13 15:26:52
* @Entity com.xiaou.pan.server.modules.file.domain.UPanUserFile
*/
public interface UPanUserFileMapper extends BaseMapper<UPanUserFile> {

    /**
     * 查询用户的文件列表
     * @param context
     * @return
     */

    List<UPanUserFileVO> selectFileList(@Param("param") QueryFileListContext context);
}




