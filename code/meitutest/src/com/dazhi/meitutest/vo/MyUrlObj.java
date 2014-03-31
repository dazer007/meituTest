package com.dazhi.meitutest.vo;

import java.io.Serializable;

/**
 * 封装本地访问服务器的参数对象
 */
public class MyUrlObj implements Serializable {
    private String requestURL; // 请求服务器的URL
    private String formData; // 请求的参数
    private String resultType; // 服务器返回数据的类型
    private String appName; // 该请求对应的app子应用名称
    private String describe; // 描述

    public static final String RESULT_TYPE_JSON = "json";
    public static final String RESULT_TYPE_IMAGE_URI = "imgUrl";

    public MyUrlObj() {
    }

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public String getFormData() {
        return formData;
    }

    public void setFormData(String formData) {
        this.formData = formData;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    @Override
    public String toString() {
        return "MyUrlObj{" +
                "requestURL='" + requestURL + '\'' +
                ", formData='" + formData + '\'' +
                ", resultType='" + resultType + '\'' +
                ", appName='" + appName + '\'' +
                ", describe='" + describe + '\'' +
                '}';
    }
}
