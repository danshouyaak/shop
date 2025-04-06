package com.shop.model.vo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class ShoppingVO implements Serializable {

    @TableField(exist = false)

    /*
      id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
    /**
     * 商品名称
     */
    @TableField(value = "shopName")
    private String shopName;
    /**
     * 商品数量
     */
    @TableField(value = "shopNum")
    private Integer shopNum;
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
}