package com.imooc.miaosha.dao;

import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.OrderInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

@Mapper
public interface OrderDao {
    @Select("select * from miaosha_order where user_id=#{param1} and goods_id=#{param2}")
    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId);
    @Insert("insert into order_info(user_id,goods_id,goods_name,goods_count" +
            ",goods_price,order_channel,status,create_date) values(" +
            "#{userId},#{goodsId},#{goodsName},#{goodsCount},#{goodsPrice},#{orderChannel}" +
            ",#{status},#{createDate})")
    //通过SelectKey返回对应的Id,keyProperty代表的是domain对象中对应column的属性
    @SelectKey(keyColumn = "id",keyProperty = "id",resultType = Long.class,before = false,statement = "select last_insert_id()")
    public long insert(OrderInfo orderInfo);
    @Insert("insert into miaosha_order(user_id,goods_id,order_id) " +
            "values(#{userId},#{goodsId},#{orderId})")
    public int insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);
}
