package com.xiaou.pan.server;

import com.xiaou.pan.core.constants.RPanConstants;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = RPanConstants.BASE_COMPONENT_SCAN_PATH)
@ServletComponentScan(basePackages = RPanConstants.BASE_COMPONENT_SCAN_PATH)
@EnableTransactionManagement
@MapperScan(RPanConstants.BASE_COMPONENT_SCAN_PATH + ".server.modules.**.mapper")
public class UPanServerLauncher {
    public static void main(String[] args) {
        SpringApplication.run(UPanServerLauncher.class, args);
    }
}
