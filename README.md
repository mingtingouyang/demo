# 环境准备

1. 修改 hosts 文件，添加

```
127.0.0.1       eureka1
127.0.0.1       eureka2
```

2. 需要有 tomcat 8 以上

# 启动步骤

1. 先启动两个 eureka，直接用 main 方法启动
2. 依次启动 rest、search、order，用 main 方法启动
3. 启动 sso 单点登录服务，需要用 tomcat 去启动，端口使用 8084，Context 为空
4. 启动 manager 后台管理，需要用 tomcat 去启动，端口使用 8080，Context 为空
5. 启动 portal 商城主界面，需要用 tomcat 去启动，端口使用 8082，Context 为空

# 介绍

该项目基于微服务构建，使用 Spring Cloud + Eureka 注册中心管理微服务。使用了 SSM 框架，数据库使用 MySQL，运用 Redis 作为缓存，搜索功能基于 Solr 服务实现，使用了 ActiveMQ 实现消息队列功能。功能上主要包含两个部分：后台管理系统以及商城 Web 客户端。该项目以学习为目的，部分重复性的功能没有实现，请谅解。

## 后台管理系统

后台管理系统的前端主要使用 EasyUI 构建，该系统负责对商城内容的管理。以下为目前实现了的功能介绍。

### 商品类别管理

商品类别可用于商城页面的菜单加载（类别列表），以及分类创建商品。这里实现了的功能为管理每个类别的商品介绍中的参数模板，包含两级菜单，例如：

- 手机类别的商品的商品详情包括：基本信息（品牌、型号、上市时间等）、硬件信息（CPU、摄像头、电池等）...
- 电视类别的商品的商品详情包括：基本信息（品牌、型号、上市时间等）、硬件信息（尺寸、分辨率、色域等）...

管理员可以为每种类别等商品设定固定的模板，这样在添加商品时，会根据选定的商品类别，提供相应的模板给添加者填写商品的具体参数信息，用于显示在商城的商品详情页面。

![模板管理示范](https://github.com/mingtingouyang/E-Mall-System/blob/master/gif/Peek%202019-09-28%2011-22.gif)

### 商品管理

商品管理包括了商品条目的增删改查（目前删、改待实现）。商品的详细信息包括了商品名称、卖点、价格、条形码、库存以及规格参数（参考上面一条）。这里的商品图片上传基于 vsftpd 实现，在一个 Linux 服务器同时开启了 vsftpd 服务以及 tengine (nginx) 服务。程序中使用 Apache 提供的 FTPCLient 工具将图片上传到 Linux 服务器中，再返回对应的 nginx 访问地址存储在数据库中。用这样的方式实现图片上传、显示的功能。

![商品添加示范](https://github.com/mingtingouyang/E-Mall-System/blob/master/gif/Peek%202019-09-28%2011-31.gif)

### 内容管理

网站内容管理主要是对网站广告的管理。广告内容详情包括了图片、商品名称、和广告地址。目前只实现了中央大广告的管理。

![广告管理](https://github.com/mingtingouyang/E-Mall-System/blob/master/gif/Peek%202019-09-28%2011-36.gif)

## Web 客户端

Web 客户端前端以“京东商城”的形式展示商城，后台程序与多个微服务系统通信，包括了单点登录服务、订单服务、商品内容服务等。前台首页的渲染需要获取商品类别加载分类列表，以及获取广告内容加载商城广告。为了减轻数据库的负担，将这两项内容都装进 Redis Cluster 作为缓存，也加快了页面渲染速度。由于这里使用了 Redis，所以后台管理系统修改分类、广告时，对应的也会重置 Redis Cluster 中对应的项。

![分类列表](https://github.com/mingtingouyang/E-Mall-System/blob/master/gif/Peek%202019-09-27%2011-02.gif)

![轮播广告](https://github.com/mingtingouyang/E-Mall-System/blob/master/gif/Peek%202019-09-27%2011-05.gif)

Web 客户端提供搜索功能，支持全文检索。搜索功能基于 Solr 实现，能够将搜索关键字高亮处理。对应的，后台管理系统修改商品时，需要同步 Solr 的索引库。这里使用 ActiveMQ 消息队列异步处理索引库同步问题。

单点登录基于 Redis 和 Cookie 实现。用户在进行订单操作时需要验证登录，登录成功后为用户生成随机 UUID 作为 token，存储在 Cookie 中；同时将 token 作为 key，用户信息作为 value，存储在 Redis 中，并设定失效时间，每次验证登录状态都会刷新失效时间。

## 商城演示

![操作演示](https://github.com/mingtingouyang/E-Mall-System/blob/master/gif/Peek%202019-09-06%2017-14.gif)
