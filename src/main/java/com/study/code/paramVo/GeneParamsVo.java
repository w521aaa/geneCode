package com.study.code.paramVo;

import com.study.code.enums.DatabaseTypeEnum;
import lombok.Data;

@Data
public class GeneParamsVo {

    //key
    private String key;

    //作者
    private String author;

    //默认mysql
    private String databaseType = DatabaseTypeEnum.MYSQL.getKey();

    //schemaName
    private String schemaName;
    //数据库 库名
    private String dataName;
    //数据库 ip 端口
    private String dataIPPort;
    //数据库用户名
    private String dataUsername;
    //数据库密码
    private String dataPassword;

    //父包模块名
    private String packageModuleName;
    //父包名
    private String packageParent;

    //数据库 表名字  , 隔开
    private String dataTableName;
    //数据库表 忽略前缀
    private String dataTableRemovePrefix;
    //数据库字段 忽略前缀
    private String tableFieldRemovePrefix;

    //逻辑删除字段
    private String logicDelete;

    //lombok
    private Boolean lombokModel = false;

    //项目描述
    private String desc;

    //项目端口
    private String serverPort;
}
