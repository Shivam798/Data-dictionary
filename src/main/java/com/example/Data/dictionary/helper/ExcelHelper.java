package com.example.Data.dictionary.helper;

import com.example.Data.dictionary.constants.ColumnValueOptions;
import com.example.Data.dictionary.constants.Header;
import com.example.Data.dictionary.model.MetaDataModel;
import com.example.Data.dictionary.repository.MetaDataRepository;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Cell;
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
import java.util.*;

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

    public List<String> ConvertRowToJSONObjectAndSave(Row row) {
        MetaDataModel dataModel = new MetaDataModel();
       for (int i = 0; i < header.size(); i++) {
           String cellValue = "";
           Cell cell = row.getCell(i);
           if (cell != null) {
               cellValue = cell.getStringCellValue();
           }
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
                        dataModel.setCategory(cellValue);
                        break;
                    case 8:
                        dataModel.setMandatory(cellValue);
                        break;
                    case 9:
                        dataModel.setSource(cellValue);
                        break;
                    case 10:
                        dataModel.setAPI(cellValue);
                        break;
                    case 11:
                        dataModel.setCredit(cellValue);
                        break;
                    case 12:
                        dataModel.setOperation(cellValue);
                        break;
                    case 13:
                        dataModel.setFI(cellValue);
                        break;
                    case 14:
                        dataModel.setPD(cellValue);
                        break;
                    case 15:
                        dataModel.setLegal(cellValue);
                        break;
                    case 16:
                        dataModel.setTechnical(cellValue);
                        break;
                    case 17:
                        dataModel.setRCU(cellValue);
                        break;
                    case 18:
                        dataModel.setSetStage(cellValue);
                        break;
                    case 19:
                        dataModel.setUseStage(cellValue);
                        break;
                }
            } else {
                List<String> output=new ArrayList<>();
                output.add("0");
                output.add(header.get(i)+" does not have required values");
                return output;
            }
       }
        try {
            dataModel.setId(primaryKeyGenerator.generatePrimaryKey(
                    Arrays.asList(dataModel.getOrigination(), dataModel.getProjectName(), dataModel.getVariableId())));
            metaDataRepository.save(dataModel);
            List<String> output=new ArrayList<>();
            output.add("1");
            output.add("Row added successfully");
            return output;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("There is problem in creating primary key", e);
        }
    }

    private boolean ValueMatchesOptions(String cell, List<String> projectNameOptions) {
        if (projectNameOptions.isEmpty()) {
            return true;
        }
        for (String option : projectNameOptions) {
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
                    data.getDataDescription(), data.getCategory(), data.getMandatory(),
                    data.getSource(), data.getAPI(), data.getOperation(),
            data.getFI(),data.getPD(),data.getLegal(),data.getTechnical(),
            data.getRCU(),data.getSetStage(),data.getUseStage()};

            for (String attribute : attributes) {
                row.createCell(columnIdx++).setCellValue(attribute);
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}

