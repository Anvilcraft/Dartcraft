package ley.modding.tileralib.api;


import net.minecraft.block.Block;
import net.minecraft.item.Item;

public interface IRegistry {

    String getModID();

    Item registerItem(Item item);

    Block registerBlock(Block block);

    Item getItem(String id);

    Block getBlock(String id);

}
