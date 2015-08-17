/* =============================================================
 * Created: [2015/8/5 17:22] by wuzj(971643)
 * =============================================================
 *
 * Copyright 2014-2015 NetDragon Websoft Inc. All Rights Reserved
 *
 * =============================================================
 */
package search;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import service.SpringContextHolder;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author hackingwu.
 * @since 2015/08/17
 */
public class ConditionUtils {

    public static <T> List<T> query(Condition condition,OffsetPage page,Class<T> entityClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Query query = buildQuery(condition,page);
        MongoTemplate mongoTemplate = (MongoTemplate) SpringContextHolder.getBean("mongoTemplate");
        return mongoTemplate.find(query,entityClass);
    }

    public static long count(Condition condition,OffsetPage page,Class entityClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Query query = buildQuery(condition,page);
        MongoTemplate mongoTemplate = (MongoTemplate) SpringContextHolder.getBean("mongoTemplate");
        return mongoTemplate.count(query, entityClass);
    }

    public static Query buildQuery(Condition condition,OffsetPage page) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Criteria criteria = new Criteria();
        if (condition!=null)criteria = ConditionBridge.toCriteria(condition);
        Query query = new Query();
        query.addCriteria(criteria);
        query.with(page);
        return query;
    }
}
