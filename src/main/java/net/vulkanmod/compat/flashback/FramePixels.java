// SPDX-License-Identifier: LGPL-3.0-only
package net.vulkanmod.compat.flashback;

import java.nio.ByteBuffer;

/** Byte-order independent RGBA/BGRA decoding; alpha is never forced opaque. */
public final class FramePixels {
    private FramePixels() {}
    public static int checkedByteSize(int width, int height) {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("Invalid frame dimensions");
        return Math.multiplyExact(Math.multiplyExact(width, height), 4);
    }
    public static int toABGR(ByteBuffer pixels, int offset, boolean bgra) {
        int r = Byte.toUnsignedInt(pixels.get(offset + (bgra ? 2 : 0)));
        int g = Byte.toUnsignedInt(pixels.get(offset + 1));
        int b = Byte.toUnsignedInt(pixels.get(offset + (bgra ? 0 : 2)));
        int a = Byte.toUnsignedInt(pixels.get(offset + 3));
        return (a << 24) | (b << 16) | (g << 8) | r;
    }
}
