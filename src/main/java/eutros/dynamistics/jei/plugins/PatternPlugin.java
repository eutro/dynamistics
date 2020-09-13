package eutros.dynamistics.jei.plugins;

import eutros.dynamistics.helper.ItemHelper;
import eutros.dynamistics.jei.SingletonRecipe;
import eutros.dynamistics.jei.categories.ae2.PatternCategory;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeRegistryPlugin;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PatternPlugin implements IRecipeRegistryPlugin {

    public static final IRecipeRegistryPlugin INSTANCE = new PatternPlugin();

    private PatternPlugin() {
    }

    @Override
    public <V> List<String> getRecipeCategoryUids(IFocus<V> focus) {
        if(focus.getValue() instanceof ItemStack) {
            ItemStack stack = (ItemStack) focus.getValue();
            if(stack.getItem() == ItemHelper.AE2.PATTERN && stack.hasTagCompound()) {
                NBTTagCompound cmp = Objects.requireNonNull(stack.getTagCompound());
                return Collections.singletonList(PatternCategory
                        .getUid(cmp.getBoolean("crafting"),
                                cmp.getBoolean("substitute")));
            }
        }
        return Collections.emptyList();
    }

    @Override
    public <T extends IRecipeWrapper, V> List<T> getRecipeWrappers(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
        if(recipeCategory instanceof PatternCategory && ((ItemStack) focus.getValue()).getItem() == ItemHelper.AE2.PATTERN) {
            return Collections.singletonList(new SingletonRecipe((ItemStack) focus.getValue(), focus.getMode() == IFocus.Mode.INPUT).cast());
        }
        return Collections.emptyList();
    }

    @Override
    public <T extends IRecipeWrapper> List<T> getRecipeWrappers(IRecipeCategory<T> recipeCategory) {
        if(recipeCategory instanceof PatternCategory) {
            return Collections.singletonList(new SingletonRecipe(ItemHelper.AE2.EXAMPLE_PATTERN, true).cast());
        }
        return Collections.emptyList();
    }

}
