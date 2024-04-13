package com.xiaou.pan.server;

import com.xiaou.pan.core.constants.RPanConstants;
import com.xiaou.pan.core.response.R;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = RPanConstants.BASE_COMPONENT_SCAN_PATH)
@ServletComponentScan(basePackages = RPanConstants.BASE_COMPONENT_SCAN_PATH)
@RestController
public class UPanServerLauncher {
    public static void main(String[] args) {
        SpringApplication.run(UPanServerLauncher.class, args);
    }

    @GetMapping("hello")
    public R<String> hello(String name) {
        return R.success("hello" + name);
    }
}
