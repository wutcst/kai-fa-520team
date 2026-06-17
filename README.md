<div align="center">

# 🏰 World of Zuul

**一款基于 Java 的校园文字冒险游戏，支持图形化界面、中英双语指令、物品系统与数据库存档**

[![Java](https://img.shields.io/badge/Java-22-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)

</div>

---

## 📖 项目简介

World of Zuul 是一款经典文字冒险游戏的增强版本。玩家将置身于一座大学校园中，探索 **5 个相互连接的房间**，拾取和使用物品，解开谜题，并通过数据库保存和加载游戏进度。

项目采用 **Java Swing** 构建完整的图形化界面，支持 **中文/英文双语指令**，并实现了基于 **MySQL** 的存档系统。

## ✨ 功能亮点

### 🎮 游戏玩法
- 🗺️ **5 个精心设计的房间** — 校门、报告厅、校园吧、机房、办公室，各具特色
- 🎒 **物品系统** — 11 种物品散布在各房间，每件物品有独立的名称、描述和重量
- 🍪 **魔法饼干** — 随机出现在某个房间，吃下后可永久提升 5kg 负重上限
- ⚖️ **负重机制** — 初始负重 10kg，超过上限时系统给出提示
- ↩️ **历史回溯** — `back` 命令可逐层返回上一个房间，支持多级回退

### 🖥️ 图形化界面 (GUI)
- 🏠 每个房间配有独立的背景图片和物品精灵图
- 🎯 可点击的方向按钮和命令按钮
- 💬 打字机效果的半透明消息提示框
- 📦 背包对话框 — 网格式展示携带物品，点击即可丢弃
- 🌑 房间切换时的黑色淡入淡出过渡动画

### 🌐 双语指令支持
| 英文 | 中文 | 说明 |
|:---:|:---:|:---|
| `go east` | `前进 东` | 移动到指定方向 |
| `back` | `返回` | 回到上一个房间 |
| `take key` | `拾取 钥匙` | 拾取房间中的物品 |
| `drop key` | `丢弃 钥匙` | 丢弃携带的物品 |
| `look` | `查看` | 详细查看当前房间信息 |
| `items` | `物品` | 列出房间和背包中的所有物品 |
| `eat cookie` | `吃饼干` | 吃掉魔法饼干提升负重 |
| `save` | `保存` | 保存游戏进度到数据库 |
| `load` | `读取` | 读取游戏存档 |
| `help` | `帮助` | 显示所有可用命令 |

### 💾 数据库存档
- ✅ 保存/读取游戏进度（支持多存档槽位）
- ✅ 覆盖保存已有存档
- ✅ 查看所有存档列表
- ✅ 删除指定存档
- ✅ 自动创建数据库和表结构

## 🏗️ 项目架构

```
src/main/java/cn/edu/whut/sept/zuul/
├── Main.java                 # 程序入口
├── Game.java                 # 核心游戏逻辑与房间创建
├── Player.java               # 玩家模型（姓名、背包、负重）
├── Room.java                 # 房间模型（描述、出口、物品）
├── Item.java                 # 物品模型（名称、描述、重量）
├── Parser.java               # 指令解析器（中/英别名映射）
├── DBUtil.java               # MySQL 数据库工具类
├── gui/
│   └── GameFrame.java        # Swing 图形化界面
└── Command/
    ├── Command.java          # 指令值对象
    ├── CommandHandler.java   # 指令处理接口（策略模式）
    ├── CommandWords.java     # 指令注册表
    ├── GoCommand.java        # 移动指令
    ├── BackCommand.java      # 回溯指令（栈式历史）
    ├── LookCommand.java      # 查看指令
    ├── TakeCommand.java      # 拾取指令
    ├── DropCommand.java      # 丢弃指令
    ├── ItemsCommand.java     # 物品列表指令
    ├── EatCommand.java       # 进食指令
    ├── SaveCommand.java      # 保存指令
    ├── LoadCommand.java      # 读取指令
    ├── ListSavesCommand.java # 存档列表指令
    ├── DeleteSaveCommand.java# 删除存档指令
    ├── HelpCommand.java      # 帮助指令
    └── QuitCommand.java      # 退出指令
```

## 🧩 设计模式

| 模式 | 应用场景 | 说明 |
|:---|:---|:---|
| **策略模式 (Strategy)** | 指令系统 | 所有命令实现 `CommandHandler` 接口，通过 Map 注册，新增命令无需修改核心逻辑 |
| **命令模式 (Command)** | 用户输入 | 每条指令封装为独立对象，支持参数解析和执行 |
| **外观模式 (Facade)** | GUI 集成 | `Game.executeCommandLine()` 捕获控制台输出并封装为 `ExecutionResult`，解耦逻辑与表现 |

## 🛠️ 技术栈

| 类别 | 技术 |
|:---|:---|
| **语言** | Java 22 |
| **构建工具** | Apache Maven 3.9 |
| **GUI 框架** | Java Swing |
| **数据库** | MySQL 8.0 |
| **JDBC 驱动** | MySQL Connector/J 8.1.0 |
| **测试框架** | JUnit Jupiter 5.8.1 |
| **打包插件** | Maven Shade Plugin 3.5.1 (Fat JAR) |

## 🚀 快速开始

### 环境要求

- **JDK 22** 或更高版本
- **Apache Maven** 3.9+
- **MySQL** 8.0+ (运行在 `localhost:3306`)

### 1. 克隆项目

```bash
git clone https://github.com/your-username/zuul_2.git
cd zuul_2
```

### 2. 配置数据库

确保 MySQL 服务已启动。项目会自动创建 `zuul` 数据库和 `saves` 表。

> 默认连接信息：`root` / `123456`，如需修改请编辑 `src/main/java/.../DBUtil.java`

### 3. 构建项目

```bash
mvn clean package
```

### 4. 运行游戏

```bash
# 图形化界面模式（默认）
java -jar target/zuul-1.0-SNAPSHOT.jar

# 控制台模式
java -jar target/zuul-1.0-SNAPSHOT.jar --console
```

### 5. 运行测试

```bash
mvn test
```

## 🧪 测试覆盖

项目包含 **11 个测试类**，全面覆盖核心功能：

| 测试类 | 测试内容 |
|:---|:---|
| `GameTest` | 游戏初始化与核心逻辑 |
| `PlayerTest` | 玩家属性与负重计算 |
| `RoomTest` | 房间创建与出口设置 |
| `ItemTest` | 物品属性验证 |
| `ParserTest` | 指令解析与中英别名 |
| `CommandTest` | 指令值对象 |
| `CommandHandlerTest` | 指令处理逻辑 |
| `CommandWordsTest` | 指令注册表 |
| `CommandWordsSaveLoadTest` | 保存/读取指令 |
| `BoundaryTest` | 边界条件测试 |
| `ExceptionTest` | 异常处理测试 |

## 📁 图片资源

```
picture/
├── background/          # 房间背景图
│   ├── outside.png      # 校门
│   ├── theater.png      # 报告厅
│   ├── bar.png          # 校园吧
│   ├── lab.png          # 机房
│   └── office.png       # 办公室
├── items/               # 物品精灵图
│   ├── key.png, book.png, laptop.png ...
│   └── cookie.png       # 魔法饼干
└── bag.png              # 背包图标
```


---

<div align="center">



[⬆ 回到顶部](#-world-of-zuul)

</div>