package ley.modding.dartcraft.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.Config;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.client.renderer.block.RenderBlockTorch;
import ley.modding.dartcraft.item.AbstractItemBlockMetadata;
import ley.modding.dartcraft.tile.TileEntityForceTorch;
import ley.modding.dartcraft.util.DartUtils;
import ley.modding.dartcraft.util.Util;
import ley.modding.tileralib.api.ICustomItemBlockProvider;
import ley.modding.tileralib.api.ITEProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockForceTorch
    extends BlockTorch implements ITEProvider, ICustomItemBlockProvider {
    public static final String ID = "forcetorch";

    public static HashMap<String, int[]> upgrades = new HashMap<>();
    static {
        upgrades.put("Heat", new int[] { 16, 1 });
        upgrades.put("Healing", new int[] { 17, 6 });
        upgrades.put("Bane", new int[] { 18, 14 });
        upgrades.put("Camo", new int[] { 19, 0 });
        upgrades.put("Repair", new int[] { 20, 4 });
        upgrades.put("Time", new int[] { 21, 12 });
    }

    public IIcon[] icons;

    public BlockForceTorch() {
        super();
        Util.configureBlock(this, ID);
        this.setResistance(2000.0F);
        this.setLightLevel(1.0F);
    }

    @Override
    public int getRenderType() {
        return RenderBlockTorch.RI;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return 15;
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new TileEntityForceTorch();
    }

    @Override
    public void onBlockPlacedBy(
        World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack
    ) {
        super.onBlockPlacedBy(world, x, y, z, entity, stack);
        NBTTagCompound upgrades = new NBTTagCompound();
        int color = stack.getItemDamage();
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityForceTorch) {
            try {
                for (String name : BlockForceTorch.upgrades.keySet()) {
                    int[] ints = (int[]) BlockForceTorch.upgrades.get(name);
                    if (stack.getItemDamage() == ints[0]) {
                        color = ints[1];
                        upgrades.setInteger(name, 1);
                        break;
                    }
                }
            } catch (Exception var13) {}

            TileEntityForceTorch torch1 = (TileEntityForceTorch) tile;
            torch1.upgrades = upgrades;
            torch1.color = (byte) color;
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block par5, int meta) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityForceTorch) {
            TileEntityForceTorch torch = (TileEntityForceTorch) tile;
            ItemStack stack = new ItemStack(this, 1, torch.color);
            if (torch.upgrades != null && !torch.upgrades.hasNoTags()) {
                try {
                    int e = -1;
                    for (String key : BlockForceTorch.upgrades.keySet()) {
                        if (torch.upgrades.hasKey(key)) {
                            e = ((int[]) upgrades.get(key))[0];
                            break;
                        }
                    }

                    if (e > 0) {
                        stack.setItemDamage(e);
                    }
                } catch (Exception var13) {}
            }

            DartUtils.dropItem(stack, world, (double) x, (double) y, (double) z);
            world.removeTileEntity(x, y, z);
        }
    }

    @Override
    public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
        return false;
    }

    @Override
    public ArrayList<ItemStack>
    getDrops(World alec0, int alec1, int alec2, int alec3, int alec4, int alec5) {
        return new ArrayList<>();
    }

    public IIcon getIconForColor(int color) {
        switch (color) {
            case 16: // heat -> red
                return this.icons[1];

            case 17: // healing -> cyan
                return this.icons[6];

            case 18: // bane -> orange
                return this.icons[14];

            case 19: // camo -> black
                return this.icons[0];

            case 20: // aspect -> blue
               return this.icons[4];

            case 21: // time -> light blue
               return this.icons[12];

            default:
                return color >= 0 && color < this.icons.length ? this.icons[color]
                                                               : this.blockIcon;
        }
    }

    @Override
    public IIcon getIcon(int alec, int meta) {
        return this.getIconForColor(meta);
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        try {
            return this.getIconForColor(
                ((TileEntityForceTorch) world.getTileEntity(x, y, z)).color
            );
        } catch (Exception var7) {
            return this.blockIcon;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void getSubBlocks(Item id, CreativeTabs tab, List list) {
        IntStream.range(0, Config.timeUpgradeTorch ? 22 : 21)
            .mapToObj(m -> new ItemStack(this, 1, m))
            .forEach(list::add);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reggie) {
        this.icons = IntStream.range(0, 16)
                         .mapToObj(i -> "dartcraft:forcetorch" + i)
                         .map(reggie::registerIcon)
                         .toArray(IIcon[] ::new);

        this.blockIcon = this.icons[0];
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        try {
            TileEntityForceTorch e = (TileEntityForceTorch) world.getTileEntity(x, y, z);
            if (e.upgrades.hasKey("Camo")) {
                return;
            }
        } catch (Exception var7) {}

        super.randomDisplayTick(world, x, y, z, rand);
    }

    @Override
    public boolean onBlockActivated(
        World world,
        int x,
        int y,
        int z,
        EntityPlayer player,
        int par6,
        float par7,
        float par8,
        float par9
    ) {
        try {
            TileEntity e = world.getTileEntity(x, y, z);
            if (e != null && e instanceof TileEntityForceTorch) {
                TileEntityForceTorch torch = (TileEntityForceTorch) e;
                if (torch.upgrades.hasKey("Time") && Config.timeUpgradeTorch) {
                    if (Dartcraft.proxy.isSimulating(world)) {
                        ++torch.timeType;
                        if (torch.timeType > 4) {
                            torch.timeType = 0;
                        }

                        String message = "Time mode: ";
                        switch (torch.timeType) {
                            case 0:
                            default:
                                message = message + "None";
                                break;
                            case 1:
                                message = message + "Stop";
                                break;
                            case 2:
                                message = message + "Slow";
                                break;
                            case 3:
                                message = message + "Fast";
                                break;
                            case 4:
                                message = message + "Hyper";
                        }

                        Dartcraft.proxy.sendChatToPlayer(player, message);
                    }

                    return true;
                }
            }
        } catch (Exception var13) {
            var13.printStackTrace();
        }

        return false;
    }

    @Override
    public Class<? extends TileEntity> getTEClass() {
        return TileEntityForceTorch.class;
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
