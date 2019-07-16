package com.yoffey.spring.springv1.sample.controller;

import com.yoffey.spring.springv1.framework.annotation.Autowired;
import com.yoffey.spring.springv1.framework.annotation.Controller;
import com.yoffey.spring.springv1.framework.annotation.RequestMapping;
import com.yoffey.spring.springv1.sample.service.SampleService;

/**
 * description:
 *
 * @author 宗永飞 (yongfei.zong@ucarinc.com)
 * @version 1.0
 * @date 2019-07-16 16:35
 */
@Controller
public class SampleController {
    @Autowired
    private SampleService sampleService;

    @RequestMapping(value = "/sayHi")
    public void sayHi() {
        sampleService.sayHi();
    }
}
