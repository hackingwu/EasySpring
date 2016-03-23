import cn.hackingwu.excel.Excel;
import cn.hackingwu.excel.ExcelHelper;
import cn.hackingwu.excel.ExcelProcessor;
import com.alibaba.fastjson.JSON;
import com.google.common.io.PatternFilenameFilter;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
