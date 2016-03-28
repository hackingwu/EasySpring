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
    private Map<Integer,String> orderLabels;
    private Map<Integer,String> getOrderLabels() throws IOException {
        orderLabels = new HashMap<Integer, String>();
        List<String> excelHeader = excel.getContentHeader();
        Iterator i$ = excelHeader.iterator();
        int i = 0;
        while(i$.hasNext()){
            String header = (String)i$.next();
            if (labels.containsKey(header))
                orderLabels.put(i, labels.get(header));
            i++;
        }
        return orderLabels;
    }

    public ExcelProcessor(Excel excel,Map labels) throws IOException {
        this.excel = excel;
        this.labels = labels;
        orderLabels = getOrderLabels();
    }


    public List<Map> getRawContentMap() throws IOException {
        List<Map> contentMap = new ArrayList();
        List allContent = excel.getContent();
        Iterator<List> i$ = allContent.iterator();
        while(i$.hasNext()){
            List rowContent = i$.next();
            Map map = new HashMap();
            boolean allBlank = true;
            Set<Integer> keySet = orderLabels.keySet();
            Iterator<Integer> keySetIterator = keySet.iterator();
            while (keySetIterator.hasNext()) {
                Integer key = keySetIterator.next();
                Object value = rowContent.get(key);
                if (value.getClass().equals(String.class)&& !StringUtil.isEmpty((String)value) ){
                    allBlank = false;
                }
                map.put(orderLabels.get(key), value);
            }
            if (!allBlank) contentMap.add(map);
        }

        return contentMap;
    }


}
