package com.example.Data.dictionary.service;

import com.example.Data.dictionary.helper.ExcelHelper;
import com.example.Data.dictionary.helper.PrimaryKeyGenerator;
import com.example.Data.dictionary.model.MetaDataModel;
import com.example.Data.dictionary.repository.MetaDataRepository;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.Phaser;

@Service
public class DataStorageService {

    @Autowired
    private MetaDataRepository metaDataRepository;
    @Autowired
    private PrimaryKeyGenerator primaryKeyGenerator;

    @Autowired
    private ExcelHelper excelHelper;
    public List<MetaDataModel> getAllData() {
        return metaDataRepository.findAll();
    }

    public Map<String,Object> storeData(FileInputStream dataFile) {

        // Read the dataFile into a byte array
        byte[] fileBytes = new byte[0];
        try {
            fileBytes = IOUtils.toByteArray(dataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Wrap the byte array in a ByteArrayInputStream
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileBytes);

        XSSFWorkbook workbook= null;
        try {
            workbook = new XSSFWorkbook(byteArrayInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        XSSFSheet sheet=workbook.getSheetAt(0);
        Row row;
        List<Map<Integer,String>> rowsNotAdded=new ArrayList<>();
        for(int i=1; i<=sheet.getLastRowNum();i++){
            row=sheet.getRow(i);
             List<String> status=excelHelper.ConvertRowToJSONObjectAndSave(row);
            if(status.get(0)=="0"){
                Map<Integer,String> rowOutputStatus=new HashMap<>();
                rowOutputStatus.put(i,status.get(1));
                rowsNotAdded.add(rowOutputStatus);
            }
        }
        Map<String ,Object> outputMap=new HashMap<>();
        outputMap.put("msg","Data uploaded successfully");
        outputMap.put("unUploaded rows",rowsNotAdded);
        return outputMap;
    }

    public Map<String,Object> updateSingleEntity(String id, MetaDataModel updatedEntity) {
        Map<String, Object> outputMap = new HashMap<>();
        boolean exists=metaDataRepository.existsById(id);
        if(!exists){
            outputMap.put("msg","Data with id "+id+" does not exists");
            return outputMap;
        }
        try {
            String newPrimaryKey=primaryKeyGenerator.generatePrimaryKey(Arrays.asList(updatedEntity.getOrigination(),updatedEntity.getProjectName(),updatedEntity.getVariableId()));
            if(id.equals(newPrimaryKey)){
                metaDataRepository.save(updatedEntity);
            }else{
                MetaDataModel dataEntry=metaDataRepository.findMetaDataModelById(id);
                metaDataRepository.delete(dataEntry);
                updatedEntity.setId(newPrimaryKey);
                metaDataRepository.save(updatedEntity);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        outputMap.put("msg","Successfully uploaded");
        outputMap.put("data",updatedEntity);
        return outputMap;
    }

    public Map<String,Object> deleteEntity(String dataId) {
        Map<String, Object> outputMap = new HashMap<>();
        boolean exists=metaDataRepository.existsById(dataId);
        if(!exists){
            outputMap.put("msg","Data with id "+dataId+" does not exists");
            return outputMap;
        }
        MetaDataModel dataEntry=metaDataRepository.findMetaDataModelById(dataId);
        outputMap.put("msg","successfully deleted");
        outputMap.put("data",dataEntry);
        metaDataRepository.delete(dataEntry);
        return outputMap;
    }

    public InputStream getExcelData() {
        List<MetaDataModel> dataList=metaDataRepository.findAll();
        ByteArrayInputStream in= null;
        try {
            in = excelHelper.getExcelSheet(dataList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return in;
    }
}
