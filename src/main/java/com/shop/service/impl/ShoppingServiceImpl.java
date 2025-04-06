package com.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.model.entity.Shopping;
import com.shop.service.ShoppingService;
import com.shop.mapper.ShoppingMapper;
import org.springframework.stereotype.Service;

/**
 * @author yu
 * @description 针对表【shopping(商品)】的数据库操作Service实现
 * @createDate 2025-04-01 11:13:04
 */
@Service
public class ShoppingServiceImpl extends ServiceImpl<ShoppingMapper, Shopping> implements ShoppingService {

}




