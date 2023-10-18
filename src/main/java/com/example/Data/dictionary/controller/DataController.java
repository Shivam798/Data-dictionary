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

@CrossOrigin(origins = "http://localhost:5174")
@RestController
@RequestMapping("api/v1")
public class DataController {
    @Autowired
    private DataStorageService dataStorageService;

    @Autowired
    private ExcelHelper excelHelper;

    @GetMapping("get/data")
    public ResponseEntity<Object>getData(){
        return new ResponseEntity<>(dataStorageService.getAllData(),HttpStatus.OK);
    }

    @GetMapping("get/header")
    public ResponseEntity<Object> getHeaderData(){
        return new ResponseEntity<>(dataStorageService.getHeaderData(),HttpStatus.OK);
    }

    @PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> UploadDateSheet(@RequestParam("file") MultipartFile file){
        return new ResponseEntity<>(dataStorageService.storeData(file),HttpStatus.OK);
    }

    @PutMapping("updateSingleEntity/{id}")
    public ResponseEntity<Object> updateSingleEntity(@PathVariable("id") String id,@RequestBody MetaDataModel updatedEntity){
        return new ResponseEntity<>(dataStorageService.updateSingleEntity(id,updatedEntity),HttpStatus.OK);
    }

    @DeleteMapping("deleteEntity/{dataId}")
    public ResponseEntity<Object> deleteEntity(@PathVariable("dataId") String dataId){
        return new ResponseEntity<>(dataStorageService.deleteEntity(dataId),HttpStatus.OK);
    }

    @GetMapping("download/template")
    public ResponseEntity<Resource> downloadTemplate(){
        String filename = "dataTemplate.xlsx";
        InputStreamResource file = new InputStreamResource(dataStorageService.getTemplate());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    @GetMapping("/download/Excel")
    public ResponseEntity<Resource> downloadExcel(){
        String filename = "dataDictionary.xlsx";
        InputStreamResource file = new InputStreamResource(dataStorageService.getExcelData());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }
}
