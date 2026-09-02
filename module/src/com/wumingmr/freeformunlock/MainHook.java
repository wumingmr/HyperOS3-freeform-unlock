package com.wumingmr.freeformunlock;

import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedModuleInterface;

import java.lang.reflect.Method;

/**
 * Freeform Unlock - libxposed API 版本
 *
 * hook com.android.server.wm.MiuiFreeFormStackDisplayStrategy#getMaxMiuiFreeFormStackCount
 * 该方法运行在 system_server 进程，返回系统允许的 freeform 小窗数量上限。
 * 默认手机为 2（桌面模式 4），这里将其放大到 MAX_FREEFORM。
 */
public class MainHook extends XposedModule {

    private static final int MAX_FREEFORM = 20;

    @Override
    public void onSystemServerStarting(XposedModuleInterface.SystemServerStartingParam param) {
        try {
            ClassLoader cl = param.getClassLoader();
            Class<?> strategy = Class.forName(
                    "com.android.server.wm.MiuiFreeFormStackDisplayStrategy", false, cl);
            Class<?> stack = Class.forName(
                    "com.android.server.wm.MiuiFreeFormActivityStack", false, cl);

            Method method = strategy.getDeclaredMethod(
                    "getMaxMiuiFreeFormStackCount", String.class, stack);
            method.setAccessible(true);

            hook(method).intercept(chain -> {
                int orig = (Integer) chain.proceed();
                return Math.max(orig, MAX_FREEFORM);
            });
        } catch (Throwable t) {
            // 框架会自动捕获并记录异常
        }
    }
}