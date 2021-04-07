package ley.modding.dartcraft.item;

import ley.modding.dartcraft.item.tool.ItemForceMitts;
import ley.modding.dartcraft.item.tool.ItemForcePickaxe;
import ley.modding.dartcraft.item.tool.ItemForceShears;
import ley.modding.tileralib.api.IRegistry;
import net.minecraft.item.Item;

public class DartItems {

    public static Item forcegem;
    public static Item forceingot;
    public static Item forcenugget;
    public static Item forcestick;
    public static Item forceshard;
    public static Item forceshears;
    public static Item forcepickaxe;
    public static Item forcemitts;
    public static Item forceflask;
    public static Item entitybottle;

    public static void regsiter(IRegistry reg) {
        DartItems.forcegem = reg.registerItem(new BaseItem("forcegem"));
        DartItems.forceingot = reg.registerItem(new BaseItem("forceingot"));
        DartItems.forcenugget = reg.registerItem(new BaseItem("forcenugget"));
        DartItems.forcestick = reg.registerItem(new BaseItem("forcestick"));
        DartItems.forceshard = reg.registerItem(new BaseItem("forceshard"));
        DartItems.forcemitts = reg.registerItem(new ItemForceMitts());
        DartItems.forcepickaxe = reg.registerItem(new ItemForcePickaxe());
        DartItems.forceshears = reg.registerItem(new ItemForceShears());
        DartItems.forceflask = reg.registerItem(new ItemForceFlask());
        DartItems.entitybottle = reg.registerItem(new ItemEntityBottle());
    }
}
