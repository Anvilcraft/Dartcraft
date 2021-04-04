package ley.modding.dartcraft;

import cpw.mods.fml.client.registry.RenderingRegistry;
import ley.modding.dartcraft.client.renderer.entity.RenderColdAnimal;
import ley.modding.dartcraft.entity.EntityColdChicken;
import ley.modding.dartcraft.entity.EntityColdCow;
import ley.modding.dartcraft.entity.EntityColdPig;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.model.ModelCow;
import net.minecraft.client.model.ModelPig;

class DartcraftClient {
    public void init() {
        renderEntities();
    }

    public void renderEntities() {
        RenderingRegistry.registerEntityRenderingHandler(EntityColdChicken.class, new RenderColdAnimal(new ModelChicken(), 0.6f, "textures/entity/coldChicken.png"));
        RenderingRegistry.registerEntityRenderingHandler(EntityColdCow.class, new RenderColdAnimal(new ModelCow(), 0.6f, "textures/entity/coldCow.png"));
        RenderingRegistry.registerEntityRenderingHandler(EntityColdPig.class, new RenderColdAnimal(new ModelPig(), 0.6f, "textures/entity/coldPig.png"));
    }
}
