package eutros.jeiautos.jei.categories.pauto;

import eutros.jeiautos.jei.categories.pauto.processing.PackageRecipeProvider;
import eutros.jeiautos.helper.JeiHelper;
import eutros.jeiautos.helper.ModIds;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import thelm.packagedauto.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PackageProcessCategory implements IRecipeCategory<PackageRecipeProvider> {

    public static final String UID = "jeiautos:package_process";
    public static final int HEIGHT = 98;
    public static final int WIDTH = 168;
    private final IDrawableStatic background;
    private final IDrawableStatic slot;
    private final IDrawable icon;
    private final Item recipePackage;
    private final IDrawableStatic arrow;

    public PackageProcessCategory(IJeiHelpers helpers) {
        IGuiHelper guiHelper = helpers.getGuiHelper();

        recipePackage = Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModIds.PAUTO, "package")));

        slot = guiHelper.getSlotDrawable();
        background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        icon = guiHelper.createDrawableIngredient(new ItemStack(recipePackage));

        arrow = guiHelper.createDrawable(new ResourceLocation(ModIds.SELF, "textures/gui/arrows.png"),
                0,
                32,
                64,
                64);
    }

    @Nonnull
    @Override
    public String getUid() {
        return UID;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return I18n.format("jeiautos.category.title.pauto.process");
    }

    @Nonnull
    @Override
    public String getModName() {
        return "PackagedAuto";
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void drawExtras(@Nonnull Minecraft minecraft) {
        arrow.draw(minecraft,
                (WIDTH - arrow.getWidth()) / 2,
                (HEIGHT - arrow.getHeight()) / 2);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, PackageRecipeProvider recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
        NBTTagCompound nbt = recipeWrapper.getPackageNBT(stacks);

        ItemStack input = ingredients.getInputs(VanillaTypes.ITEM).get(0).get(0);

        if(input.getItem() != recipePackage) {
            input = Optional.ofNullable(JeiHelper.getFocusedStack(input.getItem(), stacks)).orElse(input).copy();
            input.setCount(1);

            stacks.init(20, true, 2, 2);
            stacks.set(20, input);
            stacks.setBackground(20, slot);
        }

        NonNullList<ItemStack> inputs = NonNullList.withSize(9, ItemStack.EMPTY);
        NonNullList<ItemStack> outputs = NonNullList.withSize(9, ItemStack.EMPTY);

        recipeType :
        {
            if(nbt.isEmpty()) break recipeType;

            IRecipeType recipeType = RecipeTypeRegistry.getRecipeType(new ResourceLocation(nbt.getString("RecipeType")));
            if(recipeType == null) break recipeType;

            IRecipeInfo recipeInfo = recipeType.getNewRecipeInfo();
            recipeInfo.readFromNBT(nbt);
            List<IPackagePattern> patterns = recipeInfo.getPatterns();
            for(int i = 0; i < patterns.size(); i++) {
                inputs.set(i, patterns.get(i).getOutput());
            }

            List<ItemStack> outStacks = recipeInfo.getOutputs();
            for(int i = 0; i < outStacks.size(); i++) {
                outputs.set(i, outStacks.get(i));
            }

            recipeWrapper.setInfo(recipeInfo);
        }

        int gridStartY = 32;
        int gridStartX = 8;
        int gridSize = 18;
        for(int i = 0; i < inputs.size(); i++) {
            stacks.init(i, true, gridStartX + (gridSize * (i % 3)), gridStartY + (gridSize * (i / 3)));
            stacks.set(i, inputs.get(i));
            stacks.setBackground(i, slot);
        }

        gridStartX = WIDTH - gridSize * 3 - gridStartX;
        for(int i = 0; i < outputs.size(); i++) {
            stacks.init(9 + i, false, gridStartX + (gridSize * (i % 3)), gridStartY + (gridSize * (i / 3)));
            stacks.set(9 + i, outputs.get(i));
            stacks.setBackground(9 + i, slot);
        }
    }

}
