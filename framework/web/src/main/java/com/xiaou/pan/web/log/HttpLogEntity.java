package com.xiaou.pan.web.log;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.Map;

/**
 * http调用日志实体
 */
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@Slf4j
public class HttpLogEntity {

    /**
     * 请求资源
     */
    private String requestUri;

    /**
     * 被调方法
     */
    private String method;

    /**
     * 调用者地址
     */
    private String remoteAddr;

    /**
     * 调用的IP地址
     */
    private String ip;

    /**
     * 请求头
     */
    private Map<String, String> requestHeaders;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 响应状态
     */
    private Integer status;

    /**
     * 响应头
     */
    private Map<String, String> responseHeaders;

    /**
     * 响应数据
     */
    private String responseData;

    /**
     * 接口耗时
     */
    private String resolveTime;

    /**
     * 打印日志
     */
    public void print() {
        log.info("====================HTTP CALL START====================");
        log.info("callTime: {}", DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(new Date()));
        log.info("requestUri: {}", getRequestUri());
        log.info("method: {}", getMethod());
        log.info("remoteAddr: {}", getRemoteAddr());
        log.info("ip: {}", getIp());
        log.info("requestHeaders: {}", getRequestHeaders());
        log.info("requestParams: {}", getRequestParams());
        log.info("status: {}", getStatus());
        log.info("responseHeaders: {}", getResponseHeaders());
        log.info("responseData: {}", getResponseData());
        log.info("resolveTime: {}", getResolveTime());
        log.info("====================HTTP CALL FINISH====================");
    }

    @Override
    public String toString() {


        String stringBuilder = "====================HTTP CALL START====================" +
                "callTime: " +
                DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(new Date()) +
                System.lineSeparator() +
                "requestUri: " +
                getRequestUri() +
                System.lineSeparator() +
                "method: " +
                getMethod() +
                System.lineSeparator() +
                "remoteAddr: " +
                getRemoteAddr() +
                System.lineSeparator() +
                "ip: " +
                getIp() +
                System.lineSeparator() +
                "requestHeaders: " +
                getRequestHeaders() +
                System.lineSeparator() +
                "requestParams: " +
                getRequestParams() +
                System.lineSeparator() +
                "status: " +
                getStatus() +
                System.lineSeparator() +
                "responseHeaders: " +
                getResponseHeaders() +
                System.lineSeparator() +
                "responseData: " +
                getResponseData() +
                System.lineSeparator() +
                "resolveTime: " +
                getResolveTime() +
                System.lineSeparator() +
                "====================HTTP CALL FINISH====================";
        return stringBuilder;
    }

}
