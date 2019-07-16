package com.yoffey.spring.springv1.sample.service.impl;

import com.yoffey.spring.springv1.framework.annotation.Service;
import com.yoffey.spring.springv1.sample.service.SampleService;

/**
 * description: 暂时不支持在接口上声明@Service
 *
 * @author 宗永飞 (yongfei.zong@ucarinc.com)
 * @version 1.0
 * @date 2019-07-16 16:36
 */
@Service(value = "sampleService")
public class SampleServiceImpl implements SampleService {
    @Override
    public void sayHi() {
        System.out.println("hi, yoffey");
    }
}
