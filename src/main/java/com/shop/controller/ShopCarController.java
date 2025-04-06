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
import com.shop.model.entity.ShopCar;
import com.shop.model.entity.Shopping;
import com.shop.model.entity.User;
import com.shop.service.ShopCarService;
import com.shop.service.ShoppingService;
import com.shop.service.UserService;
import lombok.Synchronized;
import org.elasticsearch.core.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/shopCar")
public class ShopCarController {

    @Resource
    private UserService userService;

    @Resource
    private ShopCarService shopCarService;

    @Resource
    private ShoppingService shoppingService;


    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)
    @PostMapping("/list/page")
    public BaseResponse<Page<ShopCar>> listShopCar(@RequestBody ShopCarAddRequest shopCar, HttpServletRequest request) {
        if (shopCar == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        获取登录用户
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        QueryWrapper<ShopCar> shopCarQueryWrapper = new QueryWrapper<>();
        shopCarQueryWrapper.eq("userId", loginUser.getId());
        shopCarQueryWrapper.like("shopName", shopCar.getShopName());
        int current = shopCar.getCurrent();
        int pageSize = shopCar.getPageSize();
        Page<ShopCar> page = shopCarService.page(new Page<>(current, pageSize), shopCarQueryWrapper);
        page.getRecords().forEach(shopCar1 -> {
            shopCar1.setUserId(null);
        });
        return ResultUtils.success(page);
    }

    @Transactional
    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)
    @PostMapping("/add/shopCar")
    public BaseResponse<Boolean> addShopCar(@RequestBody ShopCarAddRequest shopCar, HttpServletRequest request) {
        if (shopCar == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        获取登录用户
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        ShopCar addCar = new ShopCar();
        Shopping shopById = shoppingService.getById(shopCar.getShopId());

        ThrowUtils.throwIf(shopById == null, ErrorCode.NOT_FOUND_ERROR);
        if (shopById.getShopNum() <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "商品库存不足");
        }

//        更新商品数量
        UpdateWrapper<Shopping> shopCarUpdateWrapper = new UpdateWrapper<>();
        shopCarUpdateWrapper.eq("id", shopById.getId()).set("shopNum", shopById.getShopNum() - 1 == 0 ? 0 : shopById.getShopNum() - 1);
        boolean update = shoppingService.update(shopCarUpdateWrapper);
        ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR);


        BeanUtils.copyProperties(shopById, addCar);
        addCar.setUserId(String.valueOf(loginUser.getId()));
        addCar.setShopId(String.valueOf(shopById.getId()));
        addCar.setShopNum(String.valueOf(shopById.getShopNum()));
        addCar.setShopNum(String.valueOf(1));
        addCar.setId(null);

        boolean save = shopCarService.save(addCar);
//        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(save);

    }


    @Transactional
    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)
    @PostMapping("/delete/shopCar")
    public BaseResponse<Boolean> deleteShopCar(@RequestBody ShopCarAddRequest shopCar, HttpServletRequest request) {
        if (shopCar == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        获取登录用户
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        ShopCar shopCarById = shopCarService.getById(shopCar.getId());
        if (shopCarById == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        Shopping shopById = shoppingService.getById(shopCarById.getShopId());
        if (shopById == null) {
            Shopping shopping = new Shopping();
            BeanUtils.copyProperties(shopCarById, shopping);
            shoppingService.save(shopping);
        } else {
            UpdateWrapper<Shopping> shoppingUpdateWrapper = new UpdateWrapper<>();
            shoppingUpdateWrapper.eq("id", shopById.getId()).set("shopNum", shopById.getShopNum() + 1);
            boolean update = shoppingService.update(shoppingUpdateWrapper);
            ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR);
        }
        UpdateWrapper<ShopCar> shopCarUpdateWrapper = new UpdateWrapper<>();
        shopCarUpdateWrapper.allEq(Map.of("userId", loginUser.getId(), "shopId", shopCarById.getShopId(), "id", shopCar.getId()));
        boolean remove = shopCarService.remove(shopCarUpdateWrapper);

        return ResultUtils.success(remove);
    }
}
