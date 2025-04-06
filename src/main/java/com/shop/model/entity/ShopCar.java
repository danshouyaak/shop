package com.shop.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.shop.common.PageRequest;
import lombok.Data;

/**
 * 购物车
 * @TableName shop_car
 */
@TableName(value ="shop_car")
@Data
public class ShopCar implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @TableField(value = "userId")
    private String userId;

    /**
     * 商品id
     */
    @TableField(value = "shopId")
    private String shopId;

    /**
     * 商品名称
     */
    @TableField(value = "shopName")
    private String shopName;

    /**
     * 商品数量
     */
    @TableField(value = "shopNum")
    private String shopNum;

    /**
     * 商品地址
     */
    @TableField(value = "shopAddress")
    private String shopAddress;

    /**
     * 商品价格
     */
    @TableField(value = "shopPrice")
    private String shopPrice;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableField(value = "isDelete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}