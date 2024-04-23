package ley.modding.dartcraft.client.renderer.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import ley.modding.dartcraft.block.BlockForceTorch;
import ley.modding.dartcraft.block.DartBlocks;
import ley.modding.dartcraft.tile.TileEntityForceTorch;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class RenderBlockTorch
    extends BlockRenderer implements ISimpleBlockRenderingHandler {
    public static int RI;

    public boolean renderBlockTorch(Block block, int x, int y, int z) {
        IIcon[] torchIcons = ((BlockForceTorch) DartBlocks.forcetorch).icons;
        WorldClient world = null;
        IIcon icon = null;

        try {
            world = Minecraft.getMinecraft().theWorld;
            TileEntityForceTorch l = (TileEntityForceTorch) world.getTileEntity(x, y, z);
            icon = torchIcons[l.color];
            if (l.upgrades.hasKey("Camo")) {
                return false;
            }

            if (l.upgrades.hasKey("Heat")) {
                icon = torchIcons[1];
            }

            if (l.upgrades.hasKey("Healing")) {
                icon = torchIcons[6];
            }

            if (l.upgrades.hasKey("Bane")) {
                icon = torchIcons[14];
            }
        } catch (Exception var15) {}

        if (world != null && icon != null) {
            int l1 = world.getBlockMetadata(x, y, z);
            Tessellator tessellator = Tessellator.instance;
            tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
            tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
            double d0 = 0.4000000059604645D;
            double d1 = 0.5D - d0;
            double d2 = 0.20000000298023224D;
            if (l1 == 1) {
                this.renderTorchAtAngle(
                    icon, (double) x - d1, (double) y + d2, (double) z, -d0, 0.0D, 0
                );
            } else if (l1 == 2) {
                this.renderTorchAtAngle(
                    icon, (double) x + d1, (double) y + d2, (double) z, d0, 0.0D, 0
                );
            } else if (l1 == 3) {
                this.renderTorchAtAngle(
                    icon, (double) x, (double) y + d2, (double) z - d1, 0.0D, -d0, 0
                );
            } else if (l1 == 4) {
                this.renderTorchAtAngle(
                    icon, (double) x, (double) y + d2, (double) z + d1, 0.0D, d0, 0
                );
            } else {
                this.renderTorchAtAngle(
                    icon, (double) x, (double) y, (double) z, 0.0D, 0.0D, 0
                );
            }

            return true;
        } else {
            return false;
        }
    }

    public void renderTorchAtAngle(
        IIcon icon, double x, double y, double z, double par8, double par10, int par12
    ) {
        Tessellator tessellator = Tessellator.instance;
        double d5 = (double) icon.getMinU();
        double d6 = (double) icon.getMinV();
        double d7 = (double) icon.getMaxU();
        double d8 = (double) icon.getMaxV();
        double d9 = (double) icon.getInterpolatedU(7.0D);
        double d10 = (double) icon.getInterpolatedV(6.0D);
        double d11 = (double) icon.getInterpolatedU(9.0D);
        double d12 = (double) icon.getInterpolatedV(8.0D);
        double d13 = (double) icon.getInterpolatedU(7.0D);
        double d14 = (double) icon.getInterpolatedV(13.0D);
        double d15 = (double) icon.getInterpolatedU(9.0D);
        double d16 = (double) icon.getInterpolatedV(15.0D);
        x += 0.5D;
        z += 0.5D;
        double d17 = x - 0.5D;
        double d18 = x + 0.5D;
        double d19 = z - 0.5D;
        double d20 = z + 0.5D;
        double d21 = 0.0625D;
        double d22 = 0.625D;
        tessellator.addVertexWithUV(
            x + par8 * (1.0D - d22) - d21,
            y + d22,
            z + par10 * (1.0D - d22) - d21,
            d9,
            d10
        );
        tessellator.addVertexWithUV(
            x + par8 * (1.0D - d22) - d21,
            y + d22,
            z + par10 * (1.0D - d22) + d21,
            d9,
            d12
        );
        tessellator.addVertexWithUV(
            x + par8 * (1.0D - d22) + d21,
            y + d22,
            z + par10 * (1.0D - d22) + d21,
            d11,
            d12
        );
        tessellator.addVertexWithUV(
            x + par8 * (1.0D - d22) + d21,
            y + d22,
            z + par10 * (1.0D - d22) - d21,
            d11,
            d10
        );
        tessellator.addVertexWithUV(x + d21 + par8, y, z - d21 + par10, d15, d14);
        tessellator.addVertexWithUV(x + d21 + par8, y, z + d21 + par10, d15, d16);
        tessellator.addVertexWithUV(x - d21 + par8, y, z + d21 + par10, d13, d16);
        tessellator.addVertexWithUV(x - d21 + par8, y, z - d21 + par10, d13, d14);
        tessellator.addVertexWithUV(x - d21, y + 1.0D, d19, d5, d6);
        tessellator.addVertexWithUV(x - d21 + par8, y + 0.0D, d19 + par10, d5, d8);
        tessellator.addVertexWithUV(x - d21 + par8, y + 0.0D, d20 + par10, d7, d8);
        tessellator.addVertexWithUV(x - d21, y + 1.0D, d20, d7, d6);
        tessellator.addVertexWithUV(x + d21, y + 1.0D, d20, d5, d6);
        tessellator.addVertexWithUV(x + par8 + d21, y + 0.0D, d20 + par10, d5, d8);
        tessellator.addVertexWithUV(x + par8 + d21, y + 0.0D, d19 + par10, d7, d8);
        tessellator.addVertexWithUV(x + d21, y + 1.0D, d19, d7, d6);
        tessellator.addVertexWithUV(d17, y + 1.0D, z + d21, d5, d6);
        tessellator.addVertexWithUV(d17 + par8, y + 0.0D, z + d21 + par10, d5, d8);
        tessellator.addVertexWithUV(d18 + par8, y + 0.0D, z + d21 + par10, d7, d8);
        tessellator.addVertexWithUV(d18, y + 1.0D, z + d21, d7, d6);
        tessellator.addVertexWithUV(d18, y + 1.0D, z - d21, d5, d6);
        tessellator.addVertexWithUV(d18 + par8, y + 0.0D, z - d21 + par10, d5, d8);
        tessellator.addVertexWithUV(d17 + par8, y + 0.0D, z - d21 + par10, d7, d8);
        tessellator.addVertexWithUV(d17, y + 1.0D, z - d21, d7, d6);
    }

    @Override
    public void
    renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {}

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
        return this.renderBlockTorch(block, x, y, z);
    }

    @Override
    public boolean shouldRender3DInInventory(int alec) {
        return false;
    }

    @Override
    public int getRenderId() {
        return RI;
    }
}
