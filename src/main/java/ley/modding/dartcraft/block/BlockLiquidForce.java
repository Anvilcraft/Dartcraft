package ley.modding.dartcraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.client.IconDirectory;
import ley.modding.dartcraft.entity.EntityColdChicken;
import ley.modding.dartcraft.entity.EntityColdCow;
import ley.modding.dartcraft.entity.EntityColdPig;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class BlockLiquidForce extends BlockFluidClassic {
    public IIcon still;
    public IIcon flowing;
    public IIcon milk;

    public BlockLiquidForce() {
        super(FluidRegistry.getFluid("liquidforce"), Material.water);
        Fluid liquidForce = FluidRegistry.getFluid("liquidforce");
        if (liquidForce != null)
            liquidForce.setBlock(DartBlocks.liquidforce);
        setResistance(2000.0F);
        setBlockName("liquidforce");
    }

    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return 15;
    }

    public void
    velocityToAddToEntity(World world, int x, int y, int z, Entity entity, Vec3 vec) {
        if (entity == null)
            return;
        try {
            double modifier = 0.85D;
            entity.motionX *= modifier;
            entity.motionY *= modifier;
            entity.motionZ *= modifier;
        } catch (Exception e) {}
    }

    public void
    onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        try {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase living = (EntityLivingBase) entity;
                if (living.isEntityUndead()
                    || living instanceof net.minecraft.entity.monster.EntityBlaze) {
                    living.addPotionEffect(
                        new PotionEffect(Potion.weakness.getId(), 1, 9, false)
                    );
                    /*if (Config.baneForce) {
                        living.attackEntityFrom((DamageSource)
                    PunishDamage.instance, 2.0F); } else {*/
                    living.attackEntityFrom(DamageSource.magic, 2.0F);
                    //}
                } else {
                    if (living.getAir() < 255)
                        living.setAir(living.getAir() + 1);
                    living.heal(0.005F);
                    living.removePotionEffect(Potion.invisibility.getId());
                    living.removePotionEffect(Potion.wither.getId());
                }
                if (Dartcraft.proxy.isSimulating(world)) {
                    float chance = 0.9925F;
                    if (living instanceof EntityColdCow
                        && living.getRNG().nextFloat() > chance) {
                        EntityColdCow cow = (EntityColdCow) living;
                        //cow.shouldRevert = true;
                    }
                    if (living instanceof EntityColdChicken
                        && living.getRNG().nextFloat() > chance) {
                        EntityColdChicken chicken = (EntityColdChicken) living;
                        //chicken.shouldRevert = true;
                    }
                    if (living instanceof EntityColdPig
                        && living.getRNG().nextFloat() > chance) {
                        EntityColdPig pig = (EntityColdPig) living;
                        //pig.shouldRevert = true;
                    }
                    if (living instanceof EntitySheep
                        && living.getRNG().nextFloat() > chance) {
                        EntitySheep sheep = (EntitySheep) living;
                        sheep.eatGrassBonus();
                    }
                    if (living instanceof EntityAgeable
                        && !(
                            living instanceof net.minecraft.entity.passive.EntityHorse
                        )) {
                        EntityAgeable animal = (EntityAgeable) living;
                        if (animal.getGrowingAge() < 0)
                            animal.setGrowingAge(
                                (animal.getGrowingAge() < -20)
                                    ? (animal.getGrowingAge() + 20)
                                    : 0
                            );
                    }
                }
            }
        } catch (Exception e) {}
    }

    public Fluid getFluid() {
        return FluidRegistry.getFluid("liquidforce");
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reggie) {
        still = reggie.registerIcon("dartcraft:liquidForceStill");
        flowing = reggie.registerIcon("dartcraft:liquidForceMoving");
        Fluid liquidForce = FluidRegistry.getFluid("liquidforce");
        if (liquidForce != null)
            liquidForce.setIcons(still, flowing);
        blockIcon = still;
        milk = reggie.registerIcon("dartcraft:milk");
        Fluid fmilk = FluidRegistry.getFluid("milk");
        if (fmilk != null && milk != null) {
            fmilk.setIcons(milk, milk);
        }
        IconDirectory.registerBlockTextures(reggie);
    }
}
