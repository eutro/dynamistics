package eutros.dynamistics.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SingletonRecipe implements IRecipeWrapper {

    public final ItemStack stack;
    private boolean input;

    public SingletonRecipe(ItemStack stack, boolean input) {
        this.stack = stack;
        this.input = input;
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
        if(input)
            ingredients.setInput(VanillaTypes.ITEM, stack);
        else
            ingredients.setOutput(VanillaTypes.ITEM, stack);
    }

    @SuppressWarnings("unchecked")
    public <T> T cast() {
        return (T) this;
    }

}
