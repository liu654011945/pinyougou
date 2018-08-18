package com.pinyougou.shop.security.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 *
 * 自定义认证类
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou.shop.security.service
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String mima=new BCryptPasswordEncoder().encode("123456");
        String mima2=new BCryptPasswordEncoder().encode("123456");
        System.out.println(">>>"+mima);
        System.out.println(mima2);


        //1.从数据库根据用户名查询用户的数据
        TbSeller seller = sellerService.findOne(username);
        if(seller==null){
            throw new UsernameNotFoundException("userame not found");
        }
        //判断用户是否已经审核通过
        if(!"1".equals(seller.getStatus())){
            throw new UsernameNotFoundException("shenhe not tongguo");
        }
        //2.获取用户的信息（密码）
        //3.匹配验证（由spring security来自动完成）
        return new User(username,seller.getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_SELLER"));
    }
}
