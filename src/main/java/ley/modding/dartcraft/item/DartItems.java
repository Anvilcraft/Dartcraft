package ley.modding.dartcraft.item;

import ley.modding.dartcraft.block.DartBlocks;
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

    // Useless Items
    public static Item claw;
    public static Item fortune;
    public static Item goldenpower;
    public static Item resource;
    public static Item soulwafer;

    public static void regsiter(IRegistry reg) {
        DartItems.entitybottle = reg.registerItem(new ItemEntityBottle());
        DartItems.forceaxe = reg.registerItem(new ItemForceAxe());
        DartItems.forceflask = reg.registerItem(new ItemForceFlask());
        DartItems.forcegem = reg.registerItem(new BaseItem("forcegem"));
        DartItems.forceingot = reg.registerItem(new BaseItem("forceingot"));
        DartItems.forcemitts = reg.registerItem(new ItemForceMitts());
        DartItems.forcenugget = reg.registerItem(new BaseItem("forcenugget"));
        DartItems.forcepickaxe = reg.registerItem(new ItemForcePickaxe());
        DartItems.forceshard = reg.registerItem(new BaseItem("forceshard"));
        DartItems.forceshears = reg.registerItem(new ItemForceShears());
        DartItems.forceshovel = reg.registerItem(new ItemForceShovel());
        DartItems.forcestick = reg.registerItem(new BaseItem("forcestick"));

        DartItems.claw = reg.registerItem(new BaseItem("claw"));
        DartItems.clipboard = reg.registerItem(new ItemClipboard());
        DartItems.fortune = reg.registerItem(new BaseItem("fortune"));
        DartItems.goldenpower = reg.registerItem(new BaseItem("goldenpower"));
        DartItems.resource = reg.registerItem(new BaseItem("resource"));
        DartItems.soulwafer = reg.registerItem(new BaseItem("soulwafer"));
    }
}
