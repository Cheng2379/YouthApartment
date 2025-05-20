# 青年公寓App

## 简介
青年公寓是一款面向年轻人的租房应用，致力于提供便捷、高效的租房服务。该应用采用Kotlin语言开发，基于现代Android开发技术栈，为用户提供以下核心功能：
- 房源检索与筛选
- 租房预约
- 查看房源详细信息
- 租约管理

## 技术亮点
### 架构设计
- 采用MVC架构分层设计，Activity/Fragment负责UI展示，RetrofitUtil统一处理网络请求
- 模块化设计，按功能划分包结构（activity, entity, util, view等），降低耦合度

### 工具与扩展
- 集成并自定义了多种工具与扩展函数，包括：
  - **[RetrofitUtil.kt](app/src/main/java/com/cheng/youthapartment/util/RetrofitUtil.kt)**：封装网络请求，简化API调用流程，提高开发效率
  - **[Logger.kt](app/src/main/java/com/cheng/youthapartment/util/Logger.kt)**：自定义日志工具，可快速定位目标文件的日志行数，便于调试
  - **[DataUtil.kt](app/src/main/java/com/cheng/youthapartment/util/DataUtil.kt)**：提供日期处理、数据验证等通用数据处理功能，简化复杂逻辑
  - **[ExpandTool.kt](app/src/main/java/com/cheng/youthapartment/util/ExpandTool.kt)扩展函数**：为View、Activity等组件提供丰富的扩展功能，提高代码可读性

### UI实现
- 使用ViewPager2+自定义指示器实现房源图片轮播，支持自动滚动
- 采用RecyclerView+GridLayoutManager实现标签流式布局
- 通过ThemeModelManager实现深色/浅色主题无缝切换
- 自定义RoomItemView复用组件，统一房源列表项样式
- 采用自定义PopupWindow与动画，实现下拉筛选栏效果

### 地图功能
- 集成高德地图SDK v10.1.201，提供房源位置可视化展示：
  - 房源位置标记（Marker）
  - 地图类型切换（日间/夜间模式）
  - 15级缩放定位
- 处理地图与滚动视图的滑动冲突

### 数据管理
- 采用BaseActivity与ActivityCollector单例对象管理所有Activity
- 使用全局App管理上下文、SharedPreferences、登录状态、主题偏好等信息
- Glide图片加载配合自定义SquareCrop变换实现圆角图片
- 使用GSON解析嵌套JSON数据结构

### 租约管理
- 完整状态机设计（7种租约状态）
- 支持续约逻辑：自动计算新租期
- 退租确认对话框流程

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