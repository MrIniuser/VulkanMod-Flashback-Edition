import net.vulkanmod.compat.flashback.FramePixels;
import net.vulkanmod.compat.flashback.UiGeometry;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class CoreSelfTest {
    private static int tests;
    static void check(boolean ok, String label) {
        tests++;
        if (!ok) throw new AssertionError(label);
    }
    static void rejects(Runnable action, String label) {
        boolean failed = false;
        try { action.run(); } catch (IllegalArgumentException | ArithmeticException expected) { failed = true; }
        check(failed, label);
    }
    public static void main(String[] args) {
        ByteBuffer rgba = ByteBuffer.wrap(new byte[]{1,2,3,4,(byte)255,0,(byte)128,0});
        check(FramePixels.toABGR(rgba, 0, false) == 0x04030201, "RGBA channel order");
        check(FramePixels.toABGR(rgba, 0, true) == 0x04010203, "BGRA channel order");
        check(FramePixels.toABGR(rgba, 4, false) == 0x008000ff, "Transparent alpha preserved");
        rgba.order(ByteOrder.BIG_ENDIAN);
        check(FramePixels.toABGR(rgba, 0, false) == 0x04030201, "No machine endian assumption");
        check(FramePixels.checkedByteSize(1920,1080) == 8294400, "1080p allocation");
        rejects(() -> FramePixels.checkedByteSize(0,1), "Zero size rejected");
        rejects(() -> FramePixels.checkedByteSize(-1,2), "Negative size rejected");
        rejects(() -> FramePixels.checkedByteSize(100000,100000), "Allocation overflow rejected");
        ByteBuffer in = ByteBuffer.allocate(44).order(ByteOrder.nativeOrder());
        in.putInt(123); // nonzero input position must be honored
        in.putFloat(10).putFloat(20).putFloat(0).putFloat(0).put(new byte[]{1,2,3,4});
        in.putFloat(110).putFloat(70).putFloat(1).putFloat(1).put(new byte[]{5,6,7,8});
        in.flip().position(4);
        ByteBuffer out = ByteBuffer.allocate(48).order(ByteOrder.nativeOrder());
        UiGeometry.convertVertices(in,out,2,10,20,100,50);
        check(out.position() == 48, "Minecraft vertex stride 24");
        check(in.position() == 4, "Input buffer position preserved");
        check(out.getFloat(0) == -1 && out.getFloat(4) == 1, "Top-left projection");
        check(out.getFloat(24) == 1 && out.getFloat(28) == -1, "Bottom-right projection");
        check(out.getFloat(12) == 0 && out.getFloat(36) == 1, "Texture coordinates preserved");
        check(out.get(20) == 1 && out.get(47) == 8, "RGBA vertex bytes preserved");
        rejects(() -> UiGeometry.convertVertices(in,ByteBuffer.allocate(1),2,0,0,1,1), "Truncated output rejected");
        rejects(() -> UiGeometry.convertVertices(in,ByteBuffer.allocate(48),2,0,0,0,1), "Invalid viewport rejected");
        var clip = UiGeometry.clip(10,20,30,40,0,0,2,2,200,200);
        check(clip.equals(new UiGeometry.Clip(20,120,40,40)), "Retina bottom-left scissor");
        clip = UiGeometry.clip(-20,-20,120,120,0,0,1,1,100,100);
        check(clip.equals(new UiGeometry.Clip(0,0,100,100)), "Scissor clamped");
        check(UiGeometry.clip(101,101,110,110,0,0,1,1,100,100) == null, "Invisible command clipped");
        clip = UiGeometry.clip(11,21,20,30,10,20,1,1,100,100);
        check(clip.equals(new UiGeometry.Clip(1,90,9,9)), "Display origin respected");
        System.out.println("PASS: " + tests + " CPU checks. These do NOT validate compilation of the mod or in-game compatibility.");
    }
}
