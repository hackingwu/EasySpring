package cn.hackingwu.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author wuzj
 * @since 2015/07/22
 */
public class Excel {
    private InputStream inputStream;
    private ExcelType type;
    private String sheetName;
    private boolean header;
    private Workbook workbook;
    private Sheet sheet;
    private List<List> content;
    private List<String> contentHeader;
    public enum ExcelType{
        xls,xlsx;
    }

    public Excel(InputStream inputStream, ExcelType type) {
        this(inputStream,type,"sheet1",true);
    }

    public Excel(InputStream inputStream, ExcelType type, String sheetName, boolean header){
        this.inputStream = inputStream;
        this.type = type;
        this.sheetName = sheetName;
        this.header = header;
    }


    public Workbook getWorkbook() throws IOException {
        if (workbook == null){
            if (type == ExcelType.xls){
                workbook = new HSSFWorkbook(inputStream);
            }else if(type == ExcelType.xlsx){
                workbook = new XSSFWorkbook(inputStream);
            }
        }
        return workbook;
    }

    public Sheet getSheet() throws IOException {
        if (sheet == null){
            sheet = getWorkbook().getSheet(sheetName);
        }
        return sheet;
    }

    public List<List> getContent() throws IOException {
        if (content==null){
            content = new ArrayList();
            Iterator<Row> i$ = getSheet().rowIterator();
            boolean headerIgnored = false;
            while (i$.hasNext()){
                Row row = i$.next();
                if (header && !headerIgnored){
                    headerIgnored = true;
                    continue;
                }
                if (row != null){
                    List rowList = new ArrayList();
                    int cells = getContentHeader().size(); //解决header 和实际的列数不一致的问题，不填的要返回null
                    for (int j = 0 ; j < cells ; j++){
                        Cell cell = row.getCell(j);
                        if (cell == null) rowList.add("");
                        else{
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                            rowList.add(cell.getStringCellValue());
                        }
                    }
                    content.add(rowList);
                }
            }
        }
        return content;
    }

    public List<String> getContentHeader() throws IOException {
        if (contentHeader == null){
            contentHeader = new ArrayList();
            Row row = getSheet().getRow(0);
            int cells = row.getPhysicalNumberOfCells();
            for (int j = 0 ; j < cells ; j++){
                Cell cell = row.getCell(j);
                contentHeader.add(cell.getStringCellValue().trim());
            }
        }
        return contentHeader;
    }





    public ExcelType getType() {
        return type;
    }

    public boolean getHeader() {
        return header;
    }



    /**
     * 根据Cell类型设置数据
     * @param cell
     * @return
     */
    private Object getCellFormatValue(Cell cell) {
        Object cellValue = null;
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        cellValue = cell.getDateCellValue();
                    }else {
                        // 取得当前Cell的数值
                        cellValue = cell.getNumericCellValue(); //取消科学计数法
                    }
                    break;
                case Cell.CELL_TYPE_FORMULA: //公式型也按string取
                case Cell.CELL_TYPE_STRING:
                    // 取得当前的Cell字符串
                    try{
                        cellValue = cell.getRichStringCellValue().getString();
                    }catch (Exception e){
                        cellValue = "";
                    }
                    break;
            // 默认的Cell值
                default:
                    cellValue = "";
            }
        } else {
            cellValue = "";
        }
        return cellValue;
    }

}
