// SPDX-License-Identifier: LGPL-3.0-only
package net.vulkanmod.compat.flashback;

import java.nio.ByteBuffer;

/** CPU-only ImGui ABI conversion, separated so it can be tested without Minecraft. */
public final class UiGeometry {
    private UiGeometry() {}
    public static void convertVertices(ByteBuffer input, ByteBuffer output, int count,
                                       float originX, float originY, float width, float height) {
        if (count < 0 || !(width > 0) || !(height > 0)) throw new IllegalArgumentException("Invalid draw data");
        if ((long)count * 20 > input.remaining() || (long)count * 24 > output.remaining())
            throw new IllegalArgumentException("Truncated vertex buffer");
        for (int i = 0; i < count; i++) {
            int offset = input.position() + i * 20;
            float x = (input.getFloat(offset) - originX) / width;
            float y = (input.getFloat(offset + 4) - originY) / height;
            output.putFloat(x * 2 - 1).putFloat(1 - y * 2).putFloat(0);
            output.putFloat(input.getFloat(offset + 8)).putFloat(input.getFloat(offset + 12));
            for (int c = 0; c < 4; c++) output.put(input.get(offset + 16 + c));
        }
    }
    public record Clip(int x, int y, int width, int height) {}
    public static Clip clip(float left, float top, float right, float bottom,
                             float originX, float originY, float scaleX, float scaleY, int width, int height) {
        int x1 = Math.max(0, (int)Math.floor((left - originX) * scaleX));
        int y1 = Math.max(0, (int)Math.floor((top - originY) * scaleY));
        int x2 = Math.min(width, (int)Math.ceil((right - originX) * scaleX));
        int y2 = Math.min(height, (int)Math.ceil((bottom - originY) * scaleY));
        if (x2 <= x1 || y2 <= y1) return null;
        return new Clip(x1, height - y2, x2 - x1, y2 - y1);
    }
}
