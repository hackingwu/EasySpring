/* =============================================================
 * Created: [2015/3/3 10:22] by wuzj(971643)
 * =============================================================
 *
 * Copyright 2014-2015 NetDragon Websoft Inc. All Rights Reserved
 *
 * =============================================================
 */
package search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;

/**
 * @author hackingwu
 * @since 2015/08/17
 */
public class OffsetPage extends PageRequest {

    /**
     * Member Description
     */

    private static final long serialVersionUID = 1170816091598217048L;
    static Logger logger = LoggerFactory.getLogger(OffsetPage.class);
    public int offset = -1;
    public Sort sort = null;

    public OffsetPage(int page, int size, Sort sort) {
        super(page, size, sort);
        // TODO Auto-generated constructor stub
    }

    public static OffsetPage createPage(HttpServletRequest request, Sort sort) {
        int offset = getDefaultOffset(request);
        int limit = getDefaultLimit(request);
        OffsetPage page = new OffsetPage(1, limit, sort);
        page.setOffset(offset);
        return page;
    }

    /**
     * 获取Sort对象，增加对字段的校验
     *
     * @param sortStr
     * @param clazz
     * @return
     */
    public static Sort getSort(String sortStr, Class clazz) {
        String[] orders = sortStr.trim().split("\\s+");
        Sort sort = null;
        if (orders.length > 0) {
            String field = orders[0];
            try {
                field = StringUtil.underscore2Camel(field);
                Field clazzField = clazz.getDeclaredField(field);
                if (clazzField != null && clazzField.getAnnotation(Transient.class) == null) {
                    if (orders.length == 1) {
                        sort = new Sort(Sort.Direction.ASC, field);
                    } else if (orders.length == 2) {
                        if (orders[1].toLowerCase().equals("desc")) {
                            sort = new Sort(Sort.Direction.DESC, field);
                        } else {
                            sort = new Sort(Sort.Direction.ASC, field);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("查询排序参数错误", e);
            }
        }
        return sort;
    }

    public static OffsetPage createPageWithClass(HttpServletRequest request, Class clazz) {
        int offset = getDefaultOffset(request);
        int limit = getDefaultLimit(request);
        String $orderBy = StringUtil.AorB(request.getParameter("$orderby"), request.getParameter("orderby"));
        Sort sort = null;
        if (!StringUtil.isEmpty($orderBy)) {
            sort = getSort($orderBy, clazz);
        }
        OffsetPage page = new OffsetPage(1, limit, sort);
        page.setOffset(offset);
        return page;
    }

    private static int getDefaultOffset(HttpServletRequest request) {
        int offset = 0;
        try {
            String offsetStr = StringUtil.AorB(request.getParameter("$offset"),request.getParameter("offset"));
            if (!StringUtil.isEmpty(offsetStr))
                offset = Integer.parseInt(offsetStr);
        } catch (Exception e) {
            logger.error("", e);
        }
        return offset > -1 ? offset : 0;
    }

    private static int getDefaultLimit(HttpServletRequest request) {
        int limit = 15;
        try {
            String limitStr = StringUtil.AorB(request.getParameter("$limit"),request.getParameter("limit"));
            if (!StringUtil.isEmpty(limitStr))
                limit = Integer.parseInt(limitStr);
        } catch (Exception e) {
            logger.error("", e);
        }
        return limit > 0 ? limit : 15;
    }

    @Override
    public int getOffset() {
        if (this.offset == -1) {
            return super.getOffset();
        }
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
