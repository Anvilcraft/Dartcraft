package ley.modding.dartcraft.integration;

import ley.modding.dartcraft.proxy.CommonProxy;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.TileDeconstructionTable;

public class ThaumCraftIntegration {
    public static final Aspect[] PRIMAL_ASPECTS
        = { Aspect.AIR,   Aspect.EARTH, Aspect.FIRE,
            Aspect.WATER, Aspect.ORDER, Aspect.ENTROPY };

    public static boolean isDeconstructorWithoutAspect(TileEntity tile) {
        if (!(tile instanceof TileDeconstructionTable))
            return false;

        return ((TileDeconstructionTable) tile).aspect == null;
    }

    /**
     * Caller asserts that decon is a TileDeconstructionTable
     */
    public static void setDeconAspect(TileEntity tile) {
        TileDeconstructionTable table = (TileDeconstructionTable) tile;

        Aspect aspect = PRIMAL_ASPECTS[CommonProxy.rand.nextInt(PRIMAL_ASPECTS.length)];
        table.aspect = aspect;
        table.markDirty();
        table.getWorldObj().markBlockForUpdate(table.xCoord, table.yCoord, table.zCoord);
    }
}
