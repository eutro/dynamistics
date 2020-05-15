package client.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class NBTHelper {

    public static NonNullList<ItemStack> getItemStackList(NBTTagCompound nbt, String key) {
        return StreamSupport.stream(nbt.getTagList(key, 10)
                .spliterator(), true)
                .map(NBTTagCompound.class::cast)
                .map(ItemStack::new)
                .collect(Collectors.toCollection(NonNullList::create));
    }

}
