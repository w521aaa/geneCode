package com.study.code;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.FileType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneratorMain {
    public static void main(String[] args) {
        AutoGenerator mpg = new AutoGenerator();
        // 全局配置
        //outputDir        生成文件的输出目录   D 盘根目录
        //fileOverride     是否覆盖已有文件    false
        //open             是否打开输出目录    true
        //enableCache      是否在xml中添加二级缓存配置    false
        //baseColumnList   开启 baseColumnList   false
        //baseResultMap    开启 BaseResultMap    false
        //author           开发人员    null
        //swagger2         开启 swagger2 模式    false
        //dateType         时间类型对应策略        TIME_PACK (ONLY_DATE,SQL_PACK)
        //entityName       实体命名方式            默认值：null 例如：%sEntity 生成 UserEntity
        //mapperName       mapper 命名方式        默认值：null 例如：%sDao 生成 UserDao
        //xmlName          Mapper xml 命名方式    默认值：null 例如：%sDao 生成 UserDao.xml
        //serviceName      service 命名方式       默认值：null 例如：%sBusiness 生成 UserBusiness
        //serviceImplName  service impl 命名方式  默认值：null 例如：%sBusinessImpl 生成 UserBusinessImpl
        //controllerName   controller 命名方式    默认值：null 例如：%sAction 生成 UserAction
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor("");
        gc.setOpen(false);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:postgresql://localhost:5432/xxxxxxx");
        //数据库 schema name  例如 PostgreSQL 可指定为 public
         dsc.setSchemaName("public");
        dsc.setDriverName("org.postgresql.Driver");
        dsc.setUsername("xxxxxxx");
        dsc.setPassword("xxxxxxx");
        mpg.setDataSource(dsc);

        // 包配置
        //parent      父包名。如果为空，将下面子包名必须写全部， 否则就只需写子包名
        //moduleName  父包模块名

        PackageConfig pc = new PackageConfig();
        pc.setModuleName("generator");
        pc.setParent("com.example");
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                //自定义属性注入:abc
                //在.vm模板中，通过${cfg.abc}获取属性
                Map<String, Object> map = new HashMap<>();
                map.put("abc", this.getConfig().getGlobalConfig().getAuthor() + "-mp");
                this.setMap(map);
            }
        };

        // 如果模板引擎是 velocity
        String templatePath = "/templates/vm/mapper.xml.my.vm";

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出  指定模板文件、输出文件达到自定义文件生成目的
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/resources/mapper/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });

        //自定义判断是否创建文件 该配置用于判断某个类是否需要覆盖创建
        cfg.setFileCreate(new IFileCreate() {
            @Override
            public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
                //因为将xml  文件放到了 resources/mapper 下  就不需要再mapper下有了  但是文件夹会生成
                if(filePath.contains("mapper\\xml")) {
                    return false;
                }

                // 判断自定义文件夹是否需要创建
                checkDir(filePath);
                //返回true  覆盖文件  否则 不覆盖
                return true;
            }
        });

        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();
        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        templateConfig.setEntity("templates/vm/entity.java.my");
        templateConfig.setController("templates/vm/controller.java.my");
        templateConfig.setService("templates/vm/service.java.my");
        templateConfig.setServiceImpl("templates/vm/serviceImpl.java.my");
        templateConfig.setMapper("templates/vm/mapper.java.my");
        templateConfig.setXml("/templates/vm/mapper.xml.my");
        mpg.setTemplate(templateConfig);

        // 策略配置
        //isCapitalMode   是否大写命名
        //skipView        是否跳过视图
        //naming          数据库表映射到实体的命名策略
        //columnNaming    数据库表字段映射到实体的命名策略, 未指定按照 naming 执行
        //tablePrefix     表前缀
        //fieldPrefix     字段前缀
        //superEntityClass       自定义继承的Entity类全称，带包名  service controller  mapper 类似
        //superEntityColumns     自定义基础的Entity类，公共字段    service controller  mapper 类似
        //include         需要包含的表名，允许正则表达式（与exclude二选一配置）
        //exclude         需要排除的表名，允许正则表达式
        //entityLombokModel     【实体】是否为lombok模型（默认 false）
        //entityColumnConstant  【实体】是否生成字段常量（默认 false）
        //entityBooleanColumnRemoveIsPrefix   Boolean类型字段是否移除is前缀（默认 false）
        //restControllerStyle   生成 @RestController 控制器
        //controllerMappingHyphenStyle  驼峰转连字符
        //entityTableFieldAnnotationEnable  是否生成实体时，生成字段注解
        //logicDeleteFieldName  逻辑删除属性名称

        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.no_change);
        strategy.setColumnNaming(NamingStrategy.no_change);
        strategy.setSuperEntityClass("");
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        strategy.setInclude("t_user".split(","));
        strategy.setControllerMappingHyphenStyle(true);
//        strategy.setTablePrefix(pc.getModuleName() + "_");
        mpg.setStrategy(strategy);


        mpg.setTemplateEngine(new VelocityTemplateEngine());
        mpg.execute();
    }
}
