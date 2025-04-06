package com.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.model.entity.ShopOrder;
import com.shop.service.ShopOrderService;
import com.shop.mapper.ShopOrderMapper;
import org.springframework.stereotype.Service;

/**
* @author yu
* @description 针对表【shop_order(订单)】的数据库操作Service实现
* @createDate 2025-04-02 23:50:22
*/
@Service
public class ShopOrderServiceImpl extends ServiceImpl<ShopOrderMapper, ShopOrder>
    implements ShopOrderService{

}




