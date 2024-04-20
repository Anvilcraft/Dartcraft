package ley.modding.dartcraft.block;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import ley.modding.dartcraft.Config;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.item.AbstractItemBlockMetadata;
import ley.modding.dartcraft.item.DartItems;
import ley.modding.dartcraft.util.Util;
import ley.modding.tileralib.api.ICustomItemBlockProvider;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockPowerOre extends BaseBlock implements ICustomItemBlockProvider {
    public static final String ID = "powerore";

    public static IIcon powericon;

    public BlockPowerOre() {
        super(Material.rock);
        Util.configureBlock(this, ID);
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
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 1) {
            return Blocks.netherrack.getIcon(0, 0);
        }
        return Blocks.stone.getIcon(0, 0);
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
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        IntStream.rangeClosed(0, 1)
            .mapToObj(m -> new ItemStack(this, 1, m))
            .forEach(list::add);
    }

    @Override
    protected boolean canSilkHarvest() {
        return false;
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return BlockItem.class;
    }

    public static class BlockItem extends AbstractItemBlockMetadata {
        public BlockItem(Block block) {
            super(block);
        }

        @Override
        public String getID() {
            return ID;
        }
    }
}
