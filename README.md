# chat-m25

仿微信安卓应用（本地单机版）

## 最新版本

### v0.7.0 (2026-03-29)

**MINOR版本 - 扩展应用功能，增强日常使用体验**

- 新增群聊管理功能：可编辑群聊名称、设置群公告
- 新增聊天记录备份与恢复：支持导出数据到本地JSON文件，支持从备份文件恢复数据
- 新增表情包管理：支持浏览8个表情分类（笑脸、手势、爱心等）
- 数据库升级至v6，支持群头像和群公告字段

### v0.6.x (2026-03-29)

**早期版本汇总**

- v0.6.1: 修复Coil 3.x导入错误
- v0.6.0: 扩展聊天功能（消息转发、回复、媒体管理、新建群聊）

## 功能特性

- 仿微信UI设计，简洁美观
- 底部导航栏：微信、通讯录、我
- 聊天列表：显示聊天会话、未读消息数
- 聊天详情：支持发送和接收消息
- 聊天记录搜索：支持关键词搜索聊天内容
- 通讯录：管理联系人，支持星标朋友
- 个人中心：展示个人信息入口
- 朋友圈：浏览、发布、点赞朋友圈动态
- 群聊管理：编辑群名称、设置群公告
- 表情包管理：分类浏览表情
- 数据备份：支持导出导入聊天记录
- 本地存储：所有数据保存在本地数据库
- 日志系统：便于问题排查和分析

## 技术栈

- **语言**: Kotlin 2.3.20
- **目标SDK**: Android 16 (API 36)
- **构建工具**: Gradle 9.4.1 (Kotlin DSL)
- **UI框架**: Jetpack Compose + Material 3
- **架构**: MVVM + Clean Architecture
- **依赖注入**: Hilt
- **本地存储**: Room + DataStore
- **图片加载**: Coil
- **异步处理**: Kotlin Coroutines + Flow
- **导航**: Navigation Compose

## 版本历史

<details>
<summary>早期版本</summary>

### v0.5.x - v0.4.0
- v0.5.2: 重构优化存量代码
- v0.5.1: 修复构建失败问题
- v0.5.0: 完善用户交互功能（联系人详情、收藏功能、消息删除）
- v0.4.0: 聊天置顶、消息免打扰、聊天背景设置
- v0.3.0: 深色模式、表情选择面板
- v0.2.0: 朋友圈、聊天记录搜索
- v0.1.0: 初始版本，仿微信基本UI界面

</details>

## 下载

从 GitHub Releases 下载最新版本的 APK：
https://github.com/g-ai-001/chat-m25/releases

## 开发说明

```bash
# 克隆代码
git clone git@github.com:g-ai-001/chat-m25.git

# 查看分支
git branch -a

# 创建新版本
git checkout main
# 修改代码...
git commit -m "feat: 新功能描述"
git push origin main

# 创建版本标签触发构建
git tag v0.5.0 -m "0.5.0 新功能版本"
git push origin v0.5.0
```

## 项目结构

```
app/src/main/java/app/chat_m25/
├── data/               # 数据层
│   ├── local/         # 本地存储
│   │   ├── dao/       # Data Access Object
│   │   └── entity/     # 数据库实体
│   └── repository/     # 仓库
├── di/                 # 依赖注入模块
├── domain/             # 领域层
│   └── model/          # 领域模型
└── ui/                 # UI层
    ├── components/     # 公共组件
    ├── screens/        # 页面
    │   ├── home/      # 首页
    │   ├── contacts/   # 通讯录
    │   ├── emoticon/   # 表情包
    │   ├── favorites/  # 收藏
    │   ├── group/      # 群聊
    │   ├── profile/    # 个人中心
    │   ├── chat/      # 聊天详情
    │   └── moments/    # 朋友圈
    └── theme/          # 主题样式
```