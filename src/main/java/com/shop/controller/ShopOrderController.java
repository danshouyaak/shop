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
import com.shop.model.entity.ShopOrder;
import com.shop.model.entity.Shopping;
import com.shop.model.entity.User;
import com.shop.model.vo.ShoppingVO;
import com.shop.service.ShopCarService;
import com.shop.service.ShopOrderService;
import com.shop.service.ShoppingService;
import com.shop.service.UserService;
import org.elasticsearch.core.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("/shopOrder")
public class ShopOrderController {

    @Resource
    private ShoppingService shoppingService;

    @Resource
    private UserService userService;

    @Resource
    private ShopOrderService shopOrderService;

    @Resource
    private ShopCarService shopCarService;

    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)
    @PostMapping("/list/page")
    public BaseResponse<Page<ShopOrder>> shoppingList(@RequestBody ShopCarAddRequest shoppingVO, HttpServletRequest request) {
        if (shoppingVO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long current = shoppingVO.getCurrent();
        long pageSize = shoppingVO.getPageSize();
        QueryWrapper<ShopOrder> shopOrderQueryWrapper = new QueryWrapper<>();
        shopOrderQueryWrapper.eq("userId", loginUser.getId());
        shopOrderQueryWrapper.like("shopName", shoppingVO.getShopName());
        Page<ShopOrder> shoppingList = shopOrderService.page(new Page<>(current, pageSize),shopOrderQueryWrapper);
        shoppingList.getRecords().forEach(shopCar1 -> {
            shopCar1.setUserId(null);
        });
        return ResultUtils.success(shoppingList);
    }


    @PostMapping("/add/order")
    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)
    @Transactional
    public BaseResponse<Boolean> addShopping(@RequestBody ShoppingVO shoppingVO, HttpServletRequest request) {
        if (shoppingVO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//       从购物车到订单
        ShopCar shopping = shopCarService.getById(shoppingVO.getId());
        if (shopping == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "库存不足");
        }

        User user = userService.getLoginUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

//        库存减一
        QueryWrapper<ShopCar> shopCarQueryWrapper = new QueryWrapper<>();

        shopCarQueryWrapper.eq("id", shoppingVO.getId());
        boolean result = shopCarService.remove(shopCarQueryWrapper);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);


//        先查是否有订单
        QueryWrapper<ShopOrder> shopOrderQueryWrapper = new QueryWrapper<>();
        shopOrderQueryWrapper.allEq(Map.of("shopId", shoppingVO.getId(), "userId", user.getId()));
        ShopOrder one = shopOrderService.getOne(shopOrderQueryWrapper);


        //        保存到用户的订单中
        if (one != null) {
//            订单加一
            UpdateWrapper<ShopOrder> shopOrderUpdateWrapper = new UpdateWrapper<>();
            shopOrderUpdateWrapper.eq("id", one.getId()).set("shopNum", one.getShopNum() + 1);
            boolean update = shopOrderService.update(shopOrderUpdateWrapper);
            ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR);
        } else {
            //        创建新的定单
            ShopOrder shopOrder = new ShopOrder();
            BeanUtils.copyProperties(shopping, shopOrder);
            shopOrder.setUserId(String.valueOf(user.getId()));
            shopOrder.setShopNum(String.valueOf(1));
            shopOrder.setShopId(String.valueOf(shopping.getShopId()));
            shopOrder.setUpdateTime(new Date());
            boolean save = shopOrderService.save(shopOrder);
            ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
        }

        //        保存到用户的订单中
        return ResultUtils.success(result);
    }
}
