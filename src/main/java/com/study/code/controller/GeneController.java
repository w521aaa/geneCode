package com.study.code.controller;

import com.study.code.paramVo.GeneParamsVo;
import com.study.code.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@Controller
@RequestMapping("/gene")
public class GeneController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/code")
    public void geneCode(GeneParamsVo paramsVo, HttpServletResponse response) throws Exception {
        paramsVo.setKey(UUID.randomUUID().toString().replace("-", ""));
        //生成代码
        logger.info(paramsVo.toString());

        //输出路径
        String outDir = CommonUtils.rootPath + File.separator + paramsVo.getKey();
        CommonUtils.geneCode(outDir, paramsVo);

        //记录文件
        String content = paramsVo.getKey() + "&" + paramsVo.getAuthor() + "&" + paramsVo.getDataTableName();
        CommonUtils.writeToFileReader(content, CommonUtils.rootPath, CommonUtils.recordFileName);

        response.sendRedirect("/gene/goto/list");
    }

    @GetMapping("/project/code")
    public void geneProjectCode(GeneParamsVo paramsVo, HttpServletResponse response) throws Exception {
        paramsVo.setKey(UUID.randomUUID().toString().replace("-", ""));
        //生成代码
        logger.info(paramsVo.toString());

        //生成项目
        CommonUtils.geneProjectCode(paramsVo);

        if(!StringUtils.isEmpty(paramsVo.getDataTableName())) {
            //生成代码
            //输出路径
            String outDir = CommonUtils.rootPath + File.separator + paramsVo.getKey() + "/src/main/java";
            CommonUtils.geneCode(outDir, paramsVo);
        }

        //记录文件
        String content = paramsVo.getKey() + "&" + paramsVo.getAuthor() + "&" + paramsVo.getPackageModuleName();
        CommonUtils.writeToFileReader(content, CommonUtils.rootPath, CommonUtils.recordFileName);

        response.sendRedirect("/gene/goto/list");
    }

    @GetMapping("/code/download/{key}")
    public void download(@PathVariable("key") String key,
                         @RequestParam(defaultValue = "true") Boolean dir,
                         HttpServletResponse response) throws Exception {
        String path = CommonUtils.rootPath + File.separator + key;
        String zipName = key + ".zip";
        //根据 key 也就是目录 下载代码
        //如果zip 文件不存在 结束
        File file = new File(path);
        if(!file.exists()) {
            return;
        }

        //压缩文件
        String zipNamePath = CommonUtils.compress(path, zipName, dir, true);

        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/octet-stream");
        //3.设置content-disposition响应头控制浏览器以下载的形式打开文件
        response.addHeader("Content-Disposition","attachment;filename=" + zipName);
        //获取文件输入流
        InputStream in = new FileInputStream(zipNamePath);
        OutputStream outRes = response.getOutputStream();
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            //将缓冲区的数据输出到客户端浏览器
            outRes.write(buf, 0, len);
        }
        in.close();
        outRes.close();
    }
}
