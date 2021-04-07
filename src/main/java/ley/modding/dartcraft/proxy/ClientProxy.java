package ley.modding.dartcraft.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import ley.modding.dartcraft.Config;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.client.renderer.block.PowerOreRenderer;
import ley.modding.dartcraft.client.renderer.entity.RenderColdAnimal;
import ley.modding.dartcraft.client.renderer.entity.RenderEntityBottle;
import ley.modding.dartcraft.client.renderer.item.RenderItemForceFlask;
import ley.modding.dartcraft.entity.*;
import ley.modding.dartcraft.item.DartItems;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.model.ModelCow;
import net.minecraft.client.model.ModelPig;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {

    public boolean isSimulating(World world) {
        return world != null && !world.isRemote;
    }

    public void bindTexture(String texture) {
        if (texture != null)
            getClientInstance().getTextureManager().bindTexture(new ResourceLocation(Dartcraft.MODID, texture));
    }

    public void init() {
        super.init();
        MinecraftForgeClient.registerItemRenderer(DartItems.forceflask, RenderItemForceFlask.instance);
        MinecraftForgeClient.registerItemRenderer(DartItems.entitybottle, RenderItemForceFlask.instance);
        RenderEntityBottle bottleRenderer = new RenderEntityBottle();
        RenderingRegistry.registerEntityRenderingHandler(EntityBottle.class, bottleRenderer);
        RenderingRegistry.registerEntityRenderingHandler(EntityFlyingFlask.class, bottleRenderer);
        RenderingRegistry.registerEntityRenderingHandler(EntityColdChicken.class, new RenderColdAnimal(new ModelChicken(), 0.6f, "textures/entity/coldChicken.png"));
        RenderingRegistry.registerEntityRenderingHandler(EntityColdCow.class, new RenderColdAnimal(new ModelCow(), 0.6f, "textures/entity/coldCow.png"));
        RenderingRegistry.registerEntityRenderingHandler(EntityColdPig.class, new RenderColdAnimal(new ModelPig(), 0.6f, "textures/entity/coldPig.png"));
        Config.powerOreRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new PowerOreRenderer());
    }

}
