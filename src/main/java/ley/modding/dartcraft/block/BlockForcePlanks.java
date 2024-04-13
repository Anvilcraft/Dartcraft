package ley.modding.dartcraft.block;

import ley.modding.dartcraft.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;

public class BlockForcePlanks extends Block {
    public BlockForcePlanks() {
        super(Material.wood);
        Util.configureBlock(this, "forceplanks");
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon("dartcraft:wood");
    }
}
