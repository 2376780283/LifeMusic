# 🎵 LifeMusic - Android 音乐播放器

一款简洁美观的 Android 音乐播放应用，让你的生活充满旋律。

![screenshot](screenshots/banner.png) <!-- 可替换为你的应用截图 -->

---

## 📱 应用介绍

**LifeMusic** 是为 Android 平台开发的本地音乐播放器，主打简洁 UI 与流畅体验。无广告、无打扰，只为让你专注于音乐本身。

---

## ✨ 核心功能

- 🎧 本地音乐扫描与播放
- 📁 歌单管理（创建 / 编辑 / 删除）
- 🔁 支持顺序播放、单曲循环、随机播放
- 🎼 显示音乐信息（标题、专辑、艺术家）
- 🌙 深色 / 浅色主题切换
- 🔊 支持后台播放与通知栏控制
- 🎚️ 可视化音频频谱（可选）

---

## 📸 界面预览

| 首页 | 播放界面 | 歌单界面 |
|------|----------|----------|
| ![](screenshots/home.png) | ![](screenshots/player.png) | ![](screenshots/playlist.png) |

---

## 🧱 技术栈

- **语言**：Kotlin / Java
- **架构**：MVVM（Model-View-ViewModel）
- fetrue：**UI**：Jetpack Compose / XML Layout
- **数据存储**：Room / SharedPreferences
- **媒体播放**：MediaPlayer / ExoPlayer
- **其他**：LiveData、ViewModel、DataBinding

---

## 📦 项目结构（示例）

```bash
LifeMusic/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/zzh/lifeplayer/music/
│   │   │   │   ├── activity/          # UI 层（Activity/Fragment）
│   │   │   │   ├── viewmodel/   # 视图模型
│   │   │   │   ├── data/        # 数据层（数据库/实体）
│   │   │   │   ├── service/     # 播放服务与后台控制
│   │   │   │   └── util/       # 工具类
│   │   │   └── res/             # 布局、图标、主题
├── build.gradle
└── README.md
