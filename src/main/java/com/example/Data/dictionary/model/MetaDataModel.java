package com.example.Data.dictionary.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "dataDictionary")
@Data public class MetaDataModel {
    @Id
    private String primaryKey;
    private String projectName;
    private String origination;
    private String screenName;
    private String fieldLabel;
    private String variableId;
    private String dataType;
    private String dataDescription;
    private String mandatory;
    private String source;
    private String API;
    private String operation;
    private String category;
}