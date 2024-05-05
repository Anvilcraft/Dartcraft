package ley.modding.dartcraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.util.DartUtils;
import ley.modding.dartcraft.util.FXUtils;
import ley.modding.dartcraft.util.Util;
import net.anvilcraft.alec.jalec.factories.AlecCriticalRuntimeErrorExceptionFactory;
import net.anvilcraft.anvillib.vector.Vec3;
import net.anvilcraft.anvillib.vector.WorldVec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockForceSlab extends BlockSlab {
    int type;

    public BlockForceSlab(int type) {
        super(false, Material.rock);
        Util.configureBlock(this, "forceslab" + type);
        this.setHardness(2.0F);
        this.setResistance(2000.0F);
        this.setStepSound(Block.soundTypeStone);
        this.setLightOpacity(0);
        // TODO: WTF
        //Block.useNeighborBrightness[id] = true;
        this.type = type;
    }

    // TODO: WTF
    //@Override
    //public String getFullSlabName(int var1) {
    //    return "forceSlab";
    //}

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
            this.blockIcon = (this.type == 16 ? DartBlocks.forceplanks
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
    public String func_150002_b(int arg0) {
        throw AlecCriticalRuntimeErrorExceptionFactory.PLAIN.createAlecException(
            "Unknown Function called"
        );
    }
}
