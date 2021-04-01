package ley.modding.dartcraft.proxy;

import net.minecraft.world.World;

public class ClientProxy extends CommonProxy {

    public boolean isSimulating(World world) {
        return world != null && !world.isRemote;
    }

}
