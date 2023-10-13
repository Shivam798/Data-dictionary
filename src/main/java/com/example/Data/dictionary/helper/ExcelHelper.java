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
        if (ValueMatchesOptions(row.getCell(0).toString(), headerToHeaderOption.get(0))) {
            dataModel.setProjectName(row.getCell(0).getStringCellValue());
        } else return 0;
        if (ValueMatchesOptions(row.getCell(1).toString(), headerToHeaderOption.get(1))) {
            dataModel.setOrigination(row.getCell(1).getStringCellValue());
        } else return 0;
        if (ValueMatchesOptions(row.getCell(2).toString(), headerToHeaderOption.get(2))) {
            dataModel.setScreenName(row.getCell(2).getStringCellValue());
        } else return 0;
        if (ValueMatchesOptions(row.getCell(3).toString(), headerToHeaderOption.get(3))) {
            dataModel.setFieldLabel(row.getCell(3).getStringCellValue());
        } else return 0;
        if (ValueMatchesOptions(row.getCell(4).toString(), headerToHeaderOption.get(4))) {
            dataModel.setVariableId(row.getCell(4).getStringCellValue());
        } else return 0;
        if (ValueMatchesOptions(row.getCell(5).toString(), headerToHeaderOption.get(5))) {
            dataModel.setDataType(row.getCell(5).getStringCellValue());
        } else return 0;
        if (ValueMatchesOptions(row.getCell(6).toString(), headerToHeaderOption.get(6))) {
            dataModel.setDataDescription(row.getCell(6).getStringCellValue());
        } else return 0;
        if (ValueMatchesOptions(row.getCell(7).toString(), headerToHeaderOption.get(7))) {
            dataModel.setMandatory(row.getCell(7).getStringCellValue());
        } else return 0;
        if (ValueMatchesOptions(row.getCell(8).toString(), headerToHeaderOption.get(8))) {
            dataModel.setSource(row.getCell(8).getStringCellValue());
        } else return 0;
        if (ValueMatchesOptions(row.getCell(9).toString(), headerToHeaderOption.get(9))) {
            dataModel.setAPI(row.getCell(9).getStringCellValue());
        } else return 0;
        if (ValueMatchesOptions(row.getCell(10).toString(), headerToHeaderOption.get(10))) {
            dataModel.setOperation(row.getCell(10).getStringCellValue());
        } else return 0;
        if (ValueMatchesOptions(row.getCell(11).toString(), headerToHeaderOption.get(11))) {
            dataModel.setCategory(row.getCell(11).getStringCellValue());
        } else return 0;
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
            System.out.println(data);
            row = sheet.createRow(rowIdx++);

            row.createCell(0).setCellValue(data.getProjectName());
            row.createCell(1).setCellValue(data.getOrigination());
            row.createCell(2).setCellValue(data.getScreenName());
            row.createCell(3).setCellValue(data.getFieldLabel());
            row.createCell(4).setCellValue(data.getVariableId());
            row.createCell(5).setCellValue(data.getDataType());
            row.createCell(6).setCellValue(data.getDataDescription());
            row.createCell(7).setCellValue(data.getMandatory());
            row.createCell(8).setCellValue(data.getSource());
            row.createCell(9).setCellValue(data.getAPI());
            row.createCell(10).setCellValue(data.getOperation());
            row.createCell(11).setCellValue(data.getCategory());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}

