package ley.modding.dartcraft.item;

import ley.modding.dartcraft.item.tool.ItemForceMitts;
import ley.modding.dartcraft.item.tool.ItemForcePickaxe;
import ley.modding.dartcraft.item.tool.ItemForceShears;
import ley.modding.tileralib.api.IRegistry;
import net.minecraft.item.Item;

public class Items {

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
        Items.forcegem = reg.registerItem(new BaseItem("forcegem"));
        Items.forceingot = reg.registerItem(new BaseItem("forceingot"));
        Items.forcenugget = reg.registerItem(new BaseItem("forcenugget"));
        Items.forcestick = reg.registerItem(new BaseItem("forcestick"));
        Items.forceshard = reg.registerItem(new BaseItem("forceshard"));
        Items.forcemitts = reg.registerItem(new ItemForceMitts());
        Items.forcepickaxe = reg.registerItem(new ItemForcePickaxe());
        Items.forceshears = reg.registerItem(new ItemForceShears());
        Items.forceflask = reg.registerItem(new ItemForceFlask());
        Items.entitybottle = reg.registerItem(new ItemEntityBottle());
    }
}
