package ley.modding.dartcraft.block;

import net.minecraft.entity.EntityLivingBase;

public interface IBlockWalkOver {
    public void onEntityWalking(EntityLivingBase entity, int x, int y, int z);
}
