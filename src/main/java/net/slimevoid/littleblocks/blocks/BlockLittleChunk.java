package net.slimevoid.littleblocks.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidBlock;
import net.slimevoid.library.util.helpers.ChatHelper;
import net.slimevoid.library.util.helpers.PacketHelper;
import net.slimevoid.library.util.helpers.SlimevoidHelper;
import net.slimevoid.littleblocks.api.ILittleWorld;
import net.slimevoid.littleblocks.blocks.core.CollisionRayTrace;
import net.slimevoid.littleblocks.client.render.entities.LittleBlockDiggingFX;
import net.slimevoid.littleblocks.core.LittleBlocks;
import net.slimevoid.littleblocks.core.lib.BlockUtil;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.core.lib.IconLib;
import net.slimevoid.littleblocks.core.lib.MessageLib;
import net.slimevoid.littleblocks.core.lib.PacketLib;
import net.slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;
import net.slimevoid.littleblocks.items.ItemLittleBlocksWand;
import net.slimevoid.littleblocks.network.packets.PacketLittleBlocksCollection;
import net.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLittleChunk extends BlockContainer {

    public static int      currentPass;

    public static int      xSelected      = -10;
    public static int      ySelected      = -10;
    public static int      zSelected      = -10;
    public static int      side           = -1;
    public static Vec3     hitVec         = null;

    public boolean         updateEveryone = true;

    private static boolean isFluid        = false;

    public void registerIcons(IIconRegister iconRegister) {
        this.blockIcon = iconRegister.registerIcon(IconLib.LB_CHUNK);
    }

    private Class<? extends TileEntity> clazz;

    public BlockLittleChunk(int id, Class<? extends TileEntity> clazz, Material material, float hardness, boolean selfNotify) {
        super(material);
        this.clazz = clazz;
        this.setHardness(hardness);
        this.stepSound = Block.soundTypeWood;
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public boolean isBlockSolid(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return false;
    }

    @Override
    public int getRenderBlockPass() {
        return 0;
    }

    @Override
    public boolean canRenderInPass(int pass) {
        currentPass = pass;
        return true;
    }

    @Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
        if (entity != null) {
            return SlimevoidHelper.isLadder(world,
                                            x,
                                            y,
                                            z,
                                            entity);
        }
        return false;
    }

    @Override
    public Item getItem(World world, int x, int y, int z) {
        TileEntity tileentity = world.getTileEntity(x,
                                                    y,
                                                    z);
        if (tileentity != null && tileentity instanceof TileEntityLittleChunk) {
            TileEntityLittleChunk tilelb = (TileEntityLittleChunk) tileentity;
            Block pickedBlock = tilelb.getBlock(xSelected,
                                                ySelected,
                                                zSelected);
            if (pickedBlock != Blocks.air) {
                return Item.getItemFromBlock(pickedBlock);
            }
        }
        return ConfigurationLib.littleBlocksWand;
    }

    @Override
    public int getDamageValue(World world, int x, int y, int z) {
        TileEntity tileentity = world.getTileEntity(x,
                                                    y,
                                                    z);
        if (tileentity != null && tileentity instanceof TileEntityLittleChunk) {
            TileEntityLittleChunk tilelb = (TileEntityLittleChunk) tileentity;
            Block picked = tilelb.getBlock(xSelected,
                                           ySelected,
                                           zSelected);
            if (picked != null && picked != Blocks.air) {
                int xx = (x << 3) + xSelected;
                int yy = (y << 3) + ySelected;
                int zz = (z << 3) + zSelected;
                return picked.getDamageValue((World) tilelb.getLittleWorld(),
                                             xx,
                                             yy,
                                             zz);
            }
        }
        return 0;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer entityplayer, int x, int y, int z) {
        Block id = world.getBlock(x,
                                  y,
                                  z);

        if (id == ConfigurationLib.littleChunk) {
            TileEntityLittleChunk tile = (TileEntityLittleChunk) world.getTileEntity(x,
                                                                                     y,
                                                                                     z);
            EntityItemLittleBlocksCollection collection = new EntityItemLittleBlocksCollection(world, x, y, z, new ItemStack(ConfigurationLib.littleChunk));
            if (!tile.isEmpty()) {
                if (FMLCommonHandler.instance().getSide() == Side.CLIENT
                    && FMLClientHandler.instance().getClient().playerController.isInCreativeMode()) {
                    this.onBlockClicked(world,
                                        x,
                                        y,
                                        z,
                                        entityplayer);
                    return false;
                } else if (entityplayer.capabilities.isCreativeMode) {
                    this.onBlockClicked(world,
                                        x,
                                        y,
                                        z,
                                        entityplayer);
                    return false;
                } else {
                    if (collection != null) {
                        for (int x1 = 0; x1 < tile.size; x1++) {
                            for (int y1 = 0; y1 < tile.size; y1++) {
                                for (int z1 = 0; z1 < tile.size; z1++) {
                                    Block blockId = tile.getBlock(x1,
                                                                  y1,
                                                                  z1);
                                    int contentMeta = tile.getBlockMetadata(x1,
                                                                            y1,
                                                                            z1);
                                    if (blockId != Blocks.air) {
                                        ItemStack itemToDrop = this.dropLittleBlockAsNormalBlock(world,
                                                                                                 x,
                                                                                                 y,
                                                                                                 z,
                                                                                                 blockId,
                                                                                                 contentMeta);
                                        if (itemToDrop != null) {
                                            collection.addItemToDrop(itemToDrop);
                                        }
                                    }
                                }
                            }
                        }
                        tile.clearContents();
                        tile.markDirty();
                        world.markBlockForUpdate(x,
                                                 y,
                                                 z);
                    }
                }
            }
            if (!world.isRemote) {
                if (!collection.isEmpty()) {
                    world.spawnEntityInWorld(collection);
                    PacketLittleBlocksCollection packet = new PacketLittleBlocksCollection(collection);
                    PacketHelper.broadcastPacket(packet);
                }
            }
        }
        return super.removedByPlayer(world,
                                     entityplayer,
                                     x,
                                     y,
                                     z);
    }

    private ItemStack dropLittleBlockAsNormalBlock(World world, int x, int y, int z, Block blockId, int metaData) {
        boolean dropsBlocks = blockId.getDrops(world,
                                               x,
                                               y,
                                               z,
                                               metaData,
                                               0).size() > 0 ? true : false;
        if (dropsBlocks) {
            Item idDropped = blockId.getItemDropped(metaData,
                                                    world.rand,
                                                    0);
            int quantityDropped = blockId.quantityDropped(world.rand);
            int damageDropped = blockId.damageDropped(metaData);
            ItemStack itemstack = new ItemStack(idDropped, quantityDropped, damageDropped);

            if (quantityDropped > 0) {
                return itemstack;
            }
        }
        return null;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int localSide, float hitX, float hitY, float hitZ) {
        // System.out.println("Activated");
        if (world.isRemote) {
            if (this.canPlayerPlaceBlockOrUseItem(world,
                                                  entityplayer)) {
                try {
                    BlockUtil.getLittleController().onPlayerRightClickFirst(entityplayer,
                                                                            (World) LittleBlocks.proxy.getLittleWorld(world,
                                                                                                                      false),
                                                                            entityplayer.inventory.getCurrentItem(),
                                                                            ((x << 3) + xSelected),
                                                                            ((y << 3) + ySelected),
                                                                            ((z << 3) + zSelected),
                                                                            side,
                                                                            hitX,
                                                                            hitY,
                                                                            hitZ);
                } catch (ClassCastException e) {
                    FMLCommonHandler.instance().getFMLLogger().warn(e.getLocalizedMessage());
                    return true;
                }
            }
        }
        return true;
    }

    private boolean canPlayerPlaceBlockOrUseItem(World world, EntityPlayer entityplayer) {
        boolean denyPlacement = false;
        String placementMessage = "";
        ItemStack itemstack = entityplayer.getCurrentEquippedItem();
        if (itemstack != null) {
            Object itemHeld = itemstack.getItem();
            ItemBlock itemBlock = null;
            Block block = null;
            Item item = null;
            if (itemHeld instanceof Block) {
                block = (Block) itemHeld;
            }
            if (itemHeld instanceof Item) {
                item = (Item) itemHeld;
            }
            if (itemHeld instanceof ItemBlock) {
                itemBlock = (ItemBlock) itemHeld;
                block = itemBlock.field_150939_a;
            }
            if (block != null) {
                if (!BlockUtil.isBlockAllowed(block)) {
                    denyPlacement = true;
                    placementMessage = MessageLib.DENY_PLACEMENT;
                }
                if (block.hasTileEntity(0)) {
                    if (!BlockUtil.isTileEntityAllowed(block.createTileEntity(world,
                                                                              0))) {
                        denyPlacement = true;
                        placementMessage = MessageLib.DENY_PLACEMENT;
                    }
                }
            }
            if (item != null) {
                if (item instanceof ItemBlock) {
                    Block itemBlockId = ((ItemBlock) item).field_150939_a;
                    if (itemBlockId.hasTileEntity(0)) {
                        if (!BlockUtil.isTileEntityAllowed(itemBlockId.createTileEntity(world,
                                                                                        0))) {
                            denyPlacement = true;
                            placementMessage = MessageLib.DENY_PLACEMENT;
                        }
                    }
                }
                if (!BlockUtil.isItemAllowed(item)) {
                    denyPlacement = true;
                    placementMessage = MessageLib.DENY_USE;
                }
            }
        }
        if (denyPlacement) {
            ChatHelper.addColouredMessageToPlayer(entityplayer,
                                                  EnumChatFormatting.BOLD,
                                                  placementMessage);
            return false;
        }
        return true;
    }

    public void onServerBlockActivated(World world, EntityPlayer entityplayer, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!entityplayer.canPlayerEdit(x >> 3,
                                        y >> 3,
                                        z >> 3,
                                        side,
                                        entityplayer.getHeldItem())) {
            return;
        }
        if (entityplayer.getHeldItem() != null
            && entityplayer.getHeldItem().getItem() instanceof ItemLittleBlocksWand) {
            return;
        }
        if (this.canPlayerPlaceBlockOrUseItem(world,
                                              entityplayer)) {
            try {
                BlockUtil.onServerBlockActivated(world,
                                                 entityplayer,
                                                 entityplayer.getCurrentEquippedItem(),
                                                 x,
                                                 y,
                                                 z,
                                                 side,
                                                 hitX,
                                                 hitY,
                                                 hitZ);
            } catch (ClassCastException e) {
                FMLCommonHandler.instance().getFMLLogger().warn(e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer entityplayer) {
        if (world.isRemote) {
            BlockUtil.getLittleController().clickBlock((x << 3) + xSelected,
                                                       (y << 3) + ySelected,
                                                       (z << 3) + zSelected,
                                                       side);
        }
    }

    public void onServerBlockClicked(World world, int x, int y, int z, int side, EntityPlayer entityplayer) {
        if (entityplayer instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entityplayer;
            if (!FMLCommonHandler.instance().getMinecraftServerInstance().isBlockProtected(((ILittleWorld) world).getParentWorld(),
                                                                                           x >> 3,
                                                                                           y >> 3,
                                                                                           z >> 3,
                                                                                           entityplayer)) {
                BlockUtil.getLittleItemManager(player,
                                               world).onBlockClicked(x,
                                                                     y,
                                                                     z,
                                                                     side);
            } else {
                PacketLib.sendBlockChange(world,
                                          entityplayer,
                                          x,
                                          y,
                                          z);
            }
        }
    }

    @Override
    public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
        return super.rotateBlock(world,
                                 x,
                                 y,
                                 z,
                                 axis);
    }

    public boolean rotateLittleChunk(World world, int x, int y, int z, ForgeDirection axis) {
        TileEntity tileentity = world.getTileEntity(x,
                                                    y,
                                                    z);
        if (tileentity != null && tileentity instanceof TileEntityLittleChunk) {
            ((TileEntityLittleChunk) tileentity).rotateContents(axis);
            tileentity.markDirty();
            world.markBlockForUpdate(x,
                                     y,
                                     z);
        }
        return false;
    }

    public void dropLittleBlockAsItem_do(World world, int x, int y, int z, ItemStack itemStack) {
        this.dropBlockAsItem(world,
                             x,
                             y,
                             z,
                             itemStack);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        try {
            return this.clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getRenderType() {
        return ConfigurationLib.renderType;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    private void setBlockBoundsBasedOnSelection(World world, int x, int y, int z) {
        float m = ConfigurationLib.littleBlocksSize;
        if (xSelected == -10) {
            this.setBlockBounds(0f,
                                0f,
                                0f,
                                0f,
                                0f,
                                0f);
        } else {
            TileEntityLittleChunk tile = (TileEntityLittleChunk) world.getTileEntity(x,
                                                                                     y,
                                                                                     z);
            Block block = tile.getBlock(xSelected,
                                        ySelected,
                                        zSelected);
            // System.out.println("Content: " + content);
            if (block == null) {
                this.setBlockBounds(xSelected / m,
                                    ySelected / m,
                                    zSelected / m,
                                    (xSelected + 1) / m,
                                    (ySelected + 1) / m,
                                    (zSelected + 1) / m);
            } else {
                if (block != null) {
                    if (BlockStairs.func_150148_a/* isBlockStairsID */(block)) {
                        block.setBlockBounds(0,
                                             0,
                                             0,
                                             1,
                                             1,
                                             1);
                    } else {
                        block.setBlockBoundsBasedOnState(tile.getLittleWorld(),
                                                         (x << 3) + xSelected,
                                                         (y << 3) + ySelected,
                                                         (z << 3) + zSelected);
                    }
                    this.setBlockBounds((float) (xSelected + block.getBlockBoundsMinX())
                                                / m,
                                        (float) (ySelected + block.getBlockBoundsMinY())
                                                / m,
                                        (float) (zSelected + block.getBlockBoundsMinZ())
                                                / m,
                                        (float) (xSelected + block.getBlockBoundsMaxX())
                                                / m,
                                        (float) (ySelected + block.getBlockBoundsMaxY())
                                                / m,
                                        (float) (zSelected + block.getBlockBoundsMaxZ())
                                                / m);
                }
            }
        }
    }

    @Override
    public boolean canCollideCheck(int meta, boolean rightClicked) {
        isFluid = rightClicked;
        return super.canCollideCheck(meta,
                                     rightClicked);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        if (ConfigurationLib.littleBlocksClip) {
            return super.getCollisionBoundingBoxFromPool(world,
                                                         x,
                                                         y,
                                                         z);
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axisalignedbb, List list, Entity entity) {
        TileEntity tileentity = world.getTileEntity(x,
                                                    y,
                                                    z);
        if (tileentity != null && tileentity instanceof TileEntityLittleChunk) {
            TileEntityLittleChunk tile = (TileEntityLittleChunk) tileentity;

            float m = ConfigurationLib.littleBlocksSize;

            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(axisalignedbb.minX
                                                                    * m,
                                                            axisalignedbb.minY
                                                                    * m,
                                                            axisalignedbb.minZ
                                                                    * m,
                                                            axisalignedbb.maxX
                                                                    * m,
                                                            axisalignedbb.maxY
                                                                    * m,
                                                            axisalignedbb.maxZ
                                                                    * m);
            List<AxisAlignedBB> bbs = new ArrayList<AxisAlignedBB>();
            for (int xx = 0; xx < tile.size; xx++) {
                for (int yy = 0; yy < tile.size; yy++) {
                    for (int zz = 0; zz < tile.size; zz++) {
                        if (tile.getBlock(xx,
                                          yy,
                                          zz) != Blocks.air) {
                            Block block = tile.getBlock(xx,
                                                        yy,
                                                        zz);
                            if (block != null) {
                                block.addCollisionBoxesToList((World) tile.getLittleWorld(),
                                                              (x << 3) + xx,
                                                              (y << 3) + yy,
                                                              (z << 3) + zz,
                                                              bb,
                                                              bbs,
                                                              entity);
                            }
                        }
                    }
                }
            }
            for (AxisAlignedBB aabb : bbs) {
                aabb.setBounds(aabb.minX / m,
                               aabb.minY / m,
                               aabb.minZ / m,
                               aabb.maxX / m,
                               aabb.maxY / m,
                               aabb.maxZ / m);
                list.add(aabb);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 player, Vec3 view) {
        TileEntityLittleChunk tile = (TileEntityLittleChunk) world.getTileEntity(x,
                                                                                 y,
                                                                                 z);

        if (tile == null) {
            return null;
        }

        List<MovingObjectPosition> returns = new ArrayList<MovingObjectPosition>();

        returns = CollisionRayTrace.rayTraceLittleBlocks(this,
                                                         player,
                                                         view,
                                                         x,
                                                         y,
                                                         z,
                                                         returns,
                                                         tile,
                                                         isFluid);
        player = player.addVector(-x,
                                  -y,
                                  -z);
        view = view.addVector(-x,
                              -y,
                              -z);

        returns = CollisionRayTrace.collisionRayTracer(this,
                                                       world,
                                                       player,
                                                       view,
                                                       x,
                                                       y,
                                                       z,
                                                       returns);
        if (!returns.isEmpty()) {
            MovingObjectPosition min = null;
            double distMin = 0;
            boolean isLiquid = false;
            for (MovingObjectPosition ret : returns) {
                double dist = (double) ret.hitVec.squareDistanceTo(player);

                Block retBlock = tile.getBlock(ret.blockX,
                                               ret.blockY,
                                               ret.blockZ);
                if (retBlock != Blocks.air) {
                    isLiquid = retBlock instanceof IFluidBlock;
                    if (isLiquid && isFluid) {
                        isLiquid = !(retBlock instanceof BlockStaticLiquid && tile.getBlockMetadata(ret.blockX,
                                                                                                    ret.blockY,
                                                                                                    ret.blockZ) == 0);
                    }
                }

                if ((min == null || dist < distMin) && !isLiquid) {
                    distMin = dist;
                    min = ret;
                }
            }
            Block littleBlock = tile.getBlock(xSelected,
                                              ySelected,
                                              zSelected);

            if (min != null) {
                side = (byte) min.sideHit;
                xSelected = (int) min.blockX;
                ySelected = (int) min.blockY;
                zSelected = (int) min.blockZ;
                if (isFluid) {
                    littleBlock = tile.getBlock(xSelected,
                                                ySelected,
                                                zSelected);
                }
                boolean rayTraced = false;
                if (littleBlock != Blocks.air) {
                    if (littleBlock != null) {
                        if (!(littleBlock.hasTileEntity(tile.getBlockMetadata(xSelected,
                                                                              ySelected,
                                                                              zSelected)) && tile.getChunkTileEntity(xSelected,
                                                                                                                     ySelected,
                                                                                                                     zSelected) == null)) {

                            try {
                                littleBlock.collisionRayTrace((World) tile.getLittleWorld(),
                                                              (x << 3)
                                                                      + xSelected,
                                                              (y << 3)
                                                                      + ySelected,
                                                              (z << 3)
                                                                      + zSelected,
                                                              player,
                                                              view);
                            } catch (ClassCastException e) {
                                FMLCommonHandler.instance().getFMLLogger().warn(e.getLocalizedMessage());
                            }
                        }
                    }
                }
                this.setBlockBoundsBasedOnSelection(world,
                                                    x,
                                                    y,
                                                    z);
                return new MovingObjectPosition(x, y, z, (byte) min.sideHit,
                /** ((Vec3) min.hitVec).addVector(x, y, z) **/
                hitVec = Vec3.createVectorHelper((min.hitVec.xCoord * 8) % 1,
                                                          (min.hitVec.yCoord * 8) % 1,
                                                          (min.hitVec.zCoord * 8) % 1).addVector(x,
                                                                                                          y,
                                                                                                          z));
            }
        }
        xSelected = -10;
        ySelected = -10;
        zSelected = -10;
        side = -1;
        hitVec = null;
        this.setBlockBoundsBasedOnSelection(world,
                                            x,
                                            y,
                                            z);

        return null;
    }

    public MovingObjectPosition rayTraceBound(AxisAlignedBB bound, int i, int j, int k, Vec3 player, Vec3 view) {
        Vec3 minX = player.getIntermediateWithXValue(view,
                                                     bound.minX);
        Vec3 maxX = player.getIntermediateWithXValue(view,
                                                     bound.maxX);
        Vec3 minY = player.getIntermediateWithYValue(view,
                                                     bound.minY);
        Vec3 maxY = player.getIntermediateWithYValue(view,
                                                     bound.maxY);
        Vec3 minZ = player.getIntermediateWithZValue(view,
                                                     bound.minZ);
        Vec3 maxZ = player.getIntermediateWithZValue(view,
                                                     bound.maxZ);
        if (!this.isVecInsideYZBounds(bound,
                                      minX)) {
            minX = null;
        }
        if (!this.isVecInsideYZBounds(bound,
                                      maxX)) {
            maxX = null;
        }
        if (!this.isVecInsideXZBounds(bound,
                                      minY)) {
            minY = null;
        }
        if (!this.isVecInsideXZBounds(bound,
                                      maxY)) {
            maxY = null;
        }
        if (!this.isVecInsideXYBounds(bound,
                                      minZ)) {
            minZ = null;
        }
        if (!isVecInsideXYBounds(bound,
                                 maxZ)) {
            maxZ = null;
        }
        Vec3 tracedBound = null;
        if (minX != null
            && (tracedBound == null || player.squareDistanceTo(minX) < player.squareDistanceTo(tracedBound))) {
            tracedBound = minX;
        }
        if (maxX != null
            && (tracedBound == null || player.squareDistanceTo(maxX) < player.squareDistanceTo(tracedBound))) {
            tracedBound = maxX;
        }
        if (minY != null
            && (tracedBound == null || player.squareDistanceTo(minY) < player.squareDistanceTo(tracedBound))) {
            tracedBound = minY;
        }
        if (maxY != null
            && (tracedBound == null || player.squareDistanceTo(maxY) < player.squareDistanceTo(tracedBound))) {
            tracedBound = maxY;
        }
        if (minZ != null
            && (tracedBound == null || player.squareDistanceTo(minZ) < player.squareDistanceTo(tracedBound))) {
            tracedBound = minZ;
        }
        if (maxZ != null
            && (tracedBound == null || player.squareDistanceTo(maxZ) < player.squareDistanceTo(tracedBound))) {
            tracedBound = maxZ;
        }
        if (tracedBound == null) {
            return null;
        }
        byte side = -1;
        if (tracedBound == minX) {
            side = 4;
        }
        if (tracedBound == maxX) {
            side = 5;
        }
        if (tracedBound == minY) {
            side = 0;
        }
        if (tracedBound == maxY) {
            side = 1;
        }
        if (tracedBound == minZ) {
            side = 2;
        }
        if (tracedBound == maxZ) {
            side = 3;
        }
        return new MovingObjectPosition(i, j, k, side, tracedBound);
    }

    private boolean isVecInsideYZBounds(AxisAlignedBB bound, Vec3 Vec3) {
        if (Vec3 == null) {
            return false;
        } else {
            return Vec3.yCoord >= bound.minY && Vec3.yCoord <= bound.maxY
                   && Vec3.zCoord >= bound.minZ && Vec3.zCoord <= bound.maxZ;
        }
    }

    private boolean isVecInsideXZBounds(AxisAlignedBB bound, Vec3 Vec3) {
        if (Vec3 == null) {
            return false;
        } else {
            return Vec3.xCoord >= bound.minX && Vec3.xCoord <= bound.maxX
                   && Vec3.zCoord >= bound.minZ && Vec3.zCoord <= bound.maxZ;
        }
    }

    private boolean isVecInsideXYBounds(AxisAlignedBB bound, Vec3 Vec3) {
        if (Vec3 == null) {
            return false;
        } else {
            return Vec3.xCoord >= bound.minX && Vec3.xCoord <= bound.maxX
                   && Vec3.yCoord >= bound.minY && Vec3.yCoord <= bound.maxY;
        }
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess iblockaccess, int x, int y, int z, int side) {
        int weakPower = super.isProvidingWeakPower(iblockaccess,
                                                   x,
                                                   y,
                                                   z,
                                                   side);
        if (weakPower > 0) {
            return weakPower;
        } else {
            TileEntityLittleChunk tile = (TileEntityLittleChunk) iblockaccess.getTileEntity(x,
                                                                                            y,
                                                                                            z);
            if (tile != null) {
                int maX = tile.size, maY = tile.size, maZ = tile.size;
                int startX = 0, startY = 0, startZ = 0;

                switch (side) {
                case 1:
                    maY = 1;
                    break;

                case 0:
                    startY = maY - 1;
                    break;

                case 3:
                    maZ = 1;
                    break;

                case 2:
                    startZ = maZ - 1;
                    break;

                case 5:
                    maX = 1;
                    break;

                case 4:
                    startX = maX - 1;
                    break;
                }

                for (int xx = startX; xx < maX; xx++) {
                    for (int yy = startY; yy < maY; yy++) {
                        for (int zz = startZ; zz < maZ; zz++) {
                            if (tile.getBlock(xx,
                                              yy,
                                              zz) != Blocks.air) {
                                Block littleBlock = tile.getBlock(xx,
                                                                  yy,
                                                                  zz);
                                if (littleBlock != null) {
                                    return littleBlock.isProvidingWeakPower(tile.getLittleWorld(),
                                                                            (x << 3)
                                                                                    + xx,
                                                                            (y << 3)
                                                                                    + yy,
                                                                            (z << 3)
                                                                                    + zz,
                                                                            side);
                                }
                            }
                        }
                    }
                }
            }
            return 0;
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block blockId) {
        super.onNeighborBlockChange(world,
                                    x,
                                    y,
                                    z,
                                    blockId);
        if (updateEveryone) {
            TileEntityLittleChunk tile = (TileEntityLittleChunk) world.getTileEntity(x,
                                                                                     y,
                                                                                     z);
            if (tile != null) {
                int maX = tile.size, maY = tile.size, maZ = tile.size;
                int startX = 0, startY = 0, startZ = 0;
                for (int side = 0; side < 6; side++) {
                    switch (side) {
                    case 0:
                        maY = 1;
                        break;

                    case 1:
                        startY = maY - 1;
                        break;

                    case 2:
                        maZ = 1;
                        break;

                    case 3:
                        startZ = maZ - 1;
                        break;

                    case 4:
                        maX = 1;
                        break;

                    case 5:
                        startX = maX - 1;
                        break;
                    }

                    for (int xx = startX; xx < maX; xx++) {
                        for (int yy = startY; yy < maY; yy++) {
                            for (int zz = startZ; zz < maZ; zz++) {
                                if (tile.getBlock(xx,
                                                  yy,
                                                  zz) != Blocks.air) {
                                    Block littleBlock = tile.getBlock(xx,
                                                                      yy,
                                                                      zz);
                                    if (littleBlock != null) {
                                        littleBlock.onNeighborBlockChange((World) tile.getLittleWorld(),
                                                                          (x << 3)
                                                                                  + xx,
                                                                          (y << 3)
                                                                                  + yy,
                                                                          (z << 3)
                                                                                  + zz,
                                                                          blockId);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                super.onNeighborBlockChange(world,
                                            x,
                                            y,
                                            z,
                                            blockId);
            }
        }
    }

    @Override
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
        return LittleBlockDiggingFX.doBlockDestroyEffects(world,
                                                          x,
                                                          y,
                                                          z,
                                                          meta,
                                                          effectRenderer,
                                                          this);
    }

    @Override
    public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer) {
        return LittleBlockDiggingFX.doBlockHitEffects(world,
                                                      target,
                                                      effectRenderer,
                                                      this);
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntityLittleChunk tile = (TileEntityLittleChunk) world.getTileEntity(x,
                                                                                 y,
                                                                                 z);
        try {
            if (tile != null) return tile.getLightlevel();
        } catch (ClassCastException e) {
            FMLCommonHandler.instance().getFMLLogger().warn(e.getLocalizedMessage());
            return super.getLightValue(world,
                                       x,
                                       y,
                                       z);
        }
        return super.getLightValue(world,
                                   x,
                                   y,
                                   z);
    }
}
