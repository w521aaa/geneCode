package com.study.code.enums;


public enum DatabaseTypeEnum {

    MYSQL("mysql"),
    POSTGRELSQL("postgrelSql");

    private String key;

    DatabaseTypeEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
