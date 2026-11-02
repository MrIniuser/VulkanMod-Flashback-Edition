// SPDX-License-Identifier: LGPL-3.0-only
package net.vulkanmod.compat.flashback;

import com.mojang.blaze3d.pipeline.RenderTarget;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** Only this small boundary reflects third-party classes; no named Minecraft members. */
public final class FlashbackViewport {
    private static Method isActive;
    private static Field x, y, w, h, viewportW, viewportH;
    private static boolean resolved;
    private static void resolve() throws ReflectiveOperationException {
        if (resolved) return;
        Class<?> ui = Class.forName("com.moulberry.flashback.editor.ui.ReplayUI");
        isActive = ui.getMethod("isActive");
        x = ui.getField("frameX"); y = ui.getField("frameY");
        w = ui.getField("frameWidth"); h = ui.getField("frameHeight");
        viewportW = ui.getField("viewportSizeX"); viewportH = ui.getField("viewportSizeY");
        resolved = true;
    }
    public static void composeIfActive(RenderTarget source) {
        try {
            resolve();
            if (!(boolean)isActive.invoke(null)) return;
            int width = w.getInt(null), height = h.getInt(null);
            int totalW = viewportW.getInt(null), totalH = viewportH.getInt(null);
            if (width <= 1 || height <= 1 || totalW <= 0 || totalH <= 0) return;
            float left = (float)x.getInt(null) / totalW, top = (float)y.getInt(null) / totalH;
            FlashbackBlitter.partialToMain(source, totalW, totalH, left, top,
                    left + (float)width / totalW, top + (float)height / totalH);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Flashback API mismatch: this bridge targets supplied Flashback 0.39.10", e);
        }
    }
}
