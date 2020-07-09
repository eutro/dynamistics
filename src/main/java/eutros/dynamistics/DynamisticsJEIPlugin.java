package eutros.dynamistics;

import eutros.dynamistics.helper.JeiHelper;
import eutros.dynamistics.helper.ModIds;
import eutros.dynamistics.jei.SingletonRecipe;
import eutros.dynamistics.jei.categories.ae2.PatternCategory;
import eutros.dynamistics.jei.categories.pauto.HolderCategory;
import eutros.dynamistics.jei.categories.pauto.PackageProcessCategory;
import eutros.dynamistics.jei.categories.pauto.PackagingCategory;
import eutros.dynamistics.jei.categories.pauto.UnpackagingCategory;
import eutros.dynamistics.jei.categories.pauto.processing.PackageProcessingRecipe;
import eutros.dynamistics.jei.categories.pauto.processing.RecipeHolderProcessingRecipe;
import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@JEIPlugin
public class DynamisticsJEIPlugin implements IModPlugin {

    public static IJeiRuntime runtime;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IJeiHelpers helpers = registry.getJeiHelpers();
        JeiHelper.makeSlotDrawable(helpers.getGuiHelper());

        if(Loader.isModLoaded(ModIds.AE2)) {
            for(byte b : new byte[] {0, 1, 2}) {
                boolean crafting = (b & 2) == 0;
                boolean substitute = (b & 1) == 0;
                registry.addRecipeCategories(new PatternCategory(helpers, crafting, substitute));
            }
        }

        if(Loader.isModLoaded(ModIds.PAUTO)) {
            registry.addRecipeCategories(
                    new PackageProcessCategory(helpers),
                    new HolderCategory(helpers),
                    new UnpackagingCategory(helpers),
                    new PackagingCategory(helpers)
            );
        }
    }

    @Override
    public void register(@Nonnull IModRegistry registry) {
        if(Loader.isModLoaded(ModIds.AE2)) {
            Item pattern = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModIds.AE2, "encoded_pattern"));
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModIds.AE2, "interface"));
            ItemStack interfaceStack = item == null ? ItemStack.EMPTY : new ItemStack(item);
            item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModIds.AE2, "molecular_assembler"));
            ItemStack molAssStack = item == null ? ItemStack.EMPTY : new ItemStack(item);

            for(byte b : new byte[] {0, 1, 2}) {
                boolean crafting = (b & 2) == 0;
                boolean substitute = (b & 1) == 0;
                String uid = PatternCategory.getUid(crafting, substitute);
                registry.addRecipes(Collections.singletonList(new PatternCategory.Recipe(pattern, crafting, substitute)),
                        uid);

                registry.addRecipeCatalyst(interfaceStack, uid);
                if(crafting) {
                    registry.addRecipeCatalyst(molAssStack, uid);
                }
            }
        }

        if(Loader.isModLoaded(ModIds.PAUTO)) {
            Item recipePackage = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModIds.PAUTO, "package"));
            Item recipeHolder = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModIds.PAUTO, "recipe_holder"));

            registry.addRecipes(Collections.singletonList(new PackageProcessingRecipe(recipePackage)),
                    PackageProcessCategory.UID);

            registry.addRecipes(Collections.singletonList(new SingletonRecipe(recipePackage, true)),
                    UnpackagingCategory.UID);
            registry.addRecipeCatalyst(new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModIds.PAUTO, "unpackager")))),
                    UnpackagingCategory.UID);

            registry.addRecipes(Collections.singletonList(new SingletonRecipe(recipePackage, false)),
                    PackagingCategory.UID);
            registry.addRecipeCatalyst(new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModIds.PAUTO, "packager")))),
                    PackagingCategory.UID);

            registry.addRecipes(IntStream.range(0, 20)
                            .mapToObj(i -> new RecipeHolderProcessingRecipe(recipeHolder, i))
                            .collect(Collectors.toList()),
                    PackageProcessCategory.UID);

            registry.addRecipes(Collections.singletonList(new HolderCategory.PackageRecipe(recipePackage)),
                    HolderCategory.UID);
            registry.addRecipes(IntStream.range(0, 20)
                            .mapToObj(i -> new HolderCategory.HolderRecipe(recipeHolder, i))
                            .collect(Collectors.toList()),
                    HolderCategory.UID);
        }
    }

    @Override
    public void registerItemSubtypes(@Nonnull ISubtypeRegistry subtypeRegistry) {
        if(Loader.isModLoaded(ModIds.AE2)) {
            Item pattern = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModIds.AE2, "encoded_pattern"));
            if(pattern != null) {
                subtypeRegistry.registerSubtypeInterpreter(pattern, stack -> {
                    if(!stack.hasTagCompound()) {
                        return ISubtypeRegistry.ISubtypeInterpreter.NONE;
                    }

                    NBTTagCompound cmp = stack.getTagCompound();
                    assert cmp != null;
                    boolean crafting = cmp.getBoolean("crafting");
                    boolean substitute = cmp.getBoolean("substitute");
                    return (crafting ? ("crafting;" + (substitute ? "subs" : "noSubs")) : "processing");
                });
            }
        }
    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

}
