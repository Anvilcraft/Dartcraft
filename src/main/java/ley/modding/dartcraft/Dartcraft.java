package ley.modding.dartcraft;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import ley.modding.dartcraft.block.DartBlocks;
import ley.modding.dartcraft.entity.EntityBottle;
import ley.modding.dartcraft.entity.EntityColdChicken;
import ley.modding.dartcraft.entity.EntityColdCow;
import ley.modding.dartcraft.entity.EntityColdPig;
import ley.modding.dartcraft.entity.EntityFlyingFlask;
import ley.modding.dartcraft.entity.EntityFrozenItem;
import ley.modding.dartcraft.entity.EntityTime;
import ley.modding.dartcraft.event.EventHandler;
import ley.modding.dartcraft.handlers.TimeHandler;
import ley.modding.dartcraft.internal.Registry;
import ley.modding.dartcraft.item.DartItems;
import ley.modding.dartcraft.network.PacketClipButton;
import ley.modding.dartcraft.network.PacketDesocket;
import ley.modding.dartcraft.network.PacketFX;
import ley.modding.dartcraft.network.PacketInfuserStart;
import ley.modding.dartcraft.network.PacketUpdateAccessLevel;
import ley.modding.dartcraft.proxy.CommonProxy;
import ley.modding.dartcraft.tab.DartcraftTab;
import ley.modding.dartcraft.util.ForceEngineLiquids;
import ley.modding.dartcraft.util.FortunesUtil;
import ley.modding.dartcraft.worldgen.OreGen;
import ley.modding.tileralib.api.IRegistry;
import net.anvilcraft.anvillib.network.AnvilChannel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;

@Mod(
    modid = Dartcraft.MODID,
    name = Dartcraft.MODNAME,
    version = Dartcraft.VERSION,
    dependencies = "after:Thaumcraft;after:IC2"
)
public class Dartcraft {
    public static final String MODID = "dartcraft";
    public static final String MODNAME = "Dartcraft";
    public static final String VERSION = "1.0";

    @Mod.Instance
    public static Dartcraft instance
        = new Dartcraft();
    public static IRegistry registry;
    @SidedProxy(
        serverSide = "ley.modding.dartcraft.proxy.CommonProxy",
        clientSide = "ley.modding.dartcraft.proxy.ClientProxy"
    )
    public static CommonProxy proxy;

    public static AnvilChannel channel;

    public static CreativeTabs tab = new DartcraftTab();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(new TimeHandler());
        channel = new AnvilChannel("dartcraft");
        channel.register(PacketClipButton.class);
        channel.register(PacketFX.class);
        channel.register(PacketDesocket.class);
        channel.register(PacketUpdateAccessLevel.class);
        channel.register(PacketInfuserStart.class);
        FortunesUtil.load();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        registry = new Registry();
        DartBlocks.register(registry);
        DartItems.regsiter(registry);

        proxy.init();

        GameRegistry.registerWorldGenerator(new OreGen(), 2);

        int entityId = 0;
        EntityRegistry.registerModEntity(
            EntityColdChicken.class,
            "coldChicken",
            entityId++,
            Dartcraft.instance,
            40,
            1,
            true
        );
        EntityRegistry.registerModEntity(
            EntityColdCow.class, "coldCow", entityId++, Dartcraft.instance, 40, 1, true
        );
        EntityRegistry.registerModEntity(
            EntityColdPig.class, "coldPig", entityId++, Dartcraft.instance, 40, 1, true
        );
        EntityRegistry.registerModEntity(
            EntityBottle.class,
            "entityBottleItem",
            entityId++,
            Dartcraft.instance,
            40,
            1,
            true
        );
        EntityRegistry.registerModEntity(
            EntityFlyingFlask.class,
            "entityFlyingFlask",
            entityId++,
            Dartcraft.instance,
            40,
            1,
            true
        );
        EntityRegistry.registerModEntity(
            EntityTime.class, "entityTime", entityId++, Dartcraft.instance, 40, 1, true
        );
        EntityRegistry.registerModEntity(
            EntityFrozenItem.class,
            "entityFrozenItem",
            entityId++,
            Dartcraft.instance,
            40,
            1,
            true
        );
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        ForceEngineLiquids.load();
    }
}
