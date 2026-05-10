package ley.modding.dartcraft.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.item.DartItems;
import ley.modding.dartcraft.util.DartUtils;
import ley.modding.dartcraft.util.FXUtils;
import ley.modding.tileralib.api.ICustomItemBlockProvider;
import net.anvilcraft.anvillib.vector.Vec3;
import net.anvilcraft.anvillib.vector.WorldVec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockForceSlab extends BlockSlab implements ICustomItemBlockProvider {
    int type;

    public BlockForceSlab(boolean isDouble, int type) {
        super(isDouble, type >= 16 ? Material.wood : Material.rock);
        this.type = type;
        this.setBlockName(this.getBaseBlockName());
        this.setHardness(2.0F);
        this.setResistance(2000.0F);
        this.setStepSound(type >= 16 ? Block.soundTypeWood : Block.soundTypeStone);
        this.setLightOpacity(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(
        IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5
    ) {
        return !(
            par5 != 1 && par5 != 0
            && !super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5)
        );
    }

    @Override
    public boolean
    canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public IIcon getIcon(int alec, int meta) {
        if (this.blockIcon == null)
            this.blockIcon = (this.type >= 16 ? DartBlocks.forceplanks
                                              : DartBlocks.forcebrick[this.type])
                                 .getIcon(0, 0);
        return this.blockIcon;
    }

    @Override
    public void registerBlockIcons(IIconRegister arg0) {}

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(
        World world, int x, int y, int z, int meta, EffectRenderer renderer
    ) {
        FXUtils.makeShiny(
            new WorldVec(world, x, y, z), 2, DartUtils.getMcColor(this.type), 32, true
        );
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean
    addHitEffects(World world, MovingObjectPosition mop, EffectRenderer renderer) {
        FXUtils.makeShiny(
            new Vec3(mop).withWorld(world), 2, DartUtils.getMcColor(this.type), 4, true
        );
        return true;
    }

    @Override
    public String func_150002_b(int damage) {
        return "tile." + this.getBaseBlockName();
    }

    private String getBaseBlockName() {
        return (this.field_150004_a ? "double" : "") + "forceslab" + this.type;
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        // we register slab item blocks ourselves
        return null;
    }

    @Override
    public Item getItemDropped(int alec1, Random alec2, int alec3) {
        return DartItems.forceslabs[this.type];
    }
}
