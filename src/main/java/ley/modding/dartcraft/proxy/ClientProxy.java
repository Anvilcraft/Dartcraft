package ley.modding.dartcraft.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.block.DartBlocks;
import ley.modding.dartcraft.client.renderer.block.RenderBlockPowerOre;
import ley.modding.dartcraft.client.renderer.block.RenderBlockTorch;
import ley.modding.dartcraft.client.renderer.entity.RenderColdAnimal;
import ley.modding.dartcraft.client.renderer.entity.RenderEntityBottle;
import ley.modding.dartcraft.client.renderer.entity.RenderEntityFrozenItem;
import ley.modding.dartcraft.client.renderer.entity.RenderEntityTime;
import ley.modding.dartcraft.client.renderer.item.RenderItemEngine;
import ley.modding.dartcraft.client.renderer.item.RenderItemForceFlask;
import ley.modding.dartcraft.client.renderer.item.RenderItemInfuser;
import ley.modding.dartcraft.client.renderer.item.RenderItemMachine;
import ley.modding.dartcraft.client.renderer.tile.RenderTileEntityForceEngine;
import ley.modding.dartcraft.client.renderer.tile.RenderTileEntityInfuser;
import ley.modding.dartcraft.entity.EntityBottle;
import ley.modding.dartcraft.entity.EntityColdChicken;
import ley.modding.dartcraft.entity.EntityColdCow;
import ley.modding.dartcraft.entity.EntityColdPig;
import ley.modding.dartcraft.entity.EntityFlyingFlask;
import ley.modding.dartcraft.entity.EntityFrozenItem;
import ley.modding.dartcraft.entity.EntityTime;
import ley.modding.dartcraft.item.DartItems;
import ley.modding.dartcraft.tile.TileEntityForceEngine;
import ley.modding.dartcraft.tile.TileEntityInfuser;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.model.ModelCow;
import net.minecraft.client.model.ModelPig;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {
    public static RenderTileEntityForceEngine engineRender;
    public static RenderTileEntityInfuser infuserRender;

    @Override
    public boolean isSimulating(World world) {
        return world != null && !world.isRemote;
    }

    @Override
    public void bindTexture(String texture) {
        if (texture != null)
            this.getClientInstance().getTextureManager().bindTexture(
                new ResourceLocation(Dartcraft.MODID, texture)
            );
    }

    @Override
    public void init() {
        super.init();
        MinecraftForgeClient.registerItemRenderer(
            DartItems.forceflask, RenderItemForceFlask.instance
        );
        MinecraftForgeClient.registerItemRenderer(
            DartItems.entitybottle, RenderItemForceFlask.instance
        );
        MinecraftForgeClient.registerItemRenderer(
            Item.getItemFromBlock(DartBlocks.infuser), new RenderItemInfuser()
        );
        RenderEntityBottle bottleRenderer = new RenderEntityBottle();
        RenderingRegistry.registerEntityRenderingHandler(
            EntityBottle.class, bottleRenderer
        );
        RenderingRegistry.registerEntityRenderingHandler(
            EntityFlyingFlask.class, bottleRenderer
        );
        RenderingRegistry.registerEntityRenderingHandler(
            EntityColdChicken.class,
            new RenderColdAnimal(
                new ModelChicken(), 0.6f, "textures/entity/coldChicken.png"
            )
        );
        RenderingRegistry.registerEntityRenderingHandler(
            EntityColdCow.class,
            new RenderColdAnimal(new ModelCow(), 0.6f, "textures/entity/coldCow.png")
        );
        RenderingRegistry.registerEntityRenderingHandler(
            EntityColdPig.class,
            new RenderColdAnimal(new ModelPig(), 0.6f, "textures/entity/coldPig.png")
        );
        RenderingRegistry.registerEntityRenderingHandler(
            EntityTime.class, new RenderEntityTime()
        );
        RenderingRegistry.registerEntityRenderingHandler(
            EntityFrozenItem.class, new RenderEntityFrozenItem()
        );
        RenderBlockPowerOre.RI = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new RenderBlockPowerOre());
        RenderBlockTorch.RI = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new RenderBlockTorch());
        engineRender = new RenderTileEntityForceEngine();
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileEntityForceEngine.class, engineRender
        );
        infuserRender = new RenderTileEntityInfuser();
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileEntityInfuser.class, infuserRender
        );
        MinecraftForgeClient.registerItemRenderer(
            Item.getItemFromBlock(DartBlocks.engine), new RenderItemEngine()
        );
    }
}
