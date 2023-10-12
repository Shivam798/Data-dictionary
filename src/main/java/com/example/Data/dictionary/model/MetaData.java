package com.example.Data.dictionary.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Entity
@AllArgsConstructor
@Data public class MetaData {
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
}
