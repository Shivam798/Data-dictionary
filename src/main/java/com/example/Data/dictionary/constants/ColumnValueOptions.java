package com.example.Data.dictionary.constants;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ColumnValueOptions {
    public static List<String> projectNameOptions= Arrays.asList("SCB","INCRED","BAOBAB","CARS24");
    public  static List<String> originationOptions= Arrays.asList("JOURNEY","PORTAL");
    public  static List<String> dataTypeOptions= Arrays.asList("STRING","INTEGER","BOOLEAN","DROPDOWN","JSON","BUTTON","HIDE","NUMBER");
    public  static List<String> mandatoryOptions= Arrays.asList("YES","NO");
    public  static List<String> sourceOptions= Arrays.asList("API","USER","systemGenerated","DERIVED");
    public  static List<String> screenNameOptions= Arrays.asList();
    public  static List<String> fieldLabelOptions= Arrays.asList();
    public  static List<String> variableIdOptions= Arrays.asList();
    public static  List<String> descriptionOptions= Arrays.asList();
    public  static List<String> APIOptions= Arrays.asList();
    public  static List<String> operationOptions= Arrays.asList();
    public  static List<String> categoryOptions= Arrays.asList();
    public  static List<String> creditOptions= Arrays.asList();
    public  static List<String> FIOptions= Arrays.asList();
    public  static List<String> PDOptions= Arrays.asList();
    public  static List<String> legalOptions= Arrays.asList();
    public  static List<String> technicalOptions= Arrays.asList();
    public  static List<String> RCUOptions= Arrays.asList();
    public  static List<String> setStageOptions= Arrays.asList();
    public  static List<String> useStageOptions= Arrays.asList();


    public static List<List<String>> headerToHeaderOption= Arrays.asList(projectNameOptions,
            originationOptions,screenNameOptions,fieldLabelOptions,
            variableIdOptions,dataTypeOptions,descriptionOptions,
            categoryOptions ,mandatoryOptions,sourceOptions,APIOptions,
            creditOptions,operationOptions,FIOptions,PDOptions,legalOptions,
            technicalOptions,RCUOptions,setStageOptions,useStageOptions);
}
