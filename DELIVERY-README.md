# 空间相册 · PICO 工程交付

交付日期：2026-11-15。包名：cn.mindsoul.spatialalbum。

## 打开与运行
1. 解压后，在 Android Studio 打开 project 文件夹，执行 Gradle Sync。
2. 使用 JDK21；安装 Android SDK35，配置本机 SDK 路径（Android Studio 可生成 local.properties；本包不携带原机器路径）。
3. 首次构建需要网络下载 Gradle 和 Maven 依赖。PICO Spatial SDK6.1.9、Kotlin2.1.20、AGP8.13.2、Gradle8.13。
4. 在 project 下运行 `./gradlew assembleDebug testDebugUnitTest`（Windows 使用 gradlew.bat）。
5. 可直接安装 APK/SpatialAlbum-debug.apk；或在 project 下执行：
   `pico-cli app install app/build/outputs/apk/debug/app-debug.apk --device emulator-5554`
   `pico-cli app launch cn.mindsoul.spatialalbum --device emulator-5554`
   替换设备编号为自己的目标。已在 PICO6.2.1 模拟器运行。

## 开屏动画
冷启动播放用户提供的本地视频。右上角“跳过”和底部“进入空间相册”均进入原有未登录首页；播放结束自动进入。退出开屏释放播放器，返回前台可继续播放。视频不会上传。

## 体验入口
- 默认未登录空态；点击“游客模式体验”展示25张本机空间照片和1段官方SBS视频示例。
- 左侧3列、每页最多9行（27项），滚动查看；超过27项可翻页。点击卡片在右侧预览。
- 支持图片/视频导入、真实容量统计、2D图片本机深度推断并生成左右眼空间照片。视频可选择MONO或SBS。
- 视频自动播放、暂停、重播；照片/视频圆角；视频贴面渲染保留极小防遮挡偏移。
- 沉浸画廊保留环绕、暂停和退出；相框已加入，照片中心高度2.0m。
- 登录注册弹窗：邮箱验证码字段；注册含密码与重复密码；用户协议、隐私政策和未勾选的同意选项。
- 应用Logo来自用户提供素材；透明前景融入首页。MindSoul品牌入口跳转https://mindsoul.cn。
- 意念生成：佩戴设备选择（博瑞恩/BrainLink Pro），左侧连接状态/EEG演示/任务时长/存储。9类情绪与0–100%专注度影响描述及示例风格参考。
- “购买脑机接口”按钮弹出联系PMTalk123购买或商务合作提示。

## 接口状态（请务必区分）
- 邮件登录/注册：对接 mindsoul 论坛接口（appId: space-album）；邮箱验证码由服务端发送。线上需后端部署后生效。照片仍仅本机，无云同步。
- 文字/意念生成：按用户确认先做前端；展示生成描述和已有素材参考，不产生新的AI图片。
- 脑机接口：继承旧PICO项目的模拟组件，没有真实蓝牙或设备SDK；选择型号仍显示未连接。
- EEG演示是合成数据，不是生理信号。主动勾选保存后，可在设备应用私有files/eeg-demo目录保存标记为SYNTHETIC DEMO的CSV。
- 本机2D转空间照片是真实Depth Anything V2推断；并非完整3D建模，遮挡与边缘可能存在误差。

## 验证与限制
- 本次新增开屏已完成编译和单元测试；安装时模拟器离线，开屏的实际播放与跳过交互尚未在模拟器验收。下列模拟器验证和截图来自新增开屏前的版本。
- 最新Debug APK构建成功；10项单元测试通过（含1项脚手架测试），测试XML位于verification/unit-tests。
- 已在模拟器看到：未登录页面、透明Logo、三列选择与悬停、协议、意念生成面板、购买入口；视频已恢复真实画面，圆角和紧凑进度条可见。
- 真实头显双目舒适度、所有导入格式与完整沉浸退出交互没有全部验收。
- Web视觉验收曾被浏览器策略阻止，用户已明确允许使用PICO模拟器验收；未将Web门禁记为通过。
- PICO托管场景流程发生Schema错误；独立本机构建成功。设计风格检查保留一项边框冲突：用户明确要求沉浸照片相框，因此保留，未声称完整规范检查全绿。

## 目录
- project/：完整可编辑工程（源码、Gradle wrapper、图片、视频、模型、测试、素材来源）。
- APK/：可安装Debug APK（开发签名，非商店正式版）。
- verification/：测试结果、最新模拟器截图（如已成功生成）、检查说明。
- SHA256SUMS.txt：归档内文件校验值。

素材来源见project/PHOTO-CREDITS.md和模型目录LICENSE.txt。官方视频为开发示例，不在此声明其独立商业再分发授权。发布前应完成真实账号/生成/硬件接入、正式签名及素材和协议的发行检查。
