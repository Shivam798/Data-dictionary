package com.example.Data.dictionary.service;

import com.example.Data.dictionary.POJO.SingleColumnResponse;
import com.example.Data.dictionary.constants.ColumnValueOptions;
import com.example.Data.dictionary.constants.Header;
import com.example.Data.dictionary.helper.ExcelHelper;
import com.example.Data.dictionary.helper.FunctionalHelper;
import com.example.Data.dictionary.helper.PrimaryKeyGenerator;
import com.example.Data.dictionary.model.MetaDataModel;
import com.example.Data.dictionary.repository.MetaDataRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class DataStorageService {

    @Autowired
    private MetaDataRepository metaDataRepository;
    @Autowired
    private PrimaryKeyGenerator primaryKeyGenerator;
    @Autowired
    private ExcelHelper excelHelper;
    @Autowired
    FunctionalHelper functionalHelper;

    public List<MetaDataModel> getAllData() {
        return metaDataRepository.findAll();
    }

    public List<SingleColumnResponse> getHeaderData() {
        List<SingleColumnResponse> headerData=new ArrayList<>();
        for (int i=0; i< ColumnValueOptions.headerToHeaderOption.size();i++){
            SingleColumnResponse columnResponse=new SingleColumnResponse();
            columnResponse.setField(Header.ColumnsForRow.get(i));
            columnResponse.setEditable(true);
            columnResponse.setHeaderName(Header.header.get(0));
            columnResponse.setWidth(150);
            if(!ColumnValueOptions.headerToHeaderOption.get(i).isEmpty()){
                columnResponse.setType("singleSelect");
                columnResponse.setValueOptions(ColumnValueOptions.headerToHeaderOption.get(i));
            }else{
                columnResponse.setType("string");
                columnResponse.setValueOptions(null);
            }
            headerData.add(columnResponse);
        }
        return headerData;
    }

    public Map<String,Object> storeData(MultipartFile file) {
        Map<String ,Object> outputMap=new HashMap<>();
        if (file.isEmpty()) {
            outputMap.put("message","File is empty");
            return outputMap;
        }
        if (!excelHelper.checkExcelFormat(file)){
            outputMap.put("message","File is not of excel type");
            return outputMap;
        }
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        XSSFSheet sheet=workbook.getSheetAt(0);
        if(!functionalHelper.incomingHeaderMatchesStoredHeader(sheet.getRow(0),Header.header)){
            outputMap.put("message","Your header is not in defined form, please download template to define data");
            return outputMap;
        }
        Row row;
        List<Map<Integer,String>> rowsNotAdded=new ArrayList<>();
        for(int i=1; i<=sheet.getLastRowNum();i++){
            row=sheet.getRow(i);
            List<String> status=excelHelper.ConvertRowToJSONObjectAndSave(row);
            if(Objects.equals(status.get(0), "0")){
                Map<Integer,String> rowOutputStatus=new HashMap<>();
                rowOutputStatus.put(i,status.get(1));
                rowsNotAdded.add(rowOutputStatus);
            }
        }
        if(rowsNotAdded.size()>0){
            outputMap.put("message","Partial Data uploaded ");
            outputMap.put("unUploaded rows",rowsNotAdded);
            return outputMap;
        }
        outputMap.put("message","Data uploaded successfully");
        outputMap.put("unUploaded rows",null);
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
                metaDataRepository.deleteById(id);
                updatedEntity.setId(newPrimaryKey);
                metaDataRepository.save(updatedEntity);
            }
            outputMap.put("msg","Successfully uploaded");
            outputMap.put("data",updatedEntity);
            return outputMap;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
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

    public InputStream getTemplate() {
        ByteArrayInputStream in= null;
        List<MetaDataModel> dummyList=new ArrayList<>();
        try {
            in = excelHelper.getExcelSheet(dummyList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return in;
    }
}
