package jei.categories.pauto;

import helper.ModIds;
import jei.SingletonRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import thelm.packagedauto.api.IPackagePattern;
import thelm.packagedauto.api.IRecipeInfo;
import thelm.packagedauto.api.IRecipeType;
import thelm.packagedauto.api.RecipeTypeRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static helper.JeiHelper.getFocusedStack;

public class UnpackagingCategory implements IRecipeCategory<SingletonRecipe> {

    public static final String UID = "jeiautos:unpackaging";
    private static final int HEIGHT = 126;
    private static final int WIDTH = 146;
    protected static final int GRID_SIZE = 18;
    private final IDrawableStatic background;
    private final IDrawableStatic slot;
    private final IDrawable icon;
    private final IDrawableStatic arrow;
    protected int gridStartY;

    public UnpackagingCategory(IJeiHelpers helpers) {
        IGuiHelper guiHelper = helpers.getGuiHelper();

        slot = guiHelper.getSlotDrawable();
        background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        icon = guiHelper.createDrawableIngredient(new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModIds.PAUTO, (isUnpackaging() ? "un" : "") + "packager")))));
        gridStartY = HEIGHT - (int) (3.5 * GRID_SIZE);
        arrow = guiHelper.createDrawable(new ResourceLocation(ModIds.SELF, "textures/gui/arrows.png"),
                isUnpackaging() ?
                0 :
                32,
                0,
                32,
                32);
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Nonnull
    @Override
    public String getUid() {
        return UID;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return I18n.format("jeiautos.category.title.pauto.unpackage");
    }

    @Nonnull
    @Override
    public String getModName() {
        return "PackagedAuto";
    }

    @Override
    public void drawExtras(@Nonnull Minecraft minecraft) {
        arrow.draw(minecraft, (WIDTH - arrow.getWidth()) / 2, isUnpackaging() ? 28 : HEIGHT - 28 - arrow.getHeight());
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, @Nonnull SingletonRecipe recipeWrapper, @Nonnull IIngredients ingredients) {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();

        ItemStack packageStack = getFocusedStack(recipeWrapper.stack.getItem(), stacks);

        if(packageStack == null) {
            packageStack = recipeWrapper.stack;
        }

        packageStack = packageStack.copy();
        packageStack.setCount(1);

        stacks.init(10, isUnpackaging(), WIDTH / 2 - 8, isUnpackaging() ? 8 : HEIGHT - 8 - 16);
        stacks.set(10, packageStack);
        stacks.setBackground(10, slot);

        NonNullList<ItemStack> inputs = NonNullList.withSize(9, ItemStack.EMPTY);

        recipeType :
        {
            if(packageStack.hasTagCompound()) {
                NBTTagCompound tag = packageStack.getTagCompound();
                assert tag != null;

                String typeId = tag.getString("RecipeType");
                if(typeId.isEmpty()) break recipeType;

                IRecipeType recipeType = RecipeTypeRegistry.getRecipeType(new ResourceLocation(typeId));
                if(recipeType == null) break recipeType;

                IRecipeInfo recipeInfo = recipeType.getNewRecipeInfo();
                recipeInfo.readFromNBT(tag);
                IPackagePattern pattern = recipeInfo.getPatterns().get(tag.getByte("Index"));

                List<ItemStack> recipeInputs = pattern.getInputs();
                for(int i = 0; i < recipeInputs.size(); i++) {
                    ItemStack s = recipeInputs.get(i);
                    inputs.set(i, s);
                }
            }
        }

        int gridStartX = (WIDTH - 3 * GRID_SIZE) / 2;
        for(int i = 0; i < inputs.size(); i++) {
            stacks.init(i, !isUnpackaging(), gridStartX + (GRID_SIZE * (i % 3)), gridStartY + (GRID_SIZE * (i / 3)));
            stacks.set(i, inputs.get(i));
            stacks.setBackground(i, slot);
        }
    }

    protected boolean isUnpackaging() {
        return true;
    }

}
