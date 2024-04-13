package ley.modding.dartcraft.block;

import ley.modding.dartcraft.Config;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.item.DartItems;
import ley.modding.dartcraft.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class BlockPowerOre extends Block {
    public static IIcon stoneicon;
    public static IIcon nethericon;
    public static IIcon powericon;

    public BlockPowerOre() {
        super(Material.rock);
        Util.configureBlock(this, "powerore");
        setHardness(3.0F);
        setResistance(10.0F);
        setStepSound(soundTypeStone);
    }

    @Override
    public Item getItemDropped(int par1, Random rand, int par3) {
        return DartItems.forcegem;
    }

    @Override
    public int quantityDropped(Random rand) {
        return rand.nextInt(3) + 2;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        powericon = reg.registerIcon(Dartcraft.MODID + ":powerore");
        stoneicon = reg.registerIcon("stone");
        nethericon = reg.registerIcon("netherrack");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 1) {
            return nethericon;
        }
        return stoneicon;
    }

    @Override
    public int getRenderType() {
        return Config.powerOreRenderID;
    }

    @Override
    public void dropXpOnBlockBreak(World world, int x, int y, int z, int exp) {
        exp = world.rand.nextInt(3) + 2;
        if (Dartcraft.proxy.isSimulating(world)) {
            while (exp > 0) {
                int amt = EntityXPOrb.getXPSplit(exp);
                exp -= amt;
                world.spawnEntityInWorld(
                    new EntityXPOrb(world, x + 0.5D, y + 0.5D, z + 0.5D, amt)
                );
            }
        }
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
    }

    @Override
    protected boolean canSilkHarvest() {
        return false;
    }
}
