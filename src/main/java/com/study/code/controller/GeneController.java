package com.study.code.controller;

import com.study.code.paramVo.GeneParamsVo;
import com.study.code.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/gene")
public class GeneController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("")
    public String geneCode() {
        return "geneCode";
    }

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "") String key, Model model) {
        String content = CommonUtils.readFromFile(CommonUtils.rootPath, CommonUtils.recordFileName);
        content = content.replace("\r\n", "&");
        List<GeneParamsVo> paramsVoList = new ArrayList<>(10);
        String[] split = content.split("&");
        for(int i=split.length-1; i>0; i-=3) {
            GeneParamsVo vo = new GeneParamsVo();
            vo.setKey(split[i-2]);
            vo.setAuthor(split[i-1]);
            vo.setDataTableName(split[i]);
            paramsVoList.add(vo);
        }

        model.addAttribute("paramList", paramsVoList);
        return "list";
    }

    @GetMapping("/code")
    public void geneCode(GeneParamsVo paramsVo, HttpServletResponse response) throws Exception {
        paramsVo.setKey(UUID.randomUUID().toString().replace("-", ""));
        //生成代码
        logger.info(paramsVo.toString());

        CommonUtils.geneCode(paramsVo);

        //记录文件
        String content = paramsVo.getKey() + "&" + paramsVo.getAuthor() + "&" + paramsVo.getDataTableName();
        CommonUtils.writeToFileReader(content, CommonUtils.rootPath, CommonUtils.recordFileName);

        response.sendRedirect("/gene/code/download/" + paramsVo.getKey());
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
