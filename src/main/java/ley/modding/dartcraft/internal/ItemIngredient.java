package ley.modding.dartcraft.internal;

import ley.modding.tileralib.api.IIngredient;
import net.minecraft.item.Item;

public class ItemIngredient implements IIngredient {

    char key;
    int count;
    Item item;

    public ItemIngredient(char key, Item item) {
        this.key = key;
        this.item = item;
    }

    public ItemIngredient(int count, Item item) {
        this.count = count;
        this.item = item;
    }

    @Override
    public char getKey() {
        return key;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getIngredient() {
        return item;
    }
}
