package com.example.Data.dictionary.helper;

import com.example.Data.dictionary.constants.ColumnValueOptions;
import com.example.Data.dictionary.constants.Header;
import com.example.Data.dictionary.model.MetaDataModel;
import com.example.Data.dictionary.repository.MetaDataRepository;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

@Component
public class ExcelHelper {
    public static final String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private List<String> header = Header.header;
    private List<List<String>> headerToHeaderOption = ColumnValueOptions.headerToHeaderOption;

    static String SHEET = "DataDictionary";
    @Autowired
    PrimaryKeyGenerator primaryKeyGenerator;

    @Autowired
    MetaDataRepository metaDataRepository;

    public static boolean checkExcelFormat(MultipartFile file) {
        if (!TYPE.equals(file.getContentType())) {
            return false;
        }
        return true;
    }

    public Integer ConvertRowToJSONObjectAndSave(Row row) {
        MetaDataModel dataModel = new MetaDataModel();
       for (int i = 0; i < header.size(); i++) {
            String cellValue = row.getCell(i).getStringCellValue();
            if (ValueMatchesOptions(cellValue, headerToHeaderOption.get(i))) {
                switch (i) {
                    case 0:
                        dataModel.setProjectName(cellValue);
                        break;
                    case 1:
                        dataModel.setOrigination(cellValue);
                        break;
                    case 2:
                        dataModel.setScreenName(cellValue);
                        break;
                    case 3:
                        dataModel.setFieldLabel(cellValue);
                        break;
                    case 4:
                        dataModel.setVariableId(cellValue);
                        break;
                    case 5:
                        dataModel.setDataType(cellValue);
                        break;
                    case 6:
                        dataModel.setDataDescription(cellValue);
                        break;
                    case 7:
                        dataModel.setMandatory(cellValue);
                        break;
                    case 8:
                        dataModel.setSource(cellValue);
                        break;
                    case 9:
                        dataModel.setAPI(cellValue);
                        break;
                    case 10:
                        dataModel.setOperation(cellValue);
                        break;
                    case 11:
                        dataModel.setCategory(cellValue);
                        break;
                }
            } else {
                return 0;
            }
       }
        try {
            dataModel.setPrimaryKey(primaryKeyGenerator.generatePrimaryKey(
                    Arrays.asList(dataModel.getOrigination(), dataModel.getProjectName(), dataModel.getVariableId())));
            metaDataRepository.save(dataModel);
            return 1;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("There is problem in creating primary key", e);
        }
    }

    private boolean ValueMatchesOptions(String cell, List<String> projectNameOptions) {
        if (projectNameOptions.isEmpty()) {
            return true;
        }
        for (String option : projectNameOptions) {
            System.out.println(option + "  " + cell);
            if (option.equalsIgnoreCase(cell)) {
                return true;
            }
        }
        return false;
    }

    public ByteArrayInputStream getExcelSheet(List<MetaDataModel> dataList) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(SHEET);
        Integer rowIdx = 0;
        Row row;
        row = sheet.createRow(rowIdx++);
        for (int i = 0; i < header.size(); i++) {
            row.createCell(i).setCellValue(header.get(i));
        }
        for (MetaDataModel data : dataList) {
            int columnIdx = 0;
            row = sheet.createRow(rowIdx++);
            String[] attributes = {data.getProjectName(), data.getOrigination(), data.getScreenName(),
                    data.getFieldLabel(), data.getVariableId(), data.getDataType(),
                    data.getDataDescription(), data.getMandatory(), data.getSource(),
                    data.getAPI(), data.getOperation(), data.getCategory()};

            for (String attribute : attributes) {
                row.createCell(columnIdx++).setCellValue(attribute);
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}

