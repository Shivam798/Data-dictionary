package com.example.Data.dictionary.controller;

import com.example.Data.dictionary.model.MetaData;
import com.example.Data.dictionary.service.DataStorageService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("api/v1")
public class DataController {
    @Autowired
    private DataStorageService dataStorageService;

    @GetMapping("get/data")
    public List<MetaData> getData(){
        return dataStorageService.getAlldata();
    }
    @PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> UploadDateSheet(@RequestParam("datafile") MultipartFile dataFile) throws IOException {
        if (dataFile.isEmpty()) {
            return new ResponseEntity<>("File is empty", HttpStatus.BAD_REQUEST);
        }

        try {
            // Convert MultipartFile to a temporary File
            File tempFile = File.createTempFile("temp", null);
            dataFile.transferTo(tempFile);

            // Convert the temporary File to FileInputStream
            FileInputStream fileInputStream = new FileInputStream(tempFile);

            // Implement your logic to handle the file using the FileInputStream
            // Example: processFile(fileInputStream);
            System.out.println("File is passed to service");

            ResponseEntity result= dataStorageService.storeData(fileInputStream);
            // Clean up the temporary file if needed
            tempFile.delete();
            return result;

        }catch (IOException e) {
            return new ResponseEntity<>("Error uploading or processing the file", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    @PutMapping("updateSingleEntity/")
    public ResponseEntity<Object> updateSingleEntity(@RequestBody MetaData updatedEntity){
        return dataStorageService.updateSingleEntity(updatedEntity);
    }
    @DeleteMapping("deleteEntity/{dataId}")
    public ResponseEntity<Object> deleteEntity(@PathVariable("dataId") String dataId){
        return dataStorageService.deleteEntity(dataId);
    }

//    @PostMapping("/download-excel")
//    public ResponseEntity<Resource> downloadExcel(
//            @RequestBody List<MetaData> dataList) throws IOException {
//
//        return dataStorageService.getExcelSheet(dataList);
//    }
}
