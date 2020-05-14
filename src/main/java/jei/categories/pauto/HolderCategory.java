package jei.categories.pauto;

import helper.JeiHelper;
import helper.ModIds;
import it.unimi.dsi.fastutil.ints.IntSet;
import jei.categories.pauto.processing.PackageRecipeProvider;
import jei.categories.pauto.processing.RecipeHolderProcessingRecipe;
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
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import thelm.packagedauto.api.IRecipeInfo;
import thelm.packagedauto.api.IRecipeType;
import thelm.packagedauto.api.RecipeTypeRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

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

        recipeType:
        {
            NBTTagCompound tag = recipeWrapper.getPackageNBT(stacks);

            IRecipeType type = RecipeTypeRegistry.getRecipeType(new ResourceLocation(tag.getString("RecipeType")));
            if(type == null) break recipeType;

            IRecipeInfo info = type.getNewRecipeInfo();
            info.readFromNBT(tag);

            recipeWrapper.setInfo(info);

            Iterator<ItemStack> inputs = info.getInputs().iterator();
            Iterator<ItemStack> outputs = info.getOutputs().iterator();

            IntSet enabledSlots = type.getEnabledSlots();
            for(int i = 0; i < input.size(); i++) {
                if(enabledSlots.contains(i)) {
                    input.set(i, inputs.next());
                    if(!inputs.hasNext()) break;
                }
            }

            for(int i = 0; i < output.size(); i++) {
                if(enabledSlots.contains(i + 81)) {
                    output.set(i, outputs.next());
                    if(!outputs.hasNext()) break;
                }
            }
        }

        for(int i = 0; i < input.size(); i++) {
            stacks.init(i, true, GRID_START_X + (GRID_SIZE * (i % 9)), GRID_START_Y + (GRID_SIZE * (i / 9)));
            stacks.set(i, input.get(i));
            stacks.setBackground(i, slot);
        }

        for(int i = 0; i < output.size(); i++) {
            stacks.init(i + 81, false, GRID_START_X + (GRID_SIZE * i), OUT_GRID_START_Y);
            stacks.set(i + 81, output.get(i));
            stacks.setBackground(i + 81, slot);
        }
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return icon;
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
            int i;
            int j;
            Color color;
            for(i = 0; i < 9; ++i) {
                for(j = 0; j < 9; ++j) {
                    color = recipeType.getSlotColor(i * 9 + j);
                    drawColor(GRID_START_X + i * GRID_SIZE,
                            GRID_START_Y + j * GRID_SIZE,
                            color);
                }
            }

            for(i = 0; i < 9; ++i) {
                    color = recipeType.getSlotColor(81 + i);
                    drawColor(GRID_START_X + i * GRID_SIZE,
                            OUT_GRID_START_Y,
                            color);
            }

            GlStateManager.color(1, 1, 1);

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

            return Collections.singletonList(recipeInfo.getRecipeType().getLocalizedName());
        }

        public static void drawColor(int x, int y, Color color) {
            GlStateManager.color((float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F);
            float f = 1.0F / (float) 512;
            float f1 = 1.0F / (float) 512;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos(x, y + 16, 0.0D).tex((float) 258 * f, ((float) 0 + (float) 16) * f1).endVertex();
            bufferbuilder.pos(x + 16, y + 16, 0.0D).tex(((float) 258 + (float) 16) * f, ((float) 0 + (float) 16) * f1).endVertex();
            bufferbuilder.pos(x + 16, y, 0.0D).tex(((float) 258 + (float) 16) * f, (float) 0 * f1).endVertex();
            bufferbuilder.pos(x, y, 0.0D).tex((float) 258 * f, (float) 0 * f1).endVertex();
            tessellator.draw();
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
