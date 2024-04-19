package com.xiaou.pan.schedule;


import com.xiaou.pan.core.exception.RPanBusinessException;
import com.xiaou.pan.core.exception.RPanFrameworkException;
import com.xiaou.pan.core.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 定时任务管理器
 * 1.创建并启动一个定时任务
 * 2.停止一个定时任务
 * 3.更新一个定时任务
 */
@Component
@Slf4j
public class ScheduleManager {
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    /**
     * 内部正在执行的定时任务缓存
     */
    public Map<String, ScheduleTaskHolder> cache = new ConcurrentHashMap<>();

    /**
     * 启动一个定时任务
     *
     * @param scheduleTask 定时任务实现类
     * @param corn         定时任务的表达式
     * @return
     */
    public String startTask(ScheduleTask scheduleTask, String corn) {
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(scheduleTask, new CronTrigger(corn));
        String key = UUIDUtil.getUUID();
        ScheduleTaskHolder holder = new ScheduleTaskHolder(scheduleTask, scheduledFuture);
        cache.put(key, holder);
        log.info("{} 定时任务已启动,唯一标识为{}", scheduleTask.getName(), key);
        return key;
    }

    /**
     * 停止一个定时任务
     *
     * @param key 定时任务的唯一标识
     */
    public void stopTask(String key) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        ScheduleTaskHolder holder = cache.get(key);
        if (Objects.isNull(holder)) {
            return;
        }
        ScheduledFuture scheduledFuture = holder.getScheduledFuture();
        boolean cancel = scheduledFuture.cancel(true);
        if (cancel) {
            log.info("{} 定时任务已停止,唯一标识为{}", holder.getScheduleTask().getName(), key);
        } else {
            log.error("{} 定时任务停止失败,唯一标识为{}", holder.getScheduleTask().getName(), key);
        }
    }

    /**
     * 更新一个定时任务的执行时间
     *
     * @param key  定时任务的唯一标识
     * @param corn 定时任务的表达式
     * @return
     */
    public String changeTask(String key, String corn) {
        if (StringUtils.isAnyBlank(key, corn)) {
            throw new RPanFrameworkException("参数不能为空");
        }
        ScheduleTaskHolder holder = cache.get(key);
        if (Objects.isNull(holder)) {
            throw new RPanFrameworkException(key + " 定时任务不存在");
        }
        stopTask(key);
        return startTask(holder.getScheduleTask(), corn);
    }
}
