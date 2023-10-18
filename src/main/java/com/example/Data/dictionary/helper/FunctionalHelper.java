package com.example.Data.dictionary.helper;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FunctionalHelper {
    public boolean incomingHeaderMatchesStoredHeader(Row row, List<String> header){
        for(int i=0;i<header.size();i++){
            String cellValue="";
            Cell cell=row.getCell(i);
            if(cell!=null){
                cellValue=cell.getStringCellValue();
            }
            if(cellValue!=header.get(i)){
                return false;
            }
        }
        return true;
    }

}
