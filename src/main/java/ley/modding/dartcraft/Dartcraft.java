package ley.modding.dartcraft;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import ley.modding.dartcraft.block.BlockPowerOre;
import ley.modding.dartcraft.block.Blocks;
import ley.modding.dartcraft.event.EventHandler;
import ley.modding.dartcraft.internal.Registry;
import ley.modding.dartcraft.entity.EntityColdChicken;
import ley.modding.dartcraft.entity.EntityColdCow;
import ley.modding.dartcraft.entity.EntityColdPig;
import ley.modding.dartcraft.item.BaseItem;
import ley.modding.dartcraft.item.Items;
import ley.modding.dartcraft.item.tool.ItemForceMitts;
import ley.modding.dartcraft.item.tool.ItemForcePickaxe;
import ley.modding.dartcraft.item.tool.ItemForceShears;
import ley.modding.dartcraft.proxy.CommonProxy;
import ley.modding.dartcraft.tab.DartcraftTab;
import ley.modding.tileralib.api.IRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = Dartcraft.MODID, name = Dartcraft.MODNAME, version = Dartcraft.VERSION)
public class Dartcraft {
    public static final String MODID = "dartcraft";
    public static final String MODNAME = "Dartcraft";
    public static final String VERSION = "1.0";

    @Mod.Instance
    public static Dartcraft instance = new Dartcraft();
    public static DartcraftClient client = new DartcraftClient();
    public static IRegistry registry;
    @SidedProxy(serverSide = "ley.modding.dartcraft.proxy.CommonProxy", clientSide = "ley.modding.dartcraft.proxy.ClientProxy")
    public static CommonProxy proxy;

    public static CreativeTabs tab = new DartcraftTab();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        // TODO: use proxys
        if (e.getSide() == Side.CLIENT)
            client.init();

        registry = new Registry();
        Items.forcegem = registry.registerItem(new BaseItem("forcegem"));
        Items.forceingot = registry.registerItem(new BaseItem("forceingot"));
        Items.forcenugget = registry.registerItem(new BaseItem("forcenugget"));
        Items.forcestick = registry.registerItem(new BaseItem("forcestick"));
        Items.forceshard = registry.registerItem(new BaseItem("forceshard"));
        Items.forcemitts = registry.registerItem(new ItemForceMitts());
        Items.forcepickaxe = registry.registerItem(new ItemForcePickaxe());
        Items.forceshears = registry.registerItem(new ItemForceShears());
        Blocks.powerore = registry.registerBlock(new BlockPowerOre());

        int entityId = 0;
        EntityRegistry.registerModEntity(EntityColdChicken.class, "coldChicken", entityId++, Dartcraft.instance, 40, 1, true);
        EntityRegistry.registerModEntity(EntityColdCow.class, "coldCow", entityId++, Dartcraft.instance, 40, 1, true);
        EntityRegistry.registerModEntity(EntityColdPig.class, "coldPig", entityId++, Dartcraft.instance, 40, 1, true);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {

    }

}
