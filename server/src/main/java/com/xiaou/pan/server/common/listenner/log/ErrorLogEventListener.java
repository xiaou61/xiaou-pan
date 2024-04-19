package com.xiaou.pan.server.common.listenner.log;

import com.xiaou.pan.core.utils.IdUtil;
import com.xiaou.pan.server.common.event.log.ErrorLogEvent;
import com.xiaou.pan.server.modules.log.domain.UPanErrorLog;
import com.xiaou.pan.server.modules.log.service.IErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 系统错误日志监听器
 */
public class ErrorLogEventListener {


    @Resource
    private IErrorLogService errorLogService;


    /**
     * 监听系统错误日志事件，并保存到数据库中
     */
    @EventListener(ErrorLogEvent.class)
    public void saveErrorLog(ErrorLogEvent event) {
        UPanErrorLog record = new UPanErrorLog();
        record.setId(IdUtil.get());
        record.setLogContent(event.getErrorMsg());
        record.setLogStatus(0);
        record.setCreateUser(event.getUserId());
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        record.setUpdateUser(event.getUserId());
        errorLogService.save(record);
    }
}
