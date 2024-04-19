package com.xiaou.pan.server.common.event.log;

import lombok.*;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ErrorLogEvent extends ApplicationEvent {

    private String errorMsg;
    private Long userId;

    public ErrorLogEvent(Object source, String errorMsg, Long userId) {
        super(source);
        this.errorMsg = errorMsg;
        this.userId = userId;
    }
}
