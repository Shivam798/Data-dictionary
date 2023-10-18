package com.example.Data.dictionary.POJO;

import lombok.Data;

import java.util.List;

@Data
public class SingleColumnResponse {
    String field;
    String headerName;
    Integer width;
    boolean editable;
    String type;
    List<String> valueOptions;
}
