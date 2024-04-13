package ley.modding.dartcraft.block;

import java.util.List;
import java.util.stream.IntStream;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.item.ItemBlockForceBrick;
import ley.modding.dartcraft.util.DartUtils;
import ley.modding.dartcraft.util.FXUtils;
import ley.modding.dartcraft.util.Util;
import ley.modding.tileralib.api.ICustomItemBlockProvider;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockForceBrick extends Block implements ICustomItemBlockProvider {
    public IIcon[] icons;

    public BlockForceBrick() {
        super(Material.rock);
        Util.configureBlock(this, "forcebrick");
        this.setHardness(2.0F);
        this.setResistance(2000.0F);
        this.setStepSound(Block.soundTypeStone);
    }

    @Override
    public boolean
    canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return meta >= 0 && meta < 16 ? this.icons[meta] : this.blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void getSubBlocks(Item par1, CreativeTabs tab, List list) {
        IntStream.range(0, 16)
            .mapToObj(i -> new ItemStack(this, 1, i))
            .forEach(list::add);
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
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

    // TODO: WTF
    //@Override
    @SideOnly(Side.CLIENT)
    public boolean addBlockDestroyEffects(
        World world, int x, int y, int z, int meta, EffectRenderer renderer
    ) {
        int color = world.getBlockMetadata(x, y, z);
        FXUtils.makeShiny(
            world,
            (double) x,
            (double) y,
            (double) z,
            2,
            DartUtils.getMcColor(color),
            32,
            true
        );
        return true;
    }

    // TODO: WTF
    //@Override
    @SideOnly(Side.CLIENT)
    public boolean addBlockHitEffects(
        World world, MovingObjectPosition target, EffectRenderer renderer
    ) {
        int color = world.getBlockMetadata(target.blockX, target.blockY, target.blockZ);
        FXUtils.makeShiny(
            world,
            (double) target.blockX,
            (double) target.blockY,
            (double) target.blockZ,
            2,
            DartUtils.getMcColor(color),
            4,
            true
        );
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reggie) {
        this.icons = IntStream.range(0, 16)
                         .mapToObj(i -> "dartcraft:brick" + i)
                         .map(reggie::registerIcon)
                         .toArray(IIcon[] ::new);
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockForceBrick.class;
    }
}
