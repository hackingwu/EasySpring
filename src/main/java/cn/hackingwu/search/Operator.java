/* =============================================================
 * Created: [2015/8/3 15:07] by wuzj(971643)
 * =============================================================
 *
 * Copyright 2014-2015 NetDragon Websoft Inc. All Rights Reserved
 *
 * =============================================================
 */
package cn.hackingwu.search;

/**
 * @author hackingwu.
 * @since 2015/08/17
 */
public enum Operator {
    eq("is"),
    like("regex"),
    gt("gt"),
    ge("gte"),
    lt("lt"),
    le("lte"),
    ne("ne")
    ;
    private String value;
    Operator(String value){
        this.value = value;
    }

    public static String getValue(String key){
        Operator operator = Operator.valueOf(key);
        return operator.value;
    }

    public String getValue() {
        return value;
    }

}
