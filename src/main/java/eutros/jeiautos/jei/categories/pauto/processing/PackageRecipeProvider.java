package eutros.jeiautos.jei.categories.pauto.processing;

import eutros.jeiautos.JeiAutosJEIPlugin;
import eutros.jeiautos.jei.categories.pauto.PackageProcessCategory;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import thelm.packagedauto.api.IRecipeInfo;
import thelm.packagedauto.api.IRecipeType;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class PackageRecipeProvider implements IRecipeWrapper {

    public int REP_SIZE = 32;
    public int REP_X = (PackageProcessCategory.WIDTH - REP_SIZE) / 2;
    public int REP_Y = (PackageProcessCategory.HEIGHT - REP_SIZE) / 2;
    protected IRecipeInfo recipeInfo = null;

    @Nonnull
    public abstract NBTTagCompound getPackageNBT(IGuiItemStackGroup group);

    public void setInfo(IRecipeInfo recipeInfo) {
        this.recipeInfo = recipeInfo;
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
        recipeInfo = null;
    }

    @Override
    public void drawInfo(@Nonnull Minecraft mc, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        if(recipeInfo == null) {
            return;
        }

        IRecipeType recipeType = recipeInfo.getRecipeType();
        String locName = recipeType.getLocalizedName();
        mc.fontRenderer.drawString(locName,
                (recipeWidth - mc.fontRenderer.getStringWidth(locName)) / 2,
                16,
                0xFF000000);
        GlStateManager.pushMatrix();
        GlStateManager.translate(REP_X, REP_Y, 0);
        GlStateManager.scale(2, 2, 2);
        drawRep(mc, recipeType);
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
                TextFormatting.GRAY + I18n.format("jeiautos.recipe.text.show_jei_categories"));
    }

    @Override
    public boolean handleClick(@Nonnull Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        if(recipeInfo == null ||
                (mouseButton != 0 && mouseButton != 1) ||
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

    public void drawRep(@Nonnull Minecraft mc, IRecipeType recipeType) {
        // this is the implementation used by PAuto in GuiEncoder
        Object rep = recipeType.getRepresentation();

        if(rep instanceof TextureAtlasSprite) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            this.drawTexturedModalRect((TextureAtlasSprite) rep);
        }

        if(rep instanceof ItemStack) {
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getRenderItem().renderItemIntoGUI((ItemStack) rep, 0, 0);
            RenderHelper.disableStandardItemLighting();
        }
    }

    public void drawTexturedModalRect(TextureAtlasSprite textureSprite) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(0, 16, 0).tex(textureSprite.getMinU(), textureSprite.getMaxV()).endVertex();
        bufferbuilder.pos(16, 16, 0).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).endVertex();
        bufferbuilder.pos(16, 0, 0).tex(textureSprite.getMaxU(), textureSprite.getMinV()).endVertex();
        bufferbuilder.pos(0, 0, 0).tex(textureSprite.getMinU(), textureSprite.getMinV()).endVertex();
        tessellator.draw();
    }

}
