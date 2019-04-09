package com.evol.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BaseController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 测试nginx 配置执行超时时间
     *              proxy_read_timeout 8;  #后端服务超过8秒未返回，nginx则返回错误页面，可以自定义错误页面
     *             proxy_next_upstream http_502 http_504 error invalid_header;
     *
     * @return
     * @throws InterruptedException
     */
    @RequestMapping("/timeoutTest")
    @ResponseBody
    public String timeoutTest() throws InterruptedException{
        long timeout = 1000 * 8L;
        logger.info("开始线程等待，毫秒数：" + timeout);
        Thread.sleep(timeout);
        logger.info("线程等待结束");
        return "timeout:" + timeout;
    }

}
