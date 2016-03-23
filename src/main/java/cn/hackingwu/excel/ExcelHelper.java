/* =============================================================
 * Created: [2015/7/23 16:06] by wuzj(971643)
 * =============================================================
 *
 * Copyright 2014-2015 NetDragon Websoft Inc. All Rights Reserved
 *
 * =============================================================
 */
package cn.hackingwu.excel;

import com.alibaba.fastjson.JSON;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author wuzj(971643).
 * @since 0.2
 */
public class ExcelHelper {

    /**
     * 表头
     * @param fields
     * @param sheetName
     * @return
     */

    public static SXSSFWorkbook getXWorkBookWithFirstRow(List fields, String sheetName){
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);
        Sheet sheet = workbook.createSheet(sheetName);
        Row firstRow = sheet.createRow(0);
        CellStyle cellStyle = workbook.createCellStyle();
        Font firstRowFont = workbook.createFont();
        firstRowFont.setFontName("宋体");
        firstRowFont.setFontHeightInPoints((short)10);
        firstRowFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setFont(firstRowFont);
        for(int i = 0 ; i < fields.size();i++){
            Cell cell =firstRow.createCell(i);
            cell.setCellValue((String)fields.get(i));
            cell.setCellStyle(cellStyle);
        }
        return workbook;
    }

    public static HSSFWorkbook getHWorkBookWithFirstRow(List fields, String sheetName){
        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);
        Row firstRow = sheet.createRow(0);
        CellStyle cellStyle = workbook.createCellStyle();
        Font firstRowFont = workbook.createFont();
        firstRowFont.setFontName("宋体");
        firstRowFont.setFontHeightInPoints((short)10);
        firstRowFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setFont(firstRowFont);
        for(int i = 0 ; i < fields.size();i++){
            Cell cell =firstRow.createCell(i);
            cell.setCellValue((String)fields.get(i));
            cell.setCellStyle(cellStyle);
        }
        return workbook;
    }

    public static void writeToExcel(Sheet sheet,int startRow,List<Map> result){
        Workbook workbook = sheet.getWorkbook();
        CellStyle contentCellStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        Font contentFont = workbook.createFont();
        contentFont.setFontName("宋体");
        contentFont.setFontHeightInPoints((short)10);
        contentFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);
        contentCellStyle.setFont(contentFont);
        contentCellStyle.setDataFormat(format.getFormat("@"));//将列属性设置为文本类型
        for(int i = 0 ; i < result.size();i++) {
            Row row = sheet.createRow(startRow + i);
            int j = 0;
            Set keySet = result.get(i).keySet();
            Iterator<String> keyIt = keySet.iterator();
            while (keyIt.hasNext()) {
                String key = keyIt.next();
                Cell cell = row.createCell(j++);
                Object object = result.get(i).get(key);
                String value = object!=null?object.toString():"";
                cell.setCellValue(value);
                cell.setCellStyle(contentCellStyle);
            }
        }
    }

    /**
     *
     * @param sheet
     * @param header 显示在第一行的
     * @param fields key为header中的值
     * @param content
     */
    public static void writeToExcel(Sheet sheet, List<String> header, Map fields, List<Map> content){
        Workbook workbook = sheet.getWorkbook();
        CellStyle contentCellStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        Font contentFont = workbook.createFont();
        contentFont.setFontName("宋体");
        contentFont.setFontHeightInPoints((short)10);
        contentFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);
        contentCellStyle.setFont(contentFont);
        contentCellStyle.setDataFormat(format.getFormat("@"));//将列属性设置为文本类型
        Row headRow = sheet.createRow(0);
        for(int i = 0 ;i < header.size();i++){
            Cell cell = headRow.createCell(i);
            cell.setCellStyle(contentCellStyle);
            cell.setCellValue(header.get(i));
        }
        for(int j = 0 ; j < content.size();j++){
            Row rowContent = sheet.createRow(1+j);
            for (int z = 0 ; z < header.size();z++){
                Cell cell = rowContent.createCell(z);
                cell.setCellStyle(contentCellStyle);
                String s = "";
                Object key = fields.get(header.get(z));
                if (content.get(j).containsKey(key)){
                    Object value = content.get(j).get(key);
                    if (value != null)s = value.toString();
                }
                cell.setCellValue(s);
            }
        }

    }

    public static void writeToExcel(Sheet sheet, List objectList, Class clazz){
        Field[] fields = clazz.getDeclaredFields();
        List<String> headers = new ArrayList<>();
        Map fieldMap = new HashMap();
        for (Field field : fields){
            headers.add(field.getName());
            fieldMap.put(field.getName(),field.getName());
        }
        List<Map> content = new ArrayList<>();
        for (Object o : objectList){
            if (o == null) continue;
            if (!o.getClass().equals(clazz)) continue;
            content.add(JSON.parseObject(JSON.toJSONString(o), Map.class));
        }
        writeToExcel(sheet,headers,fieldMap,content);
    }


}
