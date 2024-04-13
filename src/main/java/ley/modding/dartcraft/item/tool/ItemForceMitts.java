package ley.modding.dartcraft.item.tool;

import cpw.mods.fml.common.eventhandler.Event;
import forestry.api.arboriculture.IToolGrafter;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.api.IBreakable;
import ley.modding.dartcraft.item.DartItems;
import ley.modding.dartcraft.util.EntityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.player.UseHoeEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ItemForceMitts extends ItemTool implements IBreakable, IToolGrafter {
    private static int damage = 0;
    private static float efficiency = 16.0F;
    private static int toolLevel = 4;
    public static ToolMaterial material = EnumHelper.addToolMaterial(
        "FORCE", toolLevel, 256, efficiency, (float) damage, 0
    );
    public ArrayList<Block> mineableBlocks = new ArrayList<Block>();
    private int range = 2;

    public ItemForceMitts() {
        super(0.0F, material, new HashSet());
        setCreativeTab(Dartcraft.tab);
        loadMinables();
        efficiencyOnProperMaterial = efficiency;
        setUnlocalizedName("forcemitts");
        setTextureName(Dartcraft.MODID + ":forcemitts");
    }

    @Override
    public ItemStack itemReturned() {
        return new ItemStack(DartItems.forceshard);
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        Set<String> tools = new HashSet<String>();
        tools.add("pickaxe");
        tools.add("axe");
        tools.add("shovel");
        return tools;
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass) {
        return 4;
    }

    private void loadMinables() {
        this.mineableBlocks.add(Blocks.cobblestone);
        this.mineableBlocks.add(Blocks.stone);
        this.mineableBlocks.add(Blocks.sandstone);
        this.mineableBlocks.add(Blocks.mossy_cobblestone);
        this.mineableBlocks.add(Blocks.iron_ore);
        this.mineableBlocks.add(Blocks.coal_ore);
        this.mineableBlocks.add(Blocks.ice);
        this.mineableBlocks.add(Blocks.brick_block);
        this.mineableBlocks.add(Blocks.glowstone);
        this.mineableBlocks.add(Blocks.grass);
        this.mineableBlocks.add(Blocks.dirt);
        this.mineableBlocks.add(Blocks.mycelium);
        this.mineableBlocks.add(Blocks.sand);
        this.mineableBlocks.add(Blocks.gravel);
        this.mineableBlocks.add(Blocks.snow);
        this.mineableBlocks.add(Blocks.snow);
        this.mineableBlocks.add(Blocks.clay);
        this.mineableBlocks.add(Blocks.stonebrick);
        this.mineableBlocks.add(Blocks.soul_sand);
        this.mineableBlocks.add(Blocks.leaves);
    }

    @Override
    public boolean canHarvestBlock(Block block, ItemStack itemStack) {
        return this.mineableBlocks.contains(block)
            || block.getMaterial() == Material.leaves
            || block.getMaterial() == Material.wood;
    }

    @Override
    public boolean
    hitEntity(ItemStack itemStack, EntityLivingBase entity, EntityLivingBase player) {
        return true;
    }

    @Override
    public float getDigSpeed(ItemStack stack, Block block, int meta) {
        if (!ForgeHooks.isToolEffective(stack, block, meta)
            && !this.canHarvestBlock(block, stack)) {
            return 1.0F;
        } else {
            return this.efficiencyOnProperMaterial; //TODO Upgrades
        }
    }

    @Override
    public boolean onItemUse(
        ItemStack stack,
        EntityPlayer player,
        World world,
        int x,
        int y,
        int z,
        int par7,
        float par8,
        float par9,
        float par10
    ) {
        if (!player.canPlayerEdit(x, y, z, par7, stack)) {
            return false;
        } else {
            UseHoeEvent event = new UseHoeEvent(player, stack, world, x, y, z);
            if (MinecraftForge.EVENT_BUS.post(event)) {
                return false;
            } else if (event.getResult() == Event.Result.ALLOW) {
                stack.damageItem(1, player);
                return true;
            }
        }

        Block block = world.getBlock(x, y, z);

        if (par7 != 0 && world.getBlock(x, y + 1, z).isAir(world, x, y + 1, z)
            && (block == Blocks.grass || block == Blocks.dirt)) {
            Block block1 = Blocks.farmland;
            world.playSoundEffect(
                (double) ((float) x + 0.5F),
                (double) ((float) y + 0.5F),
                (double) ((float) z + 0.5F),
                block1.stepSound.getStepResourcePath(),
                (block1.stepSound.getVolume() + 1.0F) / 2.0F,
                block1.stepSound.getPitch() * 0.8F
            );

            if (world.isRemote) {
                return true;
            } else {
                world.setBlock(x, y, z, block1);
                stack.damageItem(1, player);
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public float getSaplingModifier(
        ItemStack stack, World world, EntityPlayer player, int x, int y, int z
    ) {
        return 100.0F;
    }

    public boolean
    onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        World world = player.worldObj;
        if (!Dartcraft.proxy.isSimulating(world)) {
            return false;
        } else {
            Block block = world.getBlock(x, y, z);
            if (block != null) {
                Material mat = block.getMaterial();
                if ((!(block instanceof BlockFlower))
                    && (mat == null || mat != Material.leaves)) {
                    return false;
                } else {
                    for (int i = 0 - this.range; i < 1 + this.range; ++i) {
                        for (int j = 0 - this.range; j < 1 + this.range; ++j) {
                            for (int k = 0 - this.range; k < 1 + this.range; ++k) {
                                int newX = x + i;
                                int newY = y + j;
                                int newZ = z + k;
                                Block block2 = world.getBlock(newX, newY, newZ);
                                if (block2 instanceof BlockFlower
                                    || block2 != null
                                        && block2.getMaterial() == Material.leaves) {
                                    world.getBlock(x + i, y + j, z + k)
                                        .harvestBlock(
                                            world,
                                            player,
                                            x + i,
                                            y + j,
                                            z + k,
                                            world.getBlockMetadata(x + i, y + j, z + k)
                                        );
                                    world.setBlockToAir(x + i, y + j, z + k);
                                    if (i == 0 && j == 0 && k == 0) {
                                        //PacketDispatcher.sendPacketToAllAround((double)
                                        //x, (double) y, (double) z, 30.0D,
                                        //player.field_71093_bK, (new FXPacket(17,
                                        //(double) x, (double) y, (double)
                                        //z)).getPacket());
                                    }
                                }
                            }
                        }
                    }

                    stack.damageItem(1, player);
                    world.playSoundEffect(
                        (double) x,
                        (double) y,
                        (double) z,
                        "dartcraft:fly",
                        0.75F,
                        EntityUtils.randomPitch()
                    );
                    return true;
                }
            }
            return false;
        }
    }
}
