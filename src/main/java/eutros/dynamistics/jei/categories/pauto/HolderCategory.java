package eutros.dynamistics.jei.categories.pauto;

import eutros.dynamistics.helper.ItemHelper;
import eutros.dynamistics.helper.JeiHelper;
import eutros.dynamistics.helper.ModIds;
import eutros.dynamistics.jei.categories.pauto.processing.PackageRecipeProvider;
import eutros.dynamistics.jei.categories.pauto.processing.RecipeHolderProcessingRecipe;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import thelm.packagedauto.api.IRecipeInfo;
import thelm.packagedauto.api.IRecipeType;
import thelm.packagedauto.api.RecipeTypeRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HolderCategory implements IWrapperSupplier<PackageRecipeProvider> {

    public static final String UID = "dynamistics:encoder_processing";
    private static final int WIDTH = 172;
    private static final int HEIGHT = 195;
    private final IDrawable background;
    private final IDrawable slot;
    public static final int GRID_START_Y = 0;
    public static final int GRID_SIZE = 18;
    public static final int GRID_START_X = 0;
    public static final int OUT_GRID_START_Y = HEIGHT - GRID_SIZE;
    private final IDrawableStatic arrow;
    private final IDrawable icon;

    public HolderCategory(IJeiHelpers helpers) {
        IGuiHelper guiHelper = helpers.getGuiHelper();

        background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        icon = guiHelper.createDrawableIngredient(new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModIds.PAUTO, "recipe_holder")))));
        slot = JeiHelper.getSlotDrawable();

        arrow = guiHelper.createDrawable(new ResourceLocation(ModIds.SELF, "textures/gui/arrows.png"),
                64,
                0,
                162,
                16);
    }

    @Nonnull
    @Override
    public String getUid() {
        return UID;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return I18n.format("dynamistics.category.title.pauto.holder");
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

    @Override
    public void drawExtras(@Nonnull Minecraft minecraft) {
        arrow.draw(minecraft, GRID_START_X, GRID_START_Y + GRID_SIZE * 9);
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull PackageRecipeProvider recipeWrapper, @Nonnull IIngredients ingredients) {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();

        NonNullList<ItemStack> input = NonNullList.withSize(81, ItemStack.EMPTY);
        NonNullList<ItemStack> output = NonNullList.withSize(9, ItemStack.EMPTY);

        ArrayList<IDrawable> bgList = new ArrayList<>();

        for(int i = 0; i < 90; i++) {
            bgList.add(slot);
        }

        recipeType:
        {
            NBTTagCompound tag = recipeWrapper.getPackageNBT();

            IRecipeType type = RecipeTypeRegistry.getRecipeType(new ResourceLocation(tag.getString("RecipeType")));
            if(type == null) break recipeType;

            IRecipeInfo info = type.getNewRecipeInfo();
            info.readFromNBT(tag);

            recipeWrapper.setInfo(info);

            for(Int2ObjectMap.Entry<ItemStack> entry : info.getEncoderStacks().int2ObjectEntrySet()) {
                int intKey = entry.getIntKey();
                if(intKey >= input.size()) continue;
                input.set(intKey, entry.getValue());
            }

            List<ItemStack> outputs = info.getOutputs();
            for(int i = 0; i < outputs.size(); i++) {
                output.set(i, outputs.get(i));
            }

            for(int i = 0; i < 81; i++) {
                bgList.set(i, new ColouredSlot(slot, type.getSlotColor(i)));
            }
        }

        for(int i = 0; i < input.size(); i++) {
            stacks.init(i, true, GRID_START_X + (GRID_SIZE * (i % 9)), GRID_START_Y + (GRID_SIZE * (i / 9)));
            stacks.set(i, input.get(i));
            stacks.setBackground(i, bgList.get(i));
        }

        for(int i = 0; i < output.size(); i++) {
            stacks.init(i + 81, false, GRID_START_X + (GRID_SIZE * i), OUT_GRID_START_Y);
            stacks.set(i + 81, output.get(i));
            stacks.setBackground(i + 81, bgList.get(i + 81));
        }
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    @Nonnull
    public List<PackageRecipeProvider> makeWrappers(ItemStack stack) {
        return stack.getItem() == ItemHelper.PAuto.HOLDER ?
               IntStream.range(0, 20)
                       .mapToObj(i -> new HolderCategory.HolderRecipe(stack, i))
                       .collect(Collectors.toList()) :
               Collections.singletonList(new PackageRecipe(stack));
    }

    @Nonnull
    @Override
    public ItemStack getFallbackStack() {
        return ItemHelper.PAuto.EXAMPLE_PACKAGE;
    }

    public static class ColouredSlot implements IDrawable {

        private IDrawable delegate;
        private Color color;

        public ColouredSlot(IDrawable delegate, Color color) {
            this.delegate = delegate;
            this.color = color;
        }

        @Override
        public int getWidth() {
            return delegate.getWidth();
        }

        @Override
        public int getHeight() {
            return delegate.getHeight();
        }

        @Override
        public void draw(@Nonnull Minecraft minecraft, int xOffset, int yOffset) {
            delegate.draw(minecraft, xOffset, yOffset);
            drawColor(xOffset + 1, yOffset + 1);
        }

        public void drawColor(int x, int y) {
            GuiUtils.drawGradientRect(1,
                    x,
                    y,
                    x + 16,
                    y + 16,
                    color.getRGB(),
                    color.getRGB());
        }

    }

    public static class PackageRecipe extends PackageRecipeProvider {

        public PackageRecipe(ItemStack stack) {
            super(stack);
            REP_SIZE = 8;
            REP_Y = HEIGHT - REP_SIZE;
            REP_X = WIDTH - GRID_START_X - REP_SIZE;
        }

        @Nonnull
        @Override
        public NBTTagCompound getPackageNBT() {
            return stack.getTagCompound() == null ? new NBTTagCompound() : stack.getTagCompound();
        }

        @Override
        public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
            if(recipeInfo == null) return;

            IRecipeType recipeType = recipeInfo.getRecipeType();

            GlStateManager.pushMatrix();
            GlStateManager.translate(REP_X, REP_Y, 0);
            GlStateManager.scale(0.5, 0.5, 0.5);
            drawRep(minecraft, recipeType);
            GlStateManager.popMatrix();
        }

    }

    public static class HolderRecipe extends PackageRecipe {

        private final int index;

        public HolderRecipe(ItemStack stack, int index) {
            super(stack);
            this.index = index;
        }

        @Nonnull
        @Override
        public NBTTagCompound getPackageNBT() {
            return RecipeHolderProcessingRecipe.getNBT(stack, index);
        }

    }

}
