package cn.hackingwu.search;


import cn.hackingwu.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author hackingwu.
 * @since 2015/08/17
 */
public class Condition {
    private String property;

    private Operator operator;

    private Object value;

    private List<Condition> orConditionChain ;

    private List<Condition> andConditionChain ;

    public Condition(){
        this.orConditionChain = new ArrayList();
        this.andConditionChain = new ArrayList();
    }

    public Condition(String property, Operator operator, Object value) {
        this.property = property;
        this.operator = operator;
        this.value = value;
        this.orConditionChain = new ArrayList();
        this.andConditionChain = new ArrayList();
    }

    public Condition(String property, Operator operator, Object value, List<Condition> orConditionChain, List<Condition> andConditionChain) {
        this.property = property;
        this.operator = operator;
        this.value = value;
        this.orConditionChain = orConditionChain;
        this.andConditionChain = andConditionChain;
    }

    public Condition or(String property, Operator operator, Object value) {
        Condition condition =  new Condition(property,operator,value,null,null);
        condition.orConditionChain.add(this);
        return condition;
    }

    public Condition and(String property, Operator operator, Object value){
        Condition condition =  new Condition(property,operator,value,null,null);
        condition.andConditionChain.add(this);
        return condition;
    }

    public Condition or(List<Condition> conditions){
        if (conditions == null || conditions.size() == 0) return this;
        this.orConditionChain.addAll(conditions);
        return this;
    }

    public Condition and(List<Condition> conditions){
        if (conditions == null || conditions.size() == 0) return this;
        this.andConditionChain.addAll(conditions);
        return this;
    }

    public Condition or(Condition... conditions){
        if (conditions == null || conditions.length == 0) return this;
        this.orConditionChain.addAll(new ArrayList<Condition>(Arrays.asList(conditions)));
        return this;
    }

    public Condition and(Condition... conditions){
        if (conditions == null || conditions.length == 0) return this;
        this.andConditionChain.addAll(new ArrayList<Condition>(Arrays.asList(conditions)));
        return this;
    }

    public boolean selfCondition(){
        return !StringUtil.isEmpty(this.property) && this.operator != null;
    }

    public String getProperty() {
        return property;
    }

    public Operator getOperator() {
        return operator;
    }

    public Object getValue() {
        return value;
    }

    public List<Condition> getOrConditionChain() {
        return orConditionChain;
    }

    public List<Condition> getAndConditionChain() {
        return andConditionChain;
    }
}
