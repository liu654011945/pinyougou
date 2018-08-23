package com.itheima.dataredis;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 三国的包子 ThinkPad pinyougou-parent
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.itheima.dataredis
 */

@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
@RunWith(SpringRunner.class)
public class TestRedis {

    @Autowired
    private RedisTemplate redisTemplate;


    //默认的请情况下 spring data redis 使用的序列化策略是JDK自带的序列化策略  （二进制）  对象要存进redis中需要实现序列化接口
    @Test
    public void StringTest(){
        redisTemplate.boundValueOps("key1").set("value1");//objec.set("key1","value1");
        System.out.println(redisTemplate.boundValueOps("key1").get());
    }

    @Test
    public void HashTest(){
        redisTemplate.boundHashOps("key2").put("field1","value1");
        Object o = redisTemplate.boundHashOps("key2").get("field1");
        System.out.println(o);
    }

}
