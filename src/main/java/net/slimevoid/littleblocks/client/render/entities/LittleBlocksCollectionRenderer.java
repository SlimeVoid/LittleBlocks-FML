package net.slimevoid.littleblocks.client.render.entities;

import java.util.Random;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;

public class LittleBlocksCollectionRenderer extends Render {

    @Override
    public void doRender(Entity entity, double d0, double d1, double d2, float f, float f1) {
        if (entity instanceof EntityItemLittleBlocksCollection) {
            EntityItemLittleBlocksCollection e = (EntityItemLittleBlocksCollection) entity;
            Random rand = new Random(e.getEntityId());
            for (ItemStack itemstack : e.getCollection().values()) {
                try {
                    EntityItem item = new EntityItem(e.worldObj, e.posX, e.posY, e.posZ, itemstack);
                    item.age = e.age;
                    item.hoverStart = rand.nextFloat();
                    RenderManager.instance.renderEntitySimple(item,
                                                              0);
                } catch (Exception eerazrt) {
                    eerazrt.printStackTrace();
                }
            }
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }

}
