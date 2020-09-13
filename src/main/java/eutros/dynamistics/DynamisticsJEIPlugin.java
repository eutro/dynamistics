package eutros.dynamistics;

import eutros.dynamistics.helper.ItemHelper;
import eutros.dynamistics.helper.JeiHelper;
import eutros.dynamistics.helper.ModIds;
import eutros.dynamistics.jei.categories.ae2.PatternCategory;
import eutros.dynamistics.jei.categories.pauto.HolderCategory;
import eutros.dynamistics.jei.categories.pauto.PackageProcessCategory;
import eutros.dynamistics.jei.categories.pauto.PackagingCategory;
import eutros.dynamistics.jei.categories.pauto.UnpackagingCategory;
import eutros.dynamistics.jei.plugins.PAutoPlugin;
import eutros.dynamistics.jei.plugins.PatternPlugin;
import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nonnull;

@JEIPlugin
public class DynamisticsJEIPlugin implements IModPlugin {

    public static IJeiRuntime runtime;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IJeiHelpers helpers = registry.getJeiHelpers();
        JeiHelper.makeSlotDrawable(helpers.getGuiHelper());

        if(Loader.isModLoaded(ModIds.AE2)) {
            registry.addRecipeCategories(
                    new PatternCategory(helpers, true, true),
                    new PatternCategory(helpers, true, false),
                    new PatternCategory(helpers, false, false)
            );
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
            registry.addRecipeRegistryPlugin(PatternPlugin.INSTANCE);

            ItemStack interfaceStack = new ItemStack(ItemHelper.AE2.INTERFACE);
            ItemStack molAssStack = new ItemStack(ItemHelper.AE2.MOL_ASS);

            for(byte b : new byte[] {0, 1, 2}) {
                boolean crafting = (b & 2) == 0;
                boolean substitute = (b & 1) == 0;
                String uid = PatternCategory.getUid(crafting, substitute);
                registry.addRecipeCatalyst(interfaceStack, uid);
                if(crafting) {
                    registry.addRecipeCatalyst(molAssStack, uid);
                }
            }
        }

        if(Loader.isModLoaded(ModIds.PAUTO)) {
            registry.addRecipeRegistryPlugin(PAutoPlugin.INSTANCE);

            registry.addRecipeCatalyst(new ItemStack(ItemHelper.PAuto.UNPACKAGER), UnpackagingCategory.UID);
            registry.addRecipeCatalyst(new ItemStack(ItemHelper.PAuto.PACKAGER), PackagingCategory.UID);
        }
    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

}
