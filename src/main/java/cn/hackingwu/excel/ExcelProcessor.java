package cn.hackingwu.excel;


import cn.hackingwu.util.StringUtil;

import java.io.IOException;
import java.util.*;

/**
 * @author wuzj
 * @since 2015/07/22
 */
public class ExcelProcessor {
    private Excel excel;

    private Map<String,String> labels ;//无序,【“活动”:"activity"】
    private List<String> fields;

    private List getFields() throws IOException {
        fields = new ArrayList();
        List<String> excelHeader = excel.getContentHeader();
        Iterator i$ = excelHeader.iterator();
        while(i$.hasNext()){
            String header = (String)i$.next();
            if (labels.containsKey(header))
                fields.add(labels.get(header));
        }
        return fields;
    }

    public ExcelProcessor(Excel excel,Map labels) throws IOException {
        this.excel = excel;
        this.labels = labels;
        fields = getFields();
    }


    public List<Map> getRawContentMap() throws IOException {
        List<Map> contentMap = new ArrayList();
        List allContent = excel.getContent();
        Iterator<List> i$ = allContent.iterator();
        while(i$.hasNext()){
            List rowContent = i$.next();
            Map map = new HashMap();
            boolean allBlank = true;
            for(int i = 0 ;i < fields.size();i++){
                Object value = rowContent.get(i);
                if (value.getClass().equals(String.class)&& !StringUtil.isEmpty((String)value) ){
                    allBlank = false;
                }
                map.put(fields.get(i), value);
            }
            if (!allBlank) contentMap.add(map);
        }

        return contentMap;
    }


}
