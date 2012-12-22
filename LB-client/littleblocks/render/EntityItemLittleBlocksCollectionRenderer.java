package littleblocks.render;

import java.util.HashMap;
import java.util.Random;

import littleblocks.items.EntityItemLittleBlocksCollection;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;

public class EntityItemLittleBlocksCollectionRenderer extends RenderItem {
    private HashMap<ItemStack, Random> random = new HashMap();
    private RenderBlocks renderBlocks = new RenderBlocks();
    
    public void doRender(Entity entity, double x, double y, double z, float f1, float f2)
    {
        this.doRenderItem((EntityItemLittleBlocksCollection)entity, x, y, z, f1, f2);
    }
    
    public void doRenderItem(EntityItemLittleBlocksCollection collection, double x, double y, double z, float f1, float f2)
    {
        GL11.glPushMatrix();
        for (ItemStack itemstack : collection.itemstackCollection.values()) {
        	Random rand = new Random();
        	this.random.put(itemstack, rand);
        	this.random.get(itemstack).setSeed(187L);
	        if (itemstack.getItem() != null)
	        {
	            float f3 = shouldBob() ? MathHelper.sin((collection.age + f2) / 10.0F + collection.hoverStart) * 0.1F + 0.1F : 0F;
	            float f4 = ((collection.age + f2) / 20.0F + collection.hoverStart) * (180F / (float)Math.PI);
	            byte miniCount = getMiniBlockCountForItemStack(itemstack);
	
	            GL11.glTranslatef((float)x, (float)y + f3, (float)z);
	            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	            int renderType;
	            float t2;
	            float t3;
	            float t1;
	
	            if (ForgeHooksClient.renderEntityItem(collection, itemstack, f3, f4, this.random.get(itemstack), renderManager.renderEngine, renderBlocks))
	            {
	                ;
	            }
	            else if (itemstack.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.blocksList[itemstack.itemID].getRenderType()))
	            {
	                GL11.glRotatef(f4, 0.0F, 1.0F, 0.0F);
	
	                if (field_82407_g)
	                {
	                    GL11.glScalef(1.25F, 1.25F, 1.25F);
	                    GL11.glTranslatef(0.0F, 0.05F, 0.0F);
	                    GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
	                }
	
	                this.loadTexture(Block.blocksList[itemstack.itemID].getTextureFile());
	                float var22 = 0.25F;
	                renderType = Block.blocksList[itemstack.itemID].getRenderType();
	
	                if (renderType == 1 || renderType == 19 || renderType == 12 || renderType == 2)
	                {
	                    var22 = 0.5F;
	                }
	
	                GL11.glScalef(var22, var22, var22);
	
	                for (int count = 0; count < miniCount; ++count)
	                {
	                    GL11.glPushMatrix();
	
	                    if (count > 0)
	                    {
	                        t1 = (this.random.get(itemstack).nextFloat() * 2.0F - 1.0F) * 0.2F / var22;
	                        t2 = (this.random.get(itemstack).nextFloat() * 2.0F - 1.0F) * 0.2F / var22;
	                        t3 = (this.random.get(itemstack).nextFloat() * 2.0F - 1.0F) * 0.2F / var22;
	                        GL11.glTranslatef(t1, t2, t3);
	                    }
	
	                    t1 = 1.0F;
	                    this.renderBlocks.renderBlockAsItem(Block.blocksList[itemstack.itemID], itemstack.getItemDamage(), t1);
	                    GL11.glPopMatrix();
	                }
	            }
	            else
	            {
	                int renderPass;
	                float colourMultiplier;
	
	                if (itemstack.getItem().requiresMultipleRenderPasses())
	                {
	                    if (field_82407_g)
	                    {
	                        GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
	                        GL11.glTranslatef(0.0F, -0.05F, 0.0F);
	                    }
	                    else
	                    {
	                        GL11.glScalef(0.5F, 0.5F, 0.5F);
	                    }
	
	
	                    for (renderPass = 0; renderPass <= itemstack.getItem().getRenderPasses(itemstack.getItemDamage()); ++renderPass)
	                    {
	                        this.loadTexture(Item.itemsList[itemstack.itemID].getTextureFile());
	                        this.random.get(itemstack).setSeed(187L);
	                        renderType = itemstack.getItem().getIconIndex(itemstack, renderPass);
	                        colourMultiplier = 1.0F;
	
	                        if (this.field_77024_a)
	                        {
	                            int var18 = Item.itemsList[itemstack.itemID].getColorFromItemStack(itemstack, renderPass);
	                            t2 = (var18 >> 16 & 255) / 255.0F;
	                            t3 = (var18 >> 8 & 255) / 255.0F;
	                            float var21 = (var18 & 255) / 255.0F;
	                            GL11.glColor4f(t2 * colourMultiplier, t3 * colourMultiplier, var21 * colourMultiplier, 1.0F);
	                            this.renderItem(collection, itemstack, renderType, miniCount, f2, t2 * colourMultiplier, t3 * colourMultiplier, var21 * colourMultiplier);
	                        }
	                        else
	                        {
	                            this.renderItem(collection, itemstack, renderType, miniCount, f2, 1.0F, 1.0F, 1.0F);
	                        }
	                    }
	                }
	                else
	                {
	                    if (field_82407_g)
	                    {
	                        GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
	                        GL11.glTranslatef(0.0F, -0.05F, 0.0F);
	                    }
	                    else
	                    {
	                        GL11.glScalef(0.5F, 0.5F, 0.5F);
	                    }
	
	                    renderPass = itemstack.getIconIndex();
	
	                    this.loadTexture(itemstack.getItem().getTextureFile());
	
	                    if (this.field_77024_a)
	                    {
	                        renderType = Item.itemsList[itemstack.itemID].getColorFromItemStack(itemstack, 0);
	                        colourMultiplier = (renderType >> 16 & 255) / 255.0F;
	                        t1 = (renderType >> 8 & 255) / 255.0F;
	                        t2 = (renderType & 255) / 255.0F;
	                        t3 = 1.0F;
	                        this.renderItem(collection, itemstack, renderPass, miniCount, f2, colourMultiplier * t3, t1 * t3, t2 * t3);
	                    }
	                    else
	                    {
	                        this.renderItem(collection, itemstack, renderPass, miniCount, f2, 1.0F, 1.0F, 1.0F);
	                    }
	                }
	            }
	        }
        }
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }
    
    private void renderItem(EntityItem par1EntityItem, ItemStack itemstack, int par2, int par3, float par4, float par5, float par6, float par7)
    {
        Tessellator var8 = Tessellator.instance;
        float var9 = (par2 % 16 * 16 + 0) / 256.0F;
        float var10 = (par2 % 16 * 16 + 16) / 256.0F;
        float var11 = (par2 / 16 * 16 + 0) / 256.0F;
        float var12 = (par2 / 16 * 16 + 16) / 256.0F;
        float var13 = 1.0F;
        float var14 = 0.5F;
        float var15 = 0.25F;
        float var17;

        if (this.renderManager.options.fancyGraphics)
        {
            GL11.glPushMatrix();

            if (field_82407_g)
            {
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }
            else
            {
                GL11.glRotatef(((par1EntityItem.age + par4) / 20.0F + par1EntityItem.hoverStart) * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            }

            float var16 = 0.0625F;
            var17 = 0.021875F;
            int stacksize = itemstack.stackSize;
            byte miniCount = getMiniItemCountForItemStack(itemstack);


            GL11.glTranslatef(-var14, -var15, -((var16 + var17) * miniCount / 2.0F));

            for (int var20 = 0; var20 < miniCount; ++var20)
            {
                // Makes items offset when in 3D, like when in 2D, looks much better. Considered a vanilla bug...
                if (var20 > 0 && shouldSpreadItems())
                {
                    float x = (this.random.get(itemstack).nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
                    float y = (this.random.get(itemstack).nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
                    float z = (this.random.get(itemstack).nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
                    GL11.glTranslatef(x, y, var16 + var17);
                }
                else
                {
                    GL11.glTranslatef(0f, 0f, var16 + var17);
                }

                this.loadTexture(Item.itemsList[itemstack.itemID].getTextureFile());

                GL11.glColor4f(par5, par6, par7, 1.0F);
                ItemRenderer.renderItemIn2D(var8, var10, var11, var9, var12, var16);

                if (itemstack != null && itemstack.hasEffect())
                {
                    GL11.glDepthFunc(GL11.GL_EQUAL);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    this.renderManager.renderEngine.bindTexture(this.renderManager.renderEngine.getTexture("%blur%/misc/glint.png"));
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                    float var21 = 0.76F;
                    GL11.glColor4f(0.5F * var21, 0.25F * var21, 0.8F * var21, 1.0F);
                    GL11.glMatrixMode(GL11.GL_TEXTURE);
                    GL11.glPushMatrix();
                    float var22 = 0.125F;
                    GL11.glScalef(var22, var22, var22);
                    float var23 = Minecraft.getSystemTime() % 3000L / 3000.0F * 8.0F;
                    GL11.glTranslatef(var23, 0.0F, 0.0F);
                    GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                    ItemRenderer.renderItemIn2D(var8, 0.0F, 0.0F, 1.0F, 1.0F, var16);
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glScalef(var22, var22, var22);
                    var23 = Minecraft.getSystemTime() % 4873L / 4873.0F * 8.0F;
                    GL11.glTranslatef(-var23, 0.0F, 0.0F);
                    GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                    ItemRenderer.renderItemIn2D(var8, 0.0F, 0.0F, 1.0F, 1.0F, 0.0625F);
                    GL11.glPopMatrix();
                    GL11.glMatrixMode(GL11.GL_MODELVIEW);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glDepthFunc(GL11.GL_LEQUAL);
                }
            }

            GL11.glPopMatrix();
        }
        else
        {
            for (int var25 = 0; var25 < par3; ++var25)
            {
                GL11.glPushMatrix();

                if (var25 > 0)
                {
                    var17 = (this.random.get(itemstack).nextFloat() * 2.0F - 1.0F) * 0.3F;
                    float var27 = (this.random.get(itemstack).nextFloat() * 2.0F - 1.0F) * 0.3F;
                    float var26 = (this.random.get(itemstack).nextFloat() * 2.0F - 1.0F) * 0.3F;
                    GL11.glTranslatef(var17, var27, var26);
                }

                if (!field_82407_g)
                {
                    GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                }

                GL11.glColor4f(par5, par6, par7, 1.0F);
                var8.startDrawingQuads();
                var8.setNormal(0.0F, 1.0F, 0.0F);
                var8.addVertexWithUV(0.0F - var14, 0.0F - var15, 0.0D, var9, var12);
                var8.addVertexWithUV(var13 - var14, 0.0F - var15, 0.0D, var10, var12);
                var8.addVertexWithUV(var13 - var14, 1.0F - var15, 0.0D, var10, var11);
                var8.addVertexWithUV(0.0F - var14, 1.0F - var15, 0.0D, var9, var11);
                var8.draw();
                GL11.glPopMatrix();
            }
        }
    }
}
