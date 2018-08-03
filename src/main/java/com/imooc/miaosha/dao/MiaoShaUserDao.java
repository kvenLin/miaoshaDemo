package com.imooc.miaosha.dao;

import com.imooc.miaosha.domain.MiaoShaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MiaoShaUserDao {
    @Select("select * from miaosha_user where id=#{id}")
    public MiaoShaUser getById(long id);
}
