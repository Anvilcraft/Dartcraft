package ley.modding.dartcraft;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import ley.modding.dartcraft.block.DartBlocks;
import ley.modding.dartcraft.entity.*;
import ley.modding.dartcraft.event.EventHandler;
import ley.modding.dartcraft.internal.Registry;
import ley.modding.dartcraft.item.DartItems;
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
        registry = new Registry();
        DartItems.regsiter(registry);
        DartBlocks.register(registry);

        proxy.init();

        int entityId = 0;
        EntityRegistry.registerModEntity(EntityColdChicken.class, "coldChicken", entityId++, Dartcraft.instance, 40, 1, true);
        EntityRegistry.registerModEntity(EntityColdCow.class, "coldCow", entityId++, Dartcraft.instance, 40, 1, true);
        EntityRegistry.registerModEntity(EntityColdPig.class, "coldPig", entityId++, Dartcraft.instance, 40, 1, true);
        EntityRegistry.registerModEntity(EntityBottle.class, "entityBottleItem", entityId++, Dartcraft.instance, 40, 1, true);
        EntityRegistry.registerModEntity(EntityFlyingFlask.class, "entityFlyingFlask", entityId++, Dartcraft.instance, 40, 1, true);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {

    }

}
