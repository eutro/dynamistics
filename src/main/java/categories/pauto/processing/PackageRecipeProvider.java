package categories.pauto.processing;

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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thelm.packagedauto.api.IRecipeInfo;
import thelm.packagedauto.api.IRecipeType;

import javax.annotation.Nonnull;

public abstract class PackageRecipeProvider implements IRecipeWrapper {

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

        // this is the implementation used by PAuto in GuiEncoder
        Object rep = recipeType.getRepresentation();

        if(rep instanceof TextureAtlasSprite) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            this.drawTexturedModalRect(recipeWidth / 2 - 8, recipeHeight / 2 - 8, (TextureAtlasSprite) rep, 16, 16);
        }

        if(rep instanceof ItemStack) {
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getRenderItem().renderItemIntoGUI((ItemStack) rep, recipeWidth / 2 - 8, recipeHeight / 2 - 8);
            RenderHelper.disableStandardItemLighting();
        }
    }

    public void drawTexturedModalRect(int x, int y, TextureAtlasSprite textureSprite, int width, int height) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + height, 0).tex(textureSprite.getMinU(), textureSprite.getMaxV()).endVertex();
        bufferbuilder.pos(x + width, y + height, 0).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).endVertex();
        bufferbuilder.pos(x + width, y, 0).tex(textureSprite.getMaxU(), textureSprite.getMinV()).endVertex();
        bufferbuilder.pos(x, y, 0).tex(textureSprite.getMinU(), textureSprite.getMinV()).endVertex();
        tessellator.draw();
    }

}
