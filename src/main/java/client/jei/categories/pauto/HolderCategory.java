package client.jei.categories.pauto;

import client.JeiAutosJEIPlugin;
import client.helper.JeiHelper;
import client.helper.ModIds;
import client.jei.categories.pauto.processing.PackageRecipeProvider;
import client.jei.categories.pauto.processing.RecipeHolderProcessingRecipe;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
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
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import thelm.packagedauto.api.IRecipeInfo;
import thelm.packagedauto.api.IRecipeType;
import thelm.packagedauto.api.RecipeTypeRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.*;

public class HolderCategory implements IRecipeCategory<PackageRecipeProvider> {

    public static final String UID = "jeiautos:encoder_processing";
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
        slot = guiHelper.getSlotDrawable();

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
        return I18n.format("jeiautos.category.title.pauto.holder");
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
            NBTTagCompound tag = recipeWrapper.getPackageNBT(stacks);

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

            for(int i = 0; i < 90; i++) {
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

        public static final int REP_SIZE = 8;
        public static final int REP_Y = HEIGHT - REP_SIZE;
        public static final int REP_X = WIDTH - GRID_START_X - REP_SIZE;
        protected final ItemStack stack;

        public PackageRecipe(Item item) {
            stack = new ItemStack(item);
        }

        @Nonnull
        @Override
        public NBTTagCompound getPackageNBT(IGuiItemStackGroup group) {
            return Optional.ofNullable(JeiHelper.getFocusedStack(stack.getItem(), group))
                    .filter(ItemStack::hasTagCompound)
                    .map(ItemStack::getTagCompound)
                    .orElseGet(NBTTagCompound::new);
        }

        @Override
        public void getIngredients(@Nonnull IIngredients ingredients) {
            super.getIngredients(ingredients);
            ingredients.setInput(VanillaTypes.ITEM, stack);
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

        @Nonnull
        @Override
        public List<String> getTooltipStrings(int mouseX, int mouseY) {
            if(recipeInfo == null ||
                    mouseX < REP_X ||
                    mouseY < REP_Y ||
                    mouseX > REP_X + REP_SIZE ||
                    mouseY > REP_Y + REP_SIZE) return Collections.emptyList();

            return Arrays.asList(recipeInfo.getRecipeType().getLocalizedName(),
                    TextFormatting.ITALIC + I18n.format("jeiautos.recipe.text.show_jei_categories"));
        }

        @Override
        public boolean handleClick(@Nonnull Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
            if(recipeInfo == null ||
                    (mouseButton != 1 && mouseButton != 2) ||
                    mouseX < REP_X ||
                    mouseY < REP_Y ||
                    mouseX > REP_X + REP_SIZE ||
                    mouseY > REP_Y + REP_SIZE) return false;

            List<String> categories = recipeInfo.getRecipeType().getJEICategories();
            if(JeiAutosJEIPlugin.runtime != null && !categories.isEmpty()) {
                JeiAutosJEIPlugin.runtime.getRecipesGui().showCategories(categories);
                return true;
            }

            return false;
        }

    }

    public static class HolderRecipe extends PackageRecipe {

        private final int index;

        public HolderRecipe(Item item, int index) {
            super(item);
            this.index = index;
        }

        @Nonnull
        @Override
        public NBTTagCompound getPackageNBT(IGuiItemStackGroup group) {
            return RecipeHolderProcessingRecipe.getNBT(group, stack.getItem(), index);
        }

    }

}
