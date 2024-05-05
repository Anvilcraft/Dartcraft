package ley.modding.dartcraft.item;

import ley.modding.dartcraft.item.tool.ItemForceAxe;
import ley.modding.dartcraft.item.tool.ItemForceMitts;
import ley.modding.dartcraft.item.tool.ItemForcePickaxe;
import ley.modding.dartcraft.item.tool.ItemForceShears;
import ley.modding.dartcraft.item.tool.ItemForceShovel;
import ley.modding.tileralib.api.IRegistry;
import net.minecraft.item.Item;

public class DartItems {
    public static Item clipboard;
    public static Item entitybottle;
    public static Item forceaxe;
    public static Item forceflask;
    public static Item forcegem;
    public static Item forceingot;
    public static Item forcemitts;
    public static Item forcenugget;
    public static Item forcepickaxe;
    public static Item forceshard;
    public static Item forceshears;
    public static Item forceshovel;
    public static Item forcestick;
    public static Item forcetome;

    // Useless Items
    public static Item claw;
    public static Item fortune;
    public static Item goldenpower;
    public static Item resource;
    public static Item soulwafer;

    public static void regsiter(IRegistry reg) {
        entitybottle = reg.registerItem(new ItemEntityBottle());
        forceaxe = reg.registerItem(new ItemForceAxe());
        forceflask = reg.registerItem(new ItemForceFlask());
        forcegem = reg.registerItem(new BaseItem("forcegem"));
        forceingot = reg.registerItem(new BaseItem("forceingot"));
        forcemitts = reg.registerItem(new ItemForceMitts());
        forcenugget = reg.registerItem(new BaseItem("forcenugget"));
        forcepickaxe = reg.registerItem(new ItemForcePickaxe());
        forceshard = reg.registerItem(new BaseItem("forceshard"));
        forceshears = reg.registerItem(new ItemForceShears());
        forceshovel = reg.registerItem(new ItemForceShovel());
        forcestick = reg.registerItem(new BaseItem("forcestick"));
        forcetome = reg.registerItem(new ItemForceTome());

        claw = reg.registerItem(new BaseItem("claw"));
        clipboard = reg.registerItem(new ItemClipboard());
        fortune = reg.registerItem(new BaseItem("fortune"));
        goldenpower = reg.registerItem(new BaseItem("goldenpower"));
        resource = reg.registerItem(new BaseItem("resource"));
        soulwafer = reg.registerItem(new BaseItem("soulwafer"));
    }
}
