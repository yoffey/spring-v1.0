package com.yoffey.spring.springv1.framework;

import com.yoffey.spring.springv1.framework.annotation.Autowired;
import com.yoffey.spring.springv1.framework.annotation.Controller;
import com.yoffey.spring.springv1.framework.annotation.Service;
import com.yoffey.spring.springv1.sample.controller.SampleController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description:  spring v1.0
 * 模拟spring ioc容器和依赖注入
 *
 * @author 宗永飞 (yongfei.zong@ucarinc.com)
 * @version 1.0
 * @date 2019-07-16 09:53
 */
public class DispatcherServlet extends HttpServlet {
    Properties configContext = new Properties();
    private Map<String, Object> beanMap = new ConcurrentHashMap<>();
    private List<String> classNames = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // ioc容器的初始化
        // 定位
        doLoadConfig(config);
        // 加载
        doScanner(configContext.getProperty("scanner.package"));
        // 注册
        doRegistry();
        // 自动注入
        doAutowired();
        // url mapping method
        initHandleMapping();
    }

    private void initHandleMapping() {
        String beanName = "sampleController";
        SampleController controller = (SampleController) beanMap.get(beanName);
        controller.sayHi();
    }

    private void doAutowired() {
        if (beanMap.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    String beanName = autowired.value();
                    if ("".equals(beanName.trim())) {
                        // 获取接口的全路径
                        beanName = field.getType().getName();
                    }
                    // 兼用jdk安全检查，提交反射效率，性能比不设置提高20倍左右
                    field.setAccessible(true);
                    try {
                        field.set(entry.getValue(), beanMap.get(beanName));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void doRegistry() {
        if (classNames.size() == 0) {
            return;
        }
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Controller.class)) {
                    String beanName = lowerFirstLetter(clazz.getSimpleName());
                    beanMap.put(beanName, clazz.newInstance());
                } else if (clazz.isAnnotationPresent(Service.class)) {
                    Service service = clazz.getAnnotation(Service.class);
                    String beanName = service.value();
                    if ("".equals(beanName.trim())) {
                        beanName = lowerFirstLetter(clazz.getSimpleName());
                    }

                    Object instance = clazz.newInstance();
                    beanMap.put(beanName, instance);
                    // 如果有实现接口，按接口类型注入

                    Class<?>[] interfaces = clazz.getInterfaces();
                    if (interfaces.length > 0) {
                        for (Class<?> anInterface : interfaces) {
                            beanMap.put(anInterface.getName(), instance);
                        }
                    }
                } else {
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String lowerFirstLetter(String simpleName) {
        char[] letters = simpleName.toCharArray();
        letters[0] += 32;
        return new String(letters);
    }

    private void doScanner(String packageName) {
        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());
        for (File file : classDir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(packageName + "." + file.getName());
            } else {
                classNames.add(packageName + "." + file.getName().replaceAll(".class", ""));
            }
        }
    }

    private void doLoadConfig(ServletConfig config) {
        String configPath = config.getInitParameter("contextConfigLocation").replace("classpath:", "");
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(configPath);
        try {
            configContext.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
