# 数据库初始化
# @author <a href="https://github.com/liyupi">程序员鱼皮</a>
# @from <a href="https://yupi.icu">编程导航知识星球</a>

-- 创建库
create database if not exists my_db;

-- 切换库
use my_db;


-- 购物车表
create table if not exists shop_car
(
    id           bigint auto_increment comment 'id' primary key,
    userId  varchar(1024)                           not null comment '用户id',
    shopId  varchar(1024)                           not null comment '商品id',
    shopName  varchar(1024)                           not null comment '商品名称',
    shopNum  varchar(256)                           not null comment '商品数量',
    shopAddress varchar(1024)                       not null comment '商品地址',
    shopPrice varchar(256)                       not null comment '商品价格',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
) comment '购物车' collate = utf8mb4_unicode_ci;

-- 订单表
create table if not exists shop_order
(
    id           bigint auto_increment comment 'id' primary key,
    userId  varchar(1024)                           not null comment '用户id',
    shopId  varchar(1024)                           not null comment '商品id',
    shopName  varchar(1024)                           not null comment '商品名称',
    shopNum  varchar(256)                           not null comment '商品数量',
    shopAddress varchar(1024)                       not null comment '商品地址',
    shopPrice varchar(256)                       not null comment '商品价格',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
) comment '订单' collate = utf8mb4_unicode_ci;

-- 商品表
create table if not exists shopping
(
    id           bigint auto_increment comment 'id' primary key,
    shopName  varchar(1024)                           not null comment '商品名称',
    shopNum  varchar(256)                           not null comment '商品数量',
    shopAddress varchar(1024)                       not null comment '商品地址',
    shopPrice varchar(256)                       not null comment '商品价格',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
) comment '商品' collate = utf8mb4_unicode_ci;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_unionId (unionId)
) comment '用户' collate = utf8mb4_unicode_ci;
