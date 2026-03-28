# chat-m25

仿微信安卓应用（本地单机版）

## 最新版本

### v0.1.0 (2026-03-29)

**初始版本**

- 搭建项目基础框架，使用 Jetpack Compose + Material 3
- 实现 MVVM + Clean Architecture 架构
- 配置 Room 数据库存储聊天记录和联系人
- 配置 Hilt 依赖注入
- 实现日志系统（日志保存在外部存储目录）
- 实现底部导航栏（微信、通讯录、我）
- 实现首页聊天列表界面
- 实现通讯录界面
- 实现个人中心界面
- 实现聊天详情界面
- 支持中文简体界面

## 功能特性

- 仿微信UI设计，简洁美观
- 底部导航栏：微信、通讯录、我
- 聊天列表：显示聊天会话、未读消息数
- 聊天详情：支持发送和接收消息
- 通讯录：管理联系人，支持星标朋友
- 个人中心：展示个人信息入口
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

### v0.1.0
初始版本，详见上方版本说明

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
git tag v0.2.0 -m "0.2.0 新功能版本"
git push origin v0.2.0
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
    ├── screens/        # 页面
    │   ├── home/      # 首页
    │   ├── contacts/   # 通讯录
    │   ├── profile/    # 个人中心
    │   └── chat/      # 聊天详情
    └── theme/          # 主题样式
```
