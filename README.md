# HypetOS3-freeform-unlock

LSPosed 模块，解除自由窗口（小窗）数量限制(本模块限制20)，仅在os3系统上测试过。


## 原理

Hook `com.android.server.wm.MiuiFreeFormStackDisplayStrategy#getMaxMiuiFreeFormStackCount`，该方法返回系统允许同时存在的 freeform 小窗数量上限。

## 依赖

| 组件 | 说明 |
| --- | --- |
| KernelSU / Magisk（带 Zygisk） | 底层 root 框架 |
| Zygisk Next（或原版 Zygisk） | Zygisk 实现 |
| LSPosed v2.x（Zygisk 版） | Xposed 框架，加载模块 |

## 安装

1. 从 [Releases](https://github.com/wumingmr/HyperOS3-freeform-unlock/releases) 下载最新版 `freeform-unlock.apk`
2. 安装 APK（`pm install -r freeform-unlock.apk`）
3. 打开 LSPosed Manager → 模块 → **Freeform Unlock** → 启用
4. 作用域已锁定为「系统框架」（`staticScope=true`），无需手动勾选
5. 软重启（或重启设备）生效

## 构建

需要 Android SDK 环境（`aapt2`、`d8`、`apksigner`、`zipalign`）及 libxposed API jar。

```bash
# 1. 下载 libxposed api jar
# 从 https://github.com/libxposed/api/releases 获取 (api-102.0.0.aar)

# 2. 编译
cd module
javac -cp libxposed-api.jar -d out \
  src/com/wumingmr/freeformunlock/MainHook.java

# 3. dex
d8 --release --classpath libxposed-api.jar --output dexout \
  out/com/wumingmr/freeformunlock/*.class

# 4. 打包 APK
aapt2 link -o module-unsigned.apk \
  --manifest AndroidManifest.xml \
  -I $ANDROID_HOME/platforms/android-35/android.jar \
  --min-sdk-version 29 --target-sdk-version 35
# 添加 dex + META-INF
zip -j module-unsigned.apk dexout/classes.dex
zip -r module-unsigned.apk META-INF

# 5. 签名
zipalign -f 4 module-unsigned.apk module-aligned.apk
apksigner sign --ks <keystore> --out module-signed.apk module-aligned.apk
```

## 卸载

LSPosed Manager 取消勾选 → 卸载 APK → 软重启。无系统残留。
