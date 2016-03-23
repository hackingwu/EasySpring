package cn.hackingwu.util;

import com.google.common.base.CaseFormat;
import org.joda.time.DateTime;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Su Sunbin on 2015/2/9.
 */
public class CommonUtil {
    /**
     * 下划线模式转化为驼峰模式
     *
     * @param str
     * @return
     */
    public static String underscoreToCamel(String str) {
        //TODO: 有bug 如果是驼峰模式的经过转换会变成小写 比如 starLevel -> starlevel
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str);
    }

    public static String camelToUnderScore(String str) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, str);
    }

    public static Map<String, Object> camelToUnderScore(Map<String, Object> map) {
        Map<String, Object> props = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            props.put(camelToUnderScore(entry.getKey()), entry.getValue());
        }

        return props;
    }

    public static Map<String, Object> underscoreToCamel(Map<String, Object> map) {
        Map<String, Object> props = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            props.put(underscoreToCamel(entry.getKey()), entry.getValue());
        }

        return props;
    }

    public static Date ISODateToDate(String dateString) {
        DateTime dateTime = new DateTime(dateString);
        return dateTime.toDate();
    }

    // Map转化为Bean: 利用Introspector,PropertyDescriptor实现
    public static void transMap2Bean(Map<String, Object> map, Object obj) {
        String eName = "";
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();


            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                eName = key;
                Class type = property.getPropertyType();

                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    if (type.equals(Date.class) && value.getClass().equals(String.class)) {
                        value = CommonUtil.ISODateToDate((String) value);
                    }
                    if (type.equals(Long.class) && !value.getClass().equals(Long.class)) {
                        value = Long.valueOf(value.toString());
                    }
                    // 得到property对应的setter方法
                    Method setter = property.getWriteMethod();
                    setter.invoke(obj, value);
                }

            }

        } catch (Exception e) {
            System.out.println(eName + "transMap2Bean Error " + e);
        }

    }

}
