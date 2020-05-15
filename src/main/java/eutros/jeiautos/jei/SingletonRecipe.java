package eutros.jeiautos.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SingletonRecipe implements IRecipeWrapper {

    public final ItemStack stack;
    private boolean input;

    public SingletonRecipe(Item item, boolean input) {
        stack = new ItemStack(item);
        this.input = input;
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
        if(input)
            ingredients.setInput(VanillaTypes.ITEM, stack);
        else
            ingredients.setOutput(VanillaTypes.ITEM, stack);
    }

}
