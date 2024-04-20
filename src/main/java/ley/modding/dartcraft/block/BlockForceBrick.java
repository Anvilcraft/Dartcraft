package ley.modding.dartcraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.util.DartUtils;
import ley.modding.dartcraft.util.FXUtils;
import ley.modding.dartcraft.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockForceBrick extends BaseBlock {
    int type;

    public BlockForceBrick(int type) {
        super(Material.rock);
        Util.configureBlock(this, "forcebrick" + type);
        this.setHardness(2.0F);
        this.setResistance(2000.0F);
        this.setStepSound(Block.soundTypeStone);
        this.type = type;
    }

    @Override
    public boolean
    canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public void
    onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        this.velocityToAddToEntity(world, x, y, z, entity, (Vec3) null);
    }

    @Override
    public void
    velocityToAddToEntity(World world, int x, int y, int z, Entity entity, Vec3 vec) {
        double skateLimit = 0.5D;
        double step = 0.025D;
        entity.addVelocity(step * entity.motionX, 0.0D, step * entity.motionZ);
        if (entity.motionX > skateLimit) {
            entity.motionX = skateLimit;
        }

        if (entity.motionX < -skateLimit) {
            entity.motionX = -skateLimit;
        }

        if (entity.motionY > skateLimit) {
            entity.motionY = skateLimit;
        }

        if (entity.motionY < -skateLimit) {
            entity.motionY = -skateLimit;
        }

        if (entity.motionZ > skateLimit) {
            entity.motionZ = skateLimit;
        }

        if (entity.motionZ < -skateLimit) {
            entity.motionZ = -skateLimit;
        }
    }

    @Override
    public int getColor() {
        return DartUtils.getMcColor(this.type);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reggie) {
        this.blockIcon = reggie.registerIcon("dartcraft:brick" + this.type);
    }
}
