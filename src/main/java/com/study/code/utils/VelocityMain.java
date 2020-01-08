package com.study.code.utils;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.StringWriter;
import java.util.Properties;

public class VelocityMain {

    public static void main(String[] args) {
        VelocityEngine velocityEngine = new VelocityEngine();

        Properties properties = new Properties();
        String projectPath = System.getProperty("user.dir");
        String basePath = projectPath + File.separator + "src/main/java/com/study/code/utils";//这里需要这样写路径！！！
        // 设置模板的路径
        properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, basePath);
        velocityEngine.init(properties);

        Template template = velocityEngine.getTemplate("mytemplate.vm");

        VelocityContext context = new VelocityContext();
        context.put( "name", "aaaaaaaaaaaaaaaaaaaaaaaaaaa" );

        StringWriter sw = new StringWriter();
        template.merge(context, sw);
        System.out.println(sw);

    }

}
