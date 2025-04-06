package com.shop.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.annotation.AuthCheck;
import com.shop.common.BaseResponse;
import com.shop.common.ErrorCode;
import com.shop.common.ResultUtils;
import com.shop.constant.UserConstant;
import com.shop.exception.BusinessException;
import com.shop.exception.ThrowUtils;
import com.shop.model.dto.shop.ShopCarAddRequest;
import com.shop.model.entity.ShopOrder;
import com.shop.model.entity.Shopping;
import com.shop.model.entity.User;
import com.shop.model.vo.ShoppingVO;
import com.shop.service.ShopOrderService;
import com.shop.service.ShoppingService;
import com.shop.service.UserService;
import lombok.Synchronized;
import org.elasticsearch.core.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/shopping")
public class ShoppingController {
    @Resource
    private ShoppingService shoppingService;

    @Resource
    private UserService userService;

    @Resource
    private ShopOrderService shopOrderService;

    /**
     * 分页获取商品列表
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)
    public BaseResponse<Page<Shopping>> shoppingList(@RequestBody ShopCarAddRequest shopCarAddRequest, HttpServletRequest request) {
        if (shopCarAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = shopCarAddRequest.getCurrent();
        long pageSize = shopCarAddRequest.getPageSize();
        QueryWrapper<Shopping> shoppingQueryWrapper = new QueryWrapper<>();
        shoppingQueryWrapper.like("shopName", shopCarAddRequest.getShopName());
        Page<Shopping> shoppingList = shoppingService.page(new Page<>(current, pageSize), shoppingQueryWrapper);
        return ResultUtils.success(shoppingList);
    }

    /**
     * 添加商品 尽管理员可操作
     *
     * @param shoppingVO
     * @param request
     * @return
     */
    @PostMapping("/add")
    @Transactional
    public BaseResponse<Long> shoppingAdd(@RequestBody ShoppingVO shoppingVO, HttpServletRequest request) {
        if (shoppingVO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        不是管理员不能执行删除
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        shoppingVO.setId(null);
        Shopping shopping = new Shopping();
        BeanUtils.copyProperties(shoppingVO, shopping);
        boolean result = shoppingService.save(shopping);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newShoppingId = shopping.getId();
        return ResultUtils.success(newShoppingId);
    }

    /**
     * 删除
     * 尽管理员可操作
     *
     * @param ShoppingVO
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> shoppingDelete(@RequestBody ShoppingVO ShoppingVO, HttpServletRequest request) {
        if (ShoppingVO == null || ShoppingVO.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = ShoppingVO.getId();
        Shopping oldShop = shoppingService.getById(id);
        ThrowUtils.throwIf(oldShop == null, ErrorCode.NOT_FOUND_ERROR);
//        不是管理员不能执行删除
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = shoppingService.removeById(id);
        return ResultUtils.success(b);
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/update")
    @Transactional
    public BaseResponse<Boolean> shoppingUpdate(@RequestBody ShoppingVO shoppingVO, HttpServletRequest request) {
        if (shoppingVO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        不是管理员不能执行更新操作
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        Shopping shopping = new Shopping();
        BeanUtils.copyProperties(shoppingVO, shopping);
        UpdateWrapper<Shopping> shoppingUpdateWrapper = new UpdateWrapper<>();
        shoppingUpdateWrapper.eq("id", shopping.getId());
        boolean update = shoppingService.update(shopping, shoppingUpdateWrapper);
        ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(update);
    }


}
