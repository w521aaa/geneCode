package com.study.code.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.study.code.enums.DatabaseTypeEnum;
import com.study.code.paramVo.GeneParamsVo;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CommonUtils {

    public static String rootPath = "D:\\geneCode";

    public static String recordFileName = "geneRecord.txt";

    //当前时间格式化
    public static String getFormatNowDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    //递归查找所有目录下的文件
    public static void fileList(File file, List<File> retFileList){
        File[] aFiles = file.listFiles();
        for(File atemp : aFiles){
            if(atemp.isFile()){
                retFileList.add(atemp);
            }
            if(atemp.isDirectory()){
                fileList(atemp, retFileList);

                //如果是空目录 加入进来
                if(atemp.listFiles().length == 0) {
                    retFileList.add(atemp);
                }
            }
        }
    }

    //判断为空  返回 ""
    public static String isEmpty(String str) {
        return StringUtils.isEmpty(str) ? "" : str;
    }


    //将内容写入文件
    public static void writeToFileReader(String content, String absolutePath ,String fileName) {
        try {
            File outFile = new File(absolutePath + File.separator + fileName);
            FileWriter output;
            if(!outFile.exists()) {
                outFile.createNewFile();
                output = new FileWriter(outFile);
            } else {
                output = new FileWriter(outFile, true);
            }

            output.write(content);
            output.write("\r\n");
            output.flush();
            output.close();
        } catch (IOException e) {
            System.out.println("写入文件出错：" + fileName);
            e.printStackTrace();
        }

    }

    //从文件读取内容
    public static String readFromFile(String absolutePath ,String fileName) {
        StringBuilder retStr = new StringBuilder();

        try {
            File inFile = new File(absolutePath + File.separator + fileName);
            FileReader reader;
            if(!inFile.exists()) {
                return retStr.toString();
            } else {
                reader = new FileReader(inFile);
            }

            BufferedReader bReader = new BufferedReader(reader);


            String line;
            while((line = bReader.readLine()) != null) {
                retStr.append(line);
                retStr.append("\r\n");
            }
            bReader.close();
        } catch (IOException e) {
            System.out.println("读取文件出错：" + fileName);
            e.printStackTrace();
        }
        return retStr.toString();
    }

    //将path下的内容压缩  压缩到path路径下
    public static String compress(String path, String zipFileName, Boolean containDir, Boolean forceUpdate) throws Exception {
        String zipFilePath = path + File.separator + zipFileName;
        File zipFile = new File(zipFilePath);
        //删除
        if(forceUpdate && zipFile.exists()) {
            zipFile.delete();
        }

        //存在 返回
        if(!forceUpdate && zipFile.exists()) {
            return zipFilePath;
        }

        List<File> retFileList = new ArrayList<>(10);
        //拿到路径下所有文件
        CommonUtils.fileList(new File(path), retFileList);

        //打包成zip
        FileOutputStream out = new FileOutputStream(zipFile);
        ZipOutputStream zos = new ZipOutputStream(out);

        byte[] buf = new byte[1024];
        int len;
        for (File srcFile : retFileList) {
            //空目录加入进来
            if(containDir && srcFile.isDirectory()) {
                zos.putNextEntry(new ZipEntry(getRelativePath(path, srcFile)));
                zos.closeEntry();
                continue;
            }

            if(containDir) {
                zos.putNextEntry(new ZipEntry(getRelativePath(path, srcFile)));
            } else {
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
            }

            FileInputStream in = new FileInputStream(srcFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            zos.closeEntry();
            in.close();
        }
        zos.close();
        out.close();

        return zipFilePath;
    }

    //获取相对路径
    public static String getRelativePath(String path, File currentFile) {
        return currentFile.getPath().substring(path.length() + 1);
    }

    //生成代码
    public static void geneCode(String outDir, GeneParamsVo paramsVo) {
        //如果目录不存在 则创建
        File file = new File(outDir);
        if(!file.exists()) {
            file.mkdirs();
        }

        AutoGenerator mpg = new AutoGenerator();
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir(outDir);
        gc.setFileOverride(true);
        gc.setAuthor(isEmpty(paramsVo.getAuthor()));
        gc.setOpen(false);
        gc.setDateType(DateType.ONLY_DATE);
        gc.setBaseResultMap(true);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        if(paramsVo.getDatabaseType().equals(DatabaseTypeEnum.MYSQL.getKey())) {
            dsc.setUrl("jdbc:mysql://"+ paramsVo.getDataIPPort() +"/" + paramsVo.getDataName() + "?useSSL=true&serverTimezone=UTC");
            dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        }

        if(paramsVo.getDatabaseType().equals(DatabaseTypeEnum.POSTGRELSQL.getKey())) {
            dsc.setUrl("jdbc:postgresql://"+ paramsVo.getDataIPPort() +"/" + paramsVo.getDataName());
            //数据库 schema name  例如 PostgreSQL 可指定为 public
            dsc.setSchemaName("public");
            dsc.setDriverName("org.postgresql.Driver");
        }

        dsc.setUsername(paramsVo.getDataUsername());
        dsc.setPassword(paramsVo.getDataPassword());
        mpg.setDataSource(dsc);

        // 包配置
        //parent      父包名。如果为空，将下面子包名必须写全部， 否则就只需写子包名
        //moduleName  父包模块名

        PackageConfig pc = new PackageConfig();
//        pc.setModuleName(isEmpty(paramsVo.getPackageModuleName()));
        pc.setParent(isEmpty(paramsVo.getPackageParent()));
        mpg.setPackageInfo(pc);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();
        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.backEndDataVM, 会根据使用的模板引擎自动识别
        templateConfig.setEntity("templates/backEndDataVM/entity.java.my");
        templateConfig.setController("templates/backEndDataVM/controller.java.my");
        templateConfig.setService("templates/backEndDataVM/service.java.my");
        templateConfig.setServiceImpl("templates/backEndDataVM/serviceImpl.java.my");
        templateConfig.setMapper("templates/backEndDataVM/mapper.java.my");
        templateConfig.setXml("/templates/backEndDataVM/mapper.xml.my");
        mpg.setTemplate(templateConfig);

        StrategyConfig strategy = new StrategyConfig();
//        strategy.setNameConvert(new INameConvert() {
//            @Override
//            public String entityNameConvert(TableInfo tableInfo) {
//                String tableName = NamingStrategy.removePrefix(tableInfo.getName(), isEmpty(paramsVo.getDataTableRemovePrefix()).split(","));
//                tableName = tableName.substring(0, 1).toUpperCase() + tableName.substring(1);
//                return NamingStrategy.capitalFirst(NamingStrategy.underlineToCamel(tableName));
//            }
//
//            @Override
//            public String propertyNameConvert(TableField field) {
//                return NamingStrategy.capitalFirst(NamingStrategy.removePrefixAndCamel(field.getName(), isEmpty(paramsVo.getTableFieldRemovePrefix()).split(",")));
//            }
//        });
        strategy.setTablePrefix(isEmpty(paramsVo.getDataTableRemovePrefix()));
        strategy.setFieldPrefix(isEmpty(paramsVo.getTableFieldRemovePrefix()));
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setInclude(paramsVo.getDataTableName().split(","));
        strategy.setEntityLombokModel(paramsVo.getLombokModel());
        strategy.setRestControllerStyle(true);
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setEntityTableFieldAnnotationEnable(true);
        strategy.setLogicDeleteFieldName(isEmpty(paramsVo.getLogicDelete()));
        mpg.setStrategy(strategy);

        mpg.setTemplateEngine(new VelocityTemplateEngine());
        mpg.execute();
    }

    //生成项目
    public static void geneProjectCode(GeneParamsVo paramsVo) {
        //输出路径
        String rootOutDir = CommonUtils.rootPath + File.separator + paramsVo.getKey();

        String javaOutDir = rootOutDir + File.separator + "src/main/java";
        String tempPath = paramsVo.getPackageParent().replace(".", "/");
        //如果目录不存在 则创建
        File javaFile = new File(javaOutDir + File.separator + tempPath);
        if(!javaFile.exists()) {
            javaFile.mkdirs();
        }

        String resOutDir = rootOutDir + File.separator + "src/main/resources";
        //如果目录不存在 则创建
        File resFile = new File(resOutDir);
        if(!resFile.exists()) {
            resFile.mkdirs();
        }

        VelocityEngine velocityEngine = new VelocityEngine();
        Properties properties = new Properties();
        String projectPath = System.getProperty("user.dir");
        String basePath = projectPath + File.separator + "src/main/resources/templates/backEndProjectVM/";//这里需要这样写路径！！！
        // 设置模板的路径
        properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, basePath);
        velocityEngine.init(properties);

        //生成pom文件
        Template template = velocityEngine.getTemplate("pom.xml.my.vm");
        VelocityContext pomContext = new VelocityContext();
        pomContext.put( "gene", paramsVo);
        StringWriter pomSw = new StringWriter();
        template.merge(pomContext, pomSw);
        writeToFileReader(pomSw.toString(), rootOutDir, "pom.xml");

        //生成启动文件
        template = velocityEngine.getTemplate("application.java.my.vm");
        VelocityContext startContext = new VelocityContext();
        startContext.put( "gene", paramsVo);
        startContext.put("date", getFormatNowDate());
        StringWriter startSw = new StringWriter();
        template.merge(startContext, startSw);
        String tempFileName = NamingStrategy.capitalFirst(paramsVo.getPackageModuleName()) + "Application.java";
        writeToFileReader(startSw.toString(), javaFile.getPath(), tempFileName);

        //生成配置文件
        template = velocityEngine.getTemplate("application.yml.my.vm");
        VelocityContext appContext = new VelocityContext();
        appContext.put( "gene", paramsVo);
        StringWriter appSw = new StringWriter();
        template.merge(appContext, appSw);
        writeToFileReader(appSw.toString(), resFile.getPath(), "application.yml");
    }
}
