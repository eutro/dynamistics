package eutros.dynamistics.helper;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import static eutros.dynamistics.helper.ModIds.AE2;
import static eutros.dynamistics.helper.ModIds.PAUTO;

public class ItemHelper {

    static Item get(String modid, String path) {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(modid, path));
    }

    public static class AE2 {

        public static Item PATTERN = get(AE2, "encoded_pattern");
        public static Item INTERFACE = get(AE2, "interface");
        public static Item MOL_ASS = get(AE2, "molecular_assembler");

        public static ItemStack EXAMPLE_PATTERN = new ItemStack(PATTERN);

        /*
        {
         in:[
          {id:"minecraft:diamond",Count:1b,Damage:0s},
          {id:"minecraft:diamond",Count:1b,Damage:0s},
          {id:"minecraft:diamond",Count:1b,Damage:0s},
          {},
          {id:"minecraft:stick",Count:1b,Damage:0s},
          {},
          {},
          {id:"minecraft:stick",Count:1b,Damage:0s},
          {}
         ],
         crafting:1b,
         substitute:1b,
         out:[
          {id:"minecraft:diamond_pickaxe",Count:1b,Damage:0s},
          {},
          {}
         ]
        }
         */
        static {
            NBTTagCompound cmp = new NBTTagCompound();
            NBTTagList in = new NBTTagList();
            NBTTagCompound diamond = new ItemStack(Items.DIAMOND).writeToNBT(new NBTTagCompound());
            NBTTagCompound stick = new ItemStack(Items.STICK).writeToNBT(new NBTTagCompound());
            in.appendTag(diamond.copy());
            in.appendTag(diamond.copy());
            in.appendTag(diamond.copy());
            in.appendTag(new NBTTagCompound());
            in.appendTag(stick.copy());
            in.appendTag(new NBTTagCompound());
            in.appendTag(new NBTTagCompound());
            in.appendTag(stick.copy());
            in.appendTag(new NBTTagCompound());
            cmp.setTag("in", in);
            cmp.setBoolean("crafting", true);
            cmp.setBoolean("substitute", true);
            NBTTagList out = new NBTTagList();
            out.appendTag(new ItemStack(Items.DIAMOND_PICKAXE).writeToNBT(new NBTTagCompound()));
            out.appendTag(new NBTTagCompound());
            out.appendTag(new NBTTagCompound());
            cmp.setTag("out", out);

            EXAMPLE_PATTERN.setTagCompound(cmp);
        }

    }

    public static class PAuto {

        public static Item PACKAGE = get(PAUTO, "package");
        public static Item HOLDER = get(PAUTO, "recipe_holder");
        public static Item PACKAGER = get(PAUTO, "packager");
        public static Item UNPACKAGER = get(PAUTO, "unpackager");

        public static final ItemStack EXAMPLE_PACKAGE = new ItemStack(PACKAGE);
        /*
        {
         Input:[
          {Index:0b,id:"minecraft:diamond",Count:3b,Damage:0s},
          {Index:1b,id:"minecraft:stick",Count:2b,Damage:0s}
         ],
         RecipeType:"packagedauto:processing",
         Output:[
          {Index:0b,id:"minecraft:diamond_pickaxe",Count:1b,Damage:0s}
         ],
         Index:0b
        }
         */
        static {
            NBTTagCompound cmp = new NBTTagCompound();
            NBTTagList input = new NBTTagList();
            NBTTagCompound diamond = new NBTTagCompound();
            diamond.setByte("Index", (byte) 0);
            input.appendTag(new ItemStack(Items.DIAMOND, 3).writeToNBT(diamond));
            NBTTagCompound stick = new NBTTagCompound();
            stick.setByte("Index", (byte) 1);
            input.appendTag(new ItemStack(Items.STICK, 2).writeToNBT(stick));
            cmp.setTag("Input", input);
            cmp.setString("RecipeType", "packagedauto:processing");
            NBTTagList output = new NBTTagList();
            output.appendTag(new ItemStack(Items.DIAMOND_PICKAXE).writeToNBT(new NBTTagCompound()));
            cmp.setTag("Output", output);
            cmp.setByte("Index", (byte) 0);
            EXAMPLE_PACKAGE.setTagCompound(cmp);
        }

    }

}
