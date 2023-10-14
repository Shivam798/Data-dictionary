package com.example.Data.dictionary.controller;

import com.example.Data.dictionary.helper.ExcelHelper;
import com.example.Data.dictionary.model.MetaDataModel;
import com.example.Data.dictionary.service.DataStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    private ExcelHelper excelHelper;
    @GetMapping("get/data")
    public List<MetaDataModel> getData(){
        return dataStorageService.getAlldata();
    }
    @PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> UploadDateSheet(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return new ResponseEntity<>("File is empty", HttpStatus.BAD_REQUEST);
        }
        if (!excelHelper.checkExcelFormat(file)){
            return new ResponseEntity<>("File is not of excel type", HttpStatus.BAD_REQUEST);
        }
        try {
            // Convert MultipartFile to a temporary File
            File tempFile = File.createTempFile("temp", null);
            file.transferTo(tempFile);

            // Convert the temporary File to FileInputStream
            FileInputStream fileInputStream = new FileInputStream(tempFile);

            // Implement your logic to handle the file using the FileInputStream

            ResponseEntity result= dataStorageService.storeData(fileInputStream);
            // Clean up the temporary file if needed
            tempFile.delete();
            return result;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    @PutMapping("updateSingleEntity/")
    public ResponseEntity<Object> updateSingleEntity(@RequestBody MetaDataModel updatedEntity){
        return dataStorageService.updateSingleEntity(updatedEntity);
    }
    @DeleteMapping("deleteEntity/{dataId}")
    public ResponseEntity<Object> deleteEntity(@PathVariable("dataId") String dataId){
        return dataStorageService.deleteEntity(dataId);
    }

    @GetMapping("/downloadExcel")
    public ResponseEntity<Resource> downloadExcel() throws IOException {
        String filename = "dataDictionary.xlsx";
        InputStreamResource file = new InputStreamResource(dataStorageService.getExcelData());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }
}
