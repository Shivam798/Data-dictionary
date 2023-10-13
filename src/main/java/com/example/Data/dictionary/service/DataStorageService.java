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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataStorageService {

    @Autowired
    private MetaDataRepository metaDataRepository;
    @Autowired
    private PrimaryKeyGenerator primaryKeyGenerator;

    @Autowired
    private ExcelHelper excelHelper;
    public List<MetaDataModel> getAlldata() {
        return metaDataRepository.findAll();
    }

    public ResponseEntity<Map<String,Object>> storeData(FileInputStream dataFile) throws IOException, NoSuchAlgorithmException {

        // Read the dataFile into a byte array
        byte[] fileBytes = IOUtils.toByteArray(dataFile);

        // Wrap the byte array in a ByteArrayInputStream
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileBytes);

        XSSFWorkbook workbook=new XSSFWorkbook(byteArrayInputStream);
        XSSFSheet sheet=workbook.getSheetAt(0);
        Row row;
        List<Integer> rowsNotAdded=new ArrayList<>();
        for(int i=1; i<=sheet.getLastRowNum();i++){
            row=sheet.getRow(i);
            Integer status=excelHelper.ConvertRowToJSONObjectAndSave(row);
            if(status==0){
                rowsNotAdded.add(i);
            }
        }
        Map<String ,Object> res=new HashMap<>();
        res.put("status","Data uploaded successfully");
        res.put("Rows that are not uploaded",rowsNotAdded);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    public ResponseEntity<Object> updateSingleEntity(MetaDataModel updatedEntity) {
        metaDataRepository.save(updatedEntity);
        return new ResponseEntity<>("Updated Successfully",HttpStatus.ACCEPTED);
    }

    public ResponseEntity<Object> deleteEntity(String dataId) {
        MetaDataModel dataEntry=metaDataRepository.findByPrimaryKey(dataId);
        metaDataRepository.delete(dataEntry);
        return new ResponseEntity<>("Deleted Successfully",HttpStatus.ACCEPTED);
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
