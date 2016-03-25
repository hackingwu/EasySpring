import cn.hackingwu.excel.Excel;
import cn.hackingwu.excel.ExcelHelper;
import cn.hackingwu.excel.ExcelProcessor;
import cn.hackingwu.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.PatternFilenameFilter;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import java.io.*;
import java.util.*;

/**
 * @author hackingwu.
 * @since 2016/3/23.
 */
public class ExcelTest {
    @Test
    public void test1() throws IOException {
        File parent = new File("F:/excel");
        Map map = new HashMap();
        map.put("行政区划代码", "area_code");
        map.put("学校名称", "name");
        map.put("学校代码", "code");
        if (parent.isDirectory()){
            File[] files = parent.listFiles(new PatternFilenameFilter(".*\\.xlsx"));
            for (File file : files) {
                Excel excel = new Excel(new FileInputStream(file), Excel.ExcelType.xlsx);
                List<Map> result = new ExcelProcessor(excel, map).getRawContentMap();
                String name = file.getCanonicalPath().replace(".xlsx", ".json");
                FileOutputStream fileOutputStream = new FileOutputStream(name);
                fileOutputStream.write(JSON.toJSONBytes(result));
            }
        }
    }

    @Test
    public void test2() throws Exception {
        File parent = new File("F:/gjs0325");
        File[] jsons = parent.listFiles(new PatternFilenameFilter(".*\\.json$"));
        Map areaMap = new HashMap();
        for (File file : jsons) {
            System.out.println(file.getName());
            FileInputStream jsonInputStream = new FileInputStream(file);
            ObjectMapper objectMapper = new ObjectMapper();
            try{

                Map map = objectMapper.readValue(jsonInputStream,Map.class);
                Set<String> keySet = map.keySet();
                for (String key : keySet) {
                    areaMap.put(map.get(key), key);
                }
            }catch (Exception e){}
        }
        File excelFile = new File("F:/gjs0325/schools.xls");
        Excel excel = new Excel(new FileInputStream(excelFile), Excel.ExcelType.xls);
        Workbook workbook = excel.getWorkbook();
        int num = workbook.getNumberOfSheets();
        Map label = new HashMap();
        label.put("地区", "area0");label.put("县区", "area1");label.put("行政区划代码", "area_code");
        label.put("学校名称", "name");
        label.put("学校代码", "code");
        List<Map> jsonResult = new ArrayList();
        for (int i = 0 ; i < num ;i++){
            excel.setSheet(workbook.getSheetAt(i));
            excel.setContent(null);
            System.out.println(excel.getSheet().getSheetName());
            List<Map> result = new ExcelProcessor(excel, label).getRawContentMap();
            for (Map map : result) {
                String areaCode = (String)map.get("area_code");
                if (StringUtil.isEmpty(areaCode)) {
                    String value = (String)areaMap.get(map.get("area1"));
                    if (value == null) value = (String)areaMap.get(map.get("area0"));
                    if (value != null) map.put("area_code", value);
                }
                map.remove("area1");
                map.remove("area0");
            }

            jsonResult.addAll(result);
        }
        FileOutputStream fileOutputStream = new FileOutputStream("F:/gjs0325/data.json");
        fileOutputStream.write(JSON.toJSONBytes(jsonResult));
    }
}
