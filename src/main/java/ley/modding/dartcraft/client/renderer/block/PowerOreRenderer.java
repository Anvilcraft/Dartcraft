package ley.modding.dartcraft.client.renderer.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import ley.modding.dartcraft.Config;
import ley.modding.dartcraft.block.BlockPowerOre;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class PowerOreRenderer
    extends BlockRenderer implements ISimpleBlockRenderingHandler {
    public void
    renderInventoryBlock(Block block, int meta, int modelID, RenderBlocks renderer) {
        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        renderer.setRenderBoundsFromBlock(block);
        IIcon bgIcon = null;
        if (meta == 1) {
            bgIcon = Blocks.netherrack.getIcon(0, 0);
        } else {
            bgIcon = Blocks.stone.getIcon(0, 0);
        }
        DrawFaces(renderer, block, bgIcon, false);
        GL11.glColor3f(1.0F, 1.0F, 0.0F);
        DrawFaces(renderer, block, BlockPowerOre.powericon, false);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean renderWorldBlock(
        IBlockAccess world,
        int x,
        int y,
        int z,
        Block block,
        int modelId,
        RenderBlocks renderer
    ) {
        BlockRenderer.setBrightness(world, x, y, z, block);
        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        renderer.setRenderBoundsFromBlock(block);
        renderer.renderStandardBlock(block, x, y, z);

        Tessellator tessy = Tessellator.instance;
        GL11.glEnable(3042);

        tessy.setColorOpaque(255, 255, 0);
        tessy.setBrightness(185);
        renderAllSides(world, x, y, z, block, renderer, BlockPowerOre.powericon, false);

        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        renderer.setRenderBoundsFromBlock(block);

        GL11.glDisable(3042);

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return Config.powerOreRenderID;
    }
}
