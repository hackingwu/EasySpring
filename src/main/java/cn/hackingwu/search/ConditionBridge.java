/* =============================================================
 * Created: [2015/8/3 15:37] by wuzj(971643)
 * =============================================================
 *
 * Copyright 2014-2015 NetDragon Websoft Inc. All Rights Reserved
 *
 * =============================================================
 */
package cn.hackingwu.search;

import cn.hackingwu.util.CommonUtil;
import cn.hackingwu.util.StringUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ----------------------------
 * HttpServletRequest
 *   ^    |
 *   |    v
 *   Condition
 *   |    ^
 *   v    |
 *  Criteria
 *  解耦HttpServletRequest和Criteria，把request从查询中抽离出去
 * -------------------------------
 * @author wuzj(971643).
 * @since v0.2
 */
public class ConditionBridge {

    private static Logger logger = LoggerFactory.getLogger(ConditionBridge.class);

    public static Condition getConditionFromRequest(HttpServletRequest request,Class clazz) throws Exception {
        String filter = StringUtil.AorB(request.getParameter("filter"), request.getParameter("$filter"));
        if(!StringUtil.isEmpty(filter))  filter = StringUtil.escapeSpecialWord(filter);
        return processQuery1(filter, clazz);
    }

    public static Condition processQuery(String query,Class clazz) throws Exception{
        if (!StringUtil.isEmpty(query)) {
            int lastAndPos = StringUtil.lastMatch("\\band\\b",query);
            int lastOrPos   = StringUtil.lastMatch("\\bor\\b",query);
            if (lastAndPos < 0 && lastOrPos < 0) return getConditionFromQueryStr(query,clazz);
            else{
                String joinWord = lastAndPos > lastOrPos ? "and" : "or";
                int pos = joinWord.equals("and") ? lastAndPos : lastOrPos;
                String left = query.substring(0, pos).trim();
                String right = query.substring(pos + joinWord.length()).trim();
                Condition leftCondition = processQuery(left, clazz);
                Condition rightCondition = processQuery(right, clazz);
                List<Condition> conditions = new ArrayList<>();
                if (leftCondition != null) conditions.add(leftCondition);
                if (rightCondition != null) conditions.add(rightCondition);
                if (!conditions.isEmpty()){
                    if (joinWord.equals("and")) return new Condition().and(conditions);
                    else if(joinWord.equals("or")) return new Condition().or(conditions);
                }
            }
        }
        return null;
    }

    /**
     * 减少嵌套1
     * @param query
     * @param clazz
     * @return
     * @throws Exception
     */
    public static Condition processQuery1(String query, Class clazz) throws Exception{
        Condition condition = null;
        if (!StringUtil.isEmpty(query)) {
            Pattern pattern = Pattern.compile("(\\band\\b)|(\\bor\\b)");
            String[] result = pattern.split(query);
            Matcher matcher = pattern.matcher(query);
            String lastMatch = null;
            String currentMatch;
            int i = 0;
            Condition temp;
            condition = getConditionFromQueryStr(result[i++].trim(), clazz);
            for (; matcher.find(); i++, lastMatch = currentMatch){
                currentMatch = matcher.group();
                temp = getConditionFromQueryStr(result[i].trim(), clazz);
                if (!currentMatch.equals(lastMatch)) {
                    if ("and".equals(currentMatch)) {
                        condition = new Condition().and(temp, condition);
                    } else if ("or".equals(currentMatch)) {
                        condition = new Condition().or(temp , condition);
                    }
                } else {
                    if ("and".equals(currentMatch)) condition.and(temp);
                    else if("or".equals(currentMatch)) condition.or(temp);
                }
            }
        }
        return condition;
    }

    public static Condition getConditionFromQueryStr(String queryStr,Class clazz) throws Exception{
        String[] filterValues = queryStr.split("\\s+");
        if (filterValues.length == 3){
            return buildCondition(CommonUtil.underscoreToCamel(filterValues[0]), filterValues[1], filterValues[2], clazz);
        }
        if (filterValues.length > 3){
            StringBuffer tempValue = new StringBuffer();
            tempValue.append(filterValues[2]);
            for (int i = 3; i < filterValues.length; i++) {
                tempValue.append(filterValues[i]);
            }
            return buildCondition(CommonUtil.underscoreToCamel(filterValues[0]), filterValues[1], tempValue.toString(), clazz);
        }
        return null;
    }

