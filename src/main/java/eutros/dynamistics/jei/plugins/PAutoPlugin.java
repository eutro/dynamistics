package eutros.dynamistics.jei.plugins;

import com.google.common.collect.ImmutableList;
import eutros.dynamistics.helper.ItemHelper;
import eutros.dynamistics.jei.categories.pauto.*;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeRegistryPlugin;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PAutoPlugin implements IRecipeRegistryPlugin {

    public static IRecipeRegistryPlugin INSTANCE = new PAutoPlugin();

    private PAutoPlugin() {
    }

    @Override
    public <V> List<String> getRecipeCategoryUids(IFocus<V> focus) {
        if(focus.getValue() instanceof ItemStack) {
            Item item = ((ItemStack) focus.getValue()).getItem();
            if(item == ItemHelper.PAuto.HOLDER) {
                return ImmutableList.of(
                        PackageProcessCategory.UID,
                        HolderCategory.UID
                );
            } else if(item == ItemHelper.PAuto.PACKAGE) {
                return ImmutableList.of(
                        PackageProcessCategory.UID,
                        UnpackagingCategory.UID,
                        PackagingCategory.UID,
                        HolderCategory.UID
                );
            }
        }
        return Collections.emptyList();
    }

    @Override
    public <T extends IRecipeWrapper, V> List<T> getRecipeWrappers(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
        if(focus.getValue() instanceof ItemStack) {
            Item item = ((ItemStack) focus.getValue()).getItem();
            if(recipeCategory instanceof IWrapperSupplier &&
                    (item == ItemHelper.PAuto.HOLDER || item == ItemHelper.PAuto.PACKAGE)) {
                ItemStack stack = ((ItemStack) focus.getValue()).copy();
                stack.setCount(1);
                return ((IWrapperSupplier<T>) recipeCategory).makeWrappers(stack);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public <T extends IRecipeWrapper> List<T> getRecipeWrappers(IRecipeCategory<T> recipeCategory) {
        if(recipeCategory instanceof IWrapperSupplier) {
            IWrapperSupplier<T> ws = (IWrapperSupplier<T>) recipeCategory;
            return ws.makeWrappers(ws.getFallbackStack());
        }
        return Collections.emptyList();
    }

}
