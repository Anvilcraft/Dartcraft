package ley.modding.dartcraft.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ley.modding.dartcraft.Dartcraft;
import net.anvilcraft.alec.jalec.factories.AlecCriticalRuntimeErrorExceptionFactory;
import net.anvilcraft.anvillib.util.NBTCollector;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.event.world.WorldEvent;

public class EnderStorageHandler {
    public static EnderStorageHandler INSTANCE = new EnderStorageHandler();
    private Map<UUID, ItemStack[][]> storage = new HashMap<>();

    public EnderInventory getInventory(UUID player, int color) {
        return this.getInventory(player, color, true);
    }

    public EnderInventory getInventory(UUID player, int color, boolean fake) {
        if (player == null) return null;
        //if (!Config.loadEnderStorage) {
        //    return null;
        //}
        EnderInventory inv = null;
        try {
            if (!storage.containsKey(player)) {
                storage.put(player, new ItemStack[16][72]);
                Dartcraft.LOGGER.info("Created player tag: " + player);
            }
            ItemStack[] stored = storage.get(player)[color];
            if (fake) {
                ItemStack[] storedCopy
                    = Arrays.stream(stored)
                          .map(stack -> stack == null ? null : stack.copy())
                          .toArray(ItemStack[] ::new);
                inv = new EnderInventory(storedCopy, player, color, fake);
            } else {
                inv = new EnderInventory(stored, player, color, fake);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inv;
    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save ev) {
        if (ev.world.provider.dimensionId != 0)
            return;
        File enderStorageFile = new File(
            ev.world.getSaveHandler().getWorldDirectory(), "dartcraftEnderStorage.dat"
        );

        NBTTagCompound nbt = new NBTTagCompound();
        for (Entry<UUID, ItemStack[][]> ent : this.storage.entrySet()) {
            NBTTagList colorList
                = Arrays.stream(ent.getValue())
                      .<NBTTagList>map(
                          slots
                          -> Arrays.stream(slots)
                                 .map(
                                     stack
                                     -> stack == null
                                         ? new NBTTagCompound()
                                         : stack.writeToNBT(new NBTTagCompound())
                                 )
                                 .collect(new NBTCollector())
                      )
                      .collect(new NBTCollector());
            nbt.setTag(ent.getKey().toString(), colorList);
        }

        DataOutputStream dos;
        try {
            dos = new DataOutputStream(new FileOutputStream(enderStorageFile));
        } catch (FileNotFoundException e) {
            return;
        }
        try {
            CompressedStreamTools.writeCompressed(nbt, dos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings({ "unchecked", "ALEC" })
    public void onWorldLoad(WorldEvent.Load ev) {
        if (ev.world.provider.dimensionId != 0)
            return;
        File enderStorageFile = new File(
            ev.world.getSaveHandler().getWorldDirectory(), "dartcraftEnderStorage.dat"
        );

        try {
            DataInputStream dis
                = new DataInputStream(new FileInputStream(enderStorageFile));
            NBTTagCompound nbt = CompressedStreamTools.readCompressed(dis);
            dis.close();
            if (nbt == null)
                return;
            for (String key : (Set<String>) nbt.func_150296_c()) {
                UUID id = UUID.fromString(key);
                NBTTagList colors = nbt.getTagList(key, 9);
                this.storage.put(
                    id,
                    (ItemStack[][]) colors.tagList.stream()
                        .map(
                            base
                            -> ((List<NBTTagCompound>) ((NBTTagList) base).tagList)
                                   .stream()
                                   .map(
                                       slot
                                       -> slot.func_150296_c().isEmpty()
                                           ? null
                                           : ItemStack.loadItemStackFromNBT(slot)
                                   )
                                   .toArray(ItemStack[] ::new)
                        )
                        .toArray(ItemStack[][] ::new)
                );
            }
        } catch (FileNotFoundException alec) {
        } catch (IOException e) {
            throw AlecCriticalRuntimeErrorExceptionFactory.PLAIN
                .createAlecExceptionWithCause(e, "Failed to read ender storage");
        }
    }
}
