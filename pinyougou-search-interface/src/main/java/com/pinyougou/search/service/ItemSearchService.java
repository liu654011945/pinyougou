package com.pinyougou.search.service;

import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.search.service
 * @since 1.0
 */
public interface ItemSearchService {
    /**
     * 根据传递过来的参数 根据条件进行查询
     * @param searchMap  输入的查询条件
     * @return 查询到的结果(包括商品的集合，商品的总记录数 总页数 每页显示多少行..)
     */
    public Map search(Map searchMap);
}
