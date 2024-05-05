package ley.modding.dartcraft.client;

import java.util.stream.IntStream;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class IconDirectory {
    private static boolean itemTexturesRegistered;
    private static boolean blockTexturesRegistered;

    // Item Icons
    public static IIcon noSlot;

    // Block Icons
    public static IIcon[] machines;
    public static IIcon[] furnaces;

    public static void registerItemTextures(IIconRegister reg) {
        if (itemTexturesRegistered)
            return;
        itemTexturesRegistered = true;

        noSlot = reg.registerIcon("dartcraft:noSlot");
    }

    public static void registerBlockTextures(IIconRegister reg) {
        if (blockTexturesRegistered)
            return;
        blockTexturesRegistered = true;

        machines = IntStream.range(0, 16)
                       .mapToObj(i -> "dartcraft:machine" + i)
                       .map(reg::registerIcon)
                       .toArray(IIcon[] ::new);
        furnaces = IntStream.range(0, 16)
                       .mapToObj(i -> "dartcraft:furnace" + i)
                       .map(reg::registerIcon)
                       .toArray(IIcon[] ::new);
    }
}
