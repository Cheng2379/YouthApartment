# 青年公寓App

## 简介
青年公寓是一款面向年轻人的租房应用，致力于提供便捷、高效的租房服务。该应用采用Kotlin语言开发，基于现代Android开发技术栈，为用户提供以下核心功能：
- 房源检索与筛选
- 租房预约
- 查看房源详细信息
- 租约管理

## 技术亮点
### 架构设计
- 采用MVC架构，确保代码结构清晰、易于维护
- 模块化设计，各功能模块职责明确，降低耦合度

### 工具与扩展
- 集成并自定义了多种工具与扩展函数，包括：
  - **[RetrofitUtil.kt](app/src/main/java/com/cheng/youthapartment/util/RetrofitUtil.kt)**：封装网络请求，简化API调用流程，提高开发效率
  - **[Logger.kt](app/src/main/java/com/cheng/youthapartment/util/Logger.kt)]**：自定义日志工具，可快速定位目标文件的日志行数，便于调试
  - **[DataUtil.kt](app/src/main/java/com/cheng/youthapartment/util/DataUtil.kt)**：提供日期处理、数据验证等通用数据处理功能，简化复杂逻辑
  - **[ExpandTool.kt](app/src/main/java/com/cheng/youthapartment/util/ExpandTool.kt)扩展函数**：为View、Activity等组件提供丰富的扩展功能，提高代码可读性

### UI
- 采用Material Design设计规范，提供现代化UI界面
- 支持深色/浅色主题切换，通过ThemeModelManager实现主题无缝切换
- 采用自定义View与动画

### 地图与定位
- 集成高德地图SDK，提供房源位置可视化展示
- 支持地图导航、位置搜索等功能，增强用户体验
- 实现房源地理位置筛选，方便用户按区域查找房源

### 数据管理
- 采用GSON进行JSON数据解析，高效处理API返回数据
- 使用SharedPreferences存储用户配置和登录状态
- 实现数据缓存机制，减少网络请求，提高应用响应速度

### 用户体验优化
- 实现ViewPager2轮播图，展示房源图片
- 自定义指示器，提升图片浏览体验
- 下拉刷新与分页加载，优化长列表性能
- 表单验证与错误提示，提高用户输入体验

### 安全性
- 手机号+验证码登录，保障账户安全
- 隐私政策与用户协议支持，符合应用市场规范
- 敏感信息加密处理，保护用户数据安全

## 主要功能模块

### 用户认证
- 手机号验证码登录
- 用户信息管理
- 隐私政策与服务协议

### 房源搜索
- 多条件筛选（地区、价格、支付方式等）
- 排序功能（价格高低）
- 房源列表展示

### 房源详情
- 房间基本信息展示
- 配套设施说明
- 地理位置地图展示
- 图片轮播浏览

### 租约管理
- 租约签订与确认
- 续约功能
- 退租申请
- 租约状态追踪

## 开发环境
- 开发语言：Kotlin
- 最低支持Android版本：Android 8.0 (API 26)
- 目标Android版本：Android 14 (API 34)
- 构建工具：Gradle 8.7

## 第三方依赖
- Retrofit：网络请求
- Glide：图片加载与处理
- 高德地图SDK：地图与定位服务
- ViewPager2：图片轮播
- SwipeRefreshLayout：下拉刷新