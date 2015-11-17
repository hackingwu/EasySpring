package cn.hackingwu.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hackingwu
 * @since 2015/08/17.
 */
public class StringUtil {
    public static boolean isEmpty(String s) {
        return !(s != null && s.trim().length() > 0);
    }


    public static String lowerCaseFirstCharacter(String s) {
        if (Character.isLowerCase(s.charAt(0))) return s;
        char[] chars = s.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    public static String upperCaseFirstCharacter(String s) {
        if (Character.isUpperCase(s.charAt(0))) return s;
        char[] chars = s.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static String join(List list, String separate) {
        StringBuilder stringBuilder = new StringBuilder();
        if (separate == null) separate = ",";//给separate设置一个默认值
        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                stringBuilder.append(list.get(i).toString());
                if (i < list.size() - 1) {
                    stringBuilder.append(separate);
                }
            }
        }
        return stringBuilder.toString();
    }
    public static String AorB(String a,String b){
        if (a == null) return b;
        else return a;
    }

    /**
     * 拼接Url查询参数
     * @param url
     * @param query
     * @return
     */
    public static String joinQueryString(String url,Map query){
        Set<Map.Entry> entrySet = query.entrySet();
        Iterator<Map.Entry> i$ = entrySet.iterator();
        List queryStrings = new ArrayList();
        while (i$.hasNext()){
            Map.Entry entry = i$.next();
            queryStrings.add(entry.getKey().toString() + "=" +entry.getValue().toString());
        }
        return url + "?" + join(queryStrings,"&");
    }

    public static int lastMatch(String regex, String s) {
        Pattern pattern = Pattern.compile(regex);
        int pos = -1;
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            pos = matcher.start();
        }
        return pos;
    }

    public static String underscore2Camel(String underscore){
        char[] underscoreChars = underscore.toCharArray();
        StringBuilder camel = new StringBuilder();
        int i = 0 ;
        int length = underscoreChars.length;
        while (i < length){
            if (underscoreChars[i] == '_'){
                camel.append(Character.toUpperCase(underscoreChars[i + 1]));
                i = i + 2;
            }else{
                camel.append(underscoreChars[i]);
                i = i + 1;
            }
        }
        return camel.toString();
    }

    public static String camel2Underscore(String camel){
        char[] camelChars = camel.toCharArray();
        StringBuilder underscore = new StringBuilder();
        int i = 0 ;
        int length = camelChars.length;
        while (i < length){
            if (Character.isUpperCase(camelChars[i]) && i > 0){
                underscore.append('_');
                underscore.append(Character.toLowerCase(camelChars[i++]));
            }else{
                underscore.append(camelChars[i++]);
            }
        }
        return underscore.toString();
    }
}
