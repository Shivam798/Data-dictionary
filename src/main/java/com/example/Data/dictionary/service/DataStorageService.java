package com.example.Data.dictionary.service;

import com.example.Data.dictionary.helper.PrimaryKeyGenerator;
import com.example.Data.dictionary.model.MetaData;
import com.example.Data.dictionary.repository.MetaDataRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class DataStorageService {

    @Autowired
    private MetaDataRepository metaDataRepository;
    @Autowired
    private PrimaryKeyGenerator primaryKeyGenerator;
    public List<MetaData> getAlldata() {
        return metaDataRepository.findAll();
    }

    public ResponseEntity<String> storeData(FileInputStream dataFile) throws IOException, NoSuchAlgorithmException {
        XSSFWorkbook workbook=new XSSFWorkbook(dataFile);
        XSSFSheet sheet=workbook.getSheetAt(0);
        Row row;
        for(int i=1; i<=sheet.getLastRowNum();i++){
            row=sheet.getRow(i);
            MetaData metaData=new MetaData();
            metaData.setProjectName(row.getCell(0).toString());
            metaData.setOrigination(row.getCell(1).toString());
            metaData.setScreenName(row.getCell(2).toString());
            metaData.setFieldLabel(row.getCell(3).toString());
            metaData.setVariableId(row.getCell(4).toString());
            metaData.setDataType(row.getCell(5).toString());
            metaData.setDataDescription(row.getCell(6).toString());
            metaData.setMandatory(row.getCell(7).toString());
            metaData.setSource(row.getCell(8).toString());
            metaData.setAPI(row.getCell(9).toString());
            metaData.setOperation(row.getCell(10).toString());
            metaData.setPrimaryKey(primaryKeyGenerator.generatePrimaryKey(List.of(metaData.getSource(),metaData.getProjectName(),metaData.getVariableId())));
            metaDataRepository.save(metaData);
        }
        return new ResponseEntity<>("All data uploaded", HttpStatus.OK);
    }

    public ResponseEntity<Object> updateSingleEntity(MetaData updatedEntity) {
        metaDataRepository.save(updatedEntity);
        return new ResponseEntity<>("Updated Successfully",HttpStatus.ACCEPTED);
    }

    public ResponseEntity<Object> deleteEntity(String dataId) {
        MetaData dataEntry=metaDataRepository.findByPrimaryKey(dataId);
        metaDataRepository.delete(dataEntry);
        return new ResponseEntity<>("Deleted Successfully",HttpStatus.ACCEPTED);
    }
}
