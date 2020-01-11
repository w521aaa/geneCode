package com.study.code.controller;

import com.study.code.paramVo.GeneParamsVo;
import com.study.code.utils.CommonUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/gene")
public class GeneGotoController {

    @GetMapping("")
    public String index() {
        return "index";
    }

    @GetMapping("/goto/code")
    public String geneCode() {
        return "geneCode";
    }

    @GetMapping("/goto/project")
    public String geneProject() {
        return "geneProject";
    }

    @GetMapping("/goto/list")
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



}
