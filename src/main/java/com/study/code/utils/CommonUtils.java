package com.study.code.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.study.code.enums.DatabaseTypeEnum;
import com.study.code.paramVo.GeneParamsVo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CommonUtils {

    public static String rootPath = "D:\\geneCode";

    public static String recordFileName = "geneRecord.txt";

    //递归查找所有目录下的文件
    public static void fileList(File file, List<File> retFileList){
        File[] aFiles = file.listFiles();
        for(File atemp : aFiles){
            if(atemp.isFile()){
                retFileList.add(atemp);
            }
            if(atemp.isDirectory()){
                fileList(atemp, retFileList);
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
    public static void geneCode(GeneParamsVo paramsVo) {
        //输出路径
        String outDir = CommonUtils.rootPath + File.separator + paramsVo.getKey();

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
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        templateConfig.setEntity("templates/vm/entity.java.my");
        templateConfig.setController("templates/vm/controller.java.my");
        templateConfig.setService("templates/vm/service.java.my");
        templateConfig.setServiceImpl("templates/vm/serviceImpl.java.my");
        templateConfig.setMapper("templates/vm/mapper.java.my");
        templateConfig.setXml("/templates/vm/mapper.xml.my");
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

}