    public static Condition buildCondition(String field,String operator,String value,Class clazz) throws Exception{
        Object valueObj = value;
        Class fieldType = null;
        if (field.indexOf("/") > -1) {//处理map中的查询.直接用'.'会被转义
            int pos = field.indexOf("<");
            if (pos > -1) {
                String type = field.substring(pos + 1, pos + 2);
                if (type.equalsIgnoreCase("I")) {//Integer类型
                    fieldType = Integer.class;
                } else if (type.equalsIgnoreCase("D")) {//Date类型
                    fieldType = Date.class;
                } else if (type.equalsIgnoreCase("B")) {//Boolean类型
                    fieldType = Boolean.class;
                } else if (type.equalsIgnoreCase("F")) {
                    fieldType = Float.class;
                } else if (type.equalsIgnoreCase("L")) {
                    fieldType = Long.class;
                }
                field = field.substring(0, pos);
                field = field.replace('/', '.');    //将前面Map字段与key连接符号'/'替换成'.'
            }
        } else {
            try {
                Field clazzField = clazz.getDeclaredField(field);
                if (clazzField != null && clazzField.getAnnotation(Transient.class) == null) {
                    fieldType = clazzField.getType();
                }
            } catch (Exception e) {
                logger.error("没有该字段", e);
            }
        }
        if (fieldType != null) {//处理列的查询
            if (operator.equals(Operator.in.getValue())){
                String[] ss = value.split(",");
                valueObj = new ArrayList();
                for (int i = 0 ;i < ss.length ;i++){
                    ((List)valueObj).add(convert(ss[i],fieldType));
                }
            }else{
                valueObj = convert(value,fieldType);
            }
            return new Condition(field,Operator.valueOf(operator),valueObj);
        }
        return null;
    }

    public static Object convert(String value,Class fieldType){
        Object valueObj = value;
        if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
            valueObj = Integer.valueOf(value);
        } else if (fieldType.equals(Boolean.class)) {
            valueObj = Boolean.valueOf(value);
        } else if (fieldType.equals(Date.class)) {
            DateTime dateTime = new DateTime(Long.valueOf(value.toString()));
            valueObj = dateTime.toDate();
        } else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
            valueObj = Long.valueOf(value);
        } else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
            valueObj = Float.valueOf(value);
        } else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
            valueObj = Double.valueOf(value);
        }
        return valueObj;
    }

    public static Criteria toCriteria(Condition condition) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Criteria criteria = new Criteria();
        if (condition != null){
            if (condition.selfCondition()){
                criteria = Criteria.where(condition.getProperty());
                Operator operator = condition.getOperator();
                Method operatorMethod = null;
                if (operator.equals(Operator.like)){
                    operatorMethod = Criteria.class.getDeclaredMethod(operator.getValue(), String.class);
                }else if(operator.equals(Operator.in)){
                    operatorMethod = Criteria.class.getDeclaredMethod(operator.getValue(),Collection.class);
                }else{
                    operatorMethod = Criteria.class.getDeclaredMethod(operator.getValue(), Object.class);
                }
                operatorMethod.invoke(criteria,condition.getValue());
            }
            List<Condition> andConditionChain = condition.getAndConditionChain();
            if (andConditionChain != null && andConditionChain.size() > 0){
                List<Criteria> andCriterias = toCriteria(andConditionChain);
                criteria.andOperator(andCriterias.toArray(new Criteria[andCriterias.size()]));
            }
            List<Condition> orConditionChain = condition.getOrConditionChain();
            if (orConditionChain != null && orConditionChain.size() > 0){
                List<Criteria> orCriterias = toCriteria(orConditionChain);
                criteria.orOperator(orCriterias.toArray(new Criteria[orCriterias.size()]));
            }
        }
        return criteria;
    }

    public static List<Criteria> toCriteria(List<Condition> conditions) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List<Criteria> criterias = new ArrayList<>();
        if (conditions != null && !conditions.isEmpty()) {
            Iterator<Condition> iterator = conditions.iterator();
            while(iterator.hasNext()){
                Condition condition = iterator.next();
                if (condition != null) criterias.add(toCriteria(condition));
            }
        }
        return criterias;
    }
}
