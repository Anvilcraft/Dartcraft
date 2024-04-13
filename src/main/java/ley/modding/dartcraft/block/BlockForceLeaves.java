package ley.modding.dartcraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.util.Util;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import java.util.Random;

public class BlockForceLeaves extends BlockLeaves {
    private IIcon icon;

    public BlockForceLeaves() {
        Util.configureBlock(this, "forceleaves");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        this.icon = register.registerIcon(Dartcraft.MODID + ":leaves");
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(
        IBlockAccess p_149646_1_,
        int p_149646_2_,
        int p_149646_3_,
        int p_149646_4_,
        int p_149646_5_
    ) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return this.icon;
    }

    // this seems to be getting the variants or something...
    @SideOnly(Side.CLIENT)
    @Override
    public String[] func_150125_e() {
        return new String[] { "force" };
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getBlockColor() {
        return 0xffffff;
    }

    @SideOnly(Side.CLIENT)
    public int getRenderColor(int p_149741_1_) {
        return 0xffffff;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int colorMultiplier(
        IBlockAccess p_149720_1_, int p_149720_2_, int p_149720_3_, int p_149720_4_
    ) {
        return 0xffffff;
    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return Item.getItemFromBlock(DartBlocks.forcesapling);
    }
}
