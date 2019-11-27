package com.jqsoft.babyservice.commons.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: created by ksymmy@163.com at 2019/11/26 13:55
 * @desc: Excel 转成 List
 */
@Slf4j
public class ExcelUtil {

    /**
     * Excel 转成 List
     *
     * @param filePath excel路径
     * @return List<List < Object>>
     */
    public static List<List<Object>> ExcelToList(String filePath) {
        List<List<Object>> babyList = new ArrayList<>();
        try {
            String fileType = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
            File excel = new File(filePath);
            Workbook workbook;
            if ("xls".equals(fileType)) {
                FileInputStream fi = new FileInputStream(excel);
                workbook = new HSSFWorkbook(fi);
            } else if ("xlsx".equals(fileType)) {
                workbook = new XSSFWorkbook(excel);
            } else {
                log.error("文件类型不支持");
                return babyList;
            }
            int sheets = workbook.getNumberOfSheets();
            for (int i = 0; i < sheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                int firstRowIndex = sheet.getFirstRowNum() + 1;   //第一行是列名，所以不读
                int lastRowIndex = sheet.getLastRowNum();
                log.info("firstRowIndex:{} ", firstRowIndex);
                log.info("lastRowIndex: {}", lastRowIndex);
                for (int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {   //遍历行
                    log.info("rIndex: {}", rIndex);
                    Row row = sheet.getRow(rIndex);
                    if (!isBlankRow(row, rIndex, lastRowIndex)) {
                        List<Object> baby = new ArrayList<>();
                        //int firstCellIndex = row.getFirstCellNum();
                        //int lastCellIndex = row.getLastCellNum();
                        //int lastCellIndex = 8;
                        Cell cell = row.getCell(0);
                        baby.add(cell != null ? StringUtils.trim(cell.getStringCellValue()) : "");
                        cell = row.getCell(1);
                        baby.add(cell != null && StringUtils.equals("男", StringUtils.trim(cell.getStringCellValue())) ? "1" : "2");
                        cell = row.getCell(2);
                        baby.add(cell != null ? cell.getDateCellValue() : "");
                        cell = row.getCell(3);
                        baby.add(cell != null ? StringUtils.trim(cell.getStringCellValue()) : "");
                        cell = row.getCell(4);
                        baby.add(cell != null ? new BigDecimal(cell.getNumericCellValue()).toString() : "");
                        cell = row.getCell(5);
                        baby.add(cell != null ? StringUtils.trim(cell.getStringCellValue()) : "");
                        cell = row.getCell(6);
                        baby.add(cell != null ? new BigDecimal(cell.getNumericCellValue()).toString() : "");
                        cell = row.getCell(7);
                        baby.add(cell != null ? StringUtils.trim(cell.getStringCellValue()) : "");
                        babyList.add(baby);
                    }
                }
            }
        } catch (Exception e) {
            log.error("excel解析失败:{}", e.getMessage());
        }
        return babyList;
    }

    /**
     * row 是否是空行
     */
    private static boolean isBlankRow(Row row, int index, int rowCount) {
        if (row == null) {
            return true;
        }
        for (int i = index; i <= rowCount; i++) {
            if (row.getCell(i) != null) {
                return false;
            }
        }
        return true;
    }

//    public static List<List<Object>> ExcelToList(String filePath) throws Exception {
//        FastDateFormat instance = FastDateFormat.getInstance("yyyy-MM-dd");
//        List<List<Object>> babyList = new ArrayList<>();
//        String fileType = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
//        File excel = new File(filePath);
//        Workbook workbook;
//        if ("xls".equals(fileType)) {
//            FileInputStream fi = new FileInputStream(excel);
//            workbook = new HSSFWorkbook(fi);
//        } else if ("xlsx".equals(fileType)) {
//            workbook = new XSSFWorkbook(excel);
//        } else {
//            System.out.println("文件不支持");
//            return babyList;
//        }
//        int sheets = workbook.getNumberOfSheets();
//        for (int i = 0; i < sheets; i++) {
//            Sheet sheet = workbook.getSheetAt(i);
//            int firstRowIndex = sheet.getFirstRowNum() + 1;   //第一行是列名，所以不读
//            int lastRowIndex = sheet.getLastRowNum();
//            System.out.println("firstRowIndex: " + firstRowIndex);
//            System.out.println("lastRowIndex: " + lastRowIndex);
//            for (int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {   //遍历行
//                System.out.println("rIndex: " + rIndex);
//                Row row = sheet.getRow(rIndex);
//                if (!isBlankRow(row, rIndex, lastRowIndex)) {
//                    List<Object> baby = new ArrayList<>();
//                    int firstCellIndex = row.getFirstCellNum();
////                    int lastCellIndex = row.getLastCellNum();
//                    int lastCellIndex = 8;
//                    for (int cIndex = firstCellIndex; cIndex < lastCellIndex; cIndex++) {   //遍历列
//                        Cell cell = row.getCell(cIndex);
//                        Object value = "";
//                        if (cell != null) {
//                            switch (cell.getCellTypeEnum()) {
//                                case NUMERIC:
//                                    if (DateUtil.isCellDateFormatted(cell)) {
//                                        value = instance.format(cell.getDateCellValue());
//                                    } else {
//                                        value = new BigDecimal(cell.getNumericCellValue()).toString();
//                                    }
//                                    break;
//                                case STRING:
//                                    value += StringUtils.trim(cell.getStringCellValue());
//                                    break;
//                                default:
//                                    break;
//                            }
//                            System.out.println(value);
//                        } else {
//                            value = "";
//                        }
//                        baby.add(value);
//                    }
//                    if (!CollectionUtils.isEmpty(baby))
//                        babyList.add(baby);
//                }
//            }
//        }
//        return babyList;
//    }
}

