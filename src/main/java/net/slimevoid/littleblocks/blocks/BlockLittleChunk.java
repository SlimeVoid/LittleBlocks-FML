package net.slimevoid.littleblocks.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
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
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.slimevoid.library.util.helpers.ChatHelper;
import net.slimevoid.library.util.helpers.PacketHelper;
import net.slimevoid.library.util.helpers.SlimevoidHelper;
import net.slimevoid.littleblocks.api.ILittleWorld;
import net.slimevoid.littleblocks.blocks.core.CollisionRayTrace;
import net.slimevoid.littleblocks.client.render.entities.LittleBlockDiggingFX;
import net.slimevoid.littleblocks.core.LittleBlocks;
import net.slimevoid.littleblocks.core.lib.BlockUtil;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.core.lib.MessageLib;
import net.slimevoid.littleblocks.core.lib.PacketLib;
import net.slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;
import net.slimevoid.littleblocks.items.ItemLittleBlocksWand;
import net.slimevoid.littleblocks.network.packets.PacketLittleBlocksCollection;
import net.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockLittleChunk extends BlockContainer {

    public static int      currentPass;

    public static int      xSelected      = -10;
    public static int      ySelected      = -10;
    public static int      zSelected      = -10;
    public static int      side           = -1;
    public static Vec3     hitVec         = null;

    public boolean         updateEveryone = true;

    private static boolean isFluid        = false;

    private Class<? extends TileEntity> clazz;

    public BlockLittleChunk(int id, Class<? extends TileEntity> clazz, Material material, float hardness, boolean selfNotify) {
        super(material);
        this.clazz = clazz;
        this.setHardness(hardness);
        this.stepSound = Block.soundTypeWood;
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public boolean isLadder(IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        if (entity != null) {
            return SlimevoidHelper.isLadder(world,
                                            pos,
                                            entity);
        }
        return false;
    }

    @Override
    public Item getItem(World world, BlockPos pos) {
        TileEntity tileentity = world.getTileEntity(pos);
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
    public int getDamageValue(World world, BlockPos pos) {
        TileEntity tileentity = world.getTileEntity(pos);
        if (tileentity != null && tileentity instanceof TileEntityLittleChunk) {
            TileEntityLittleChunk tilelb = (TileEntityLittleChunk) tileentity;
            Block picked = tilelb.getBlock(xSelected,
                                           ySelected,
                                           zSelected);
            if (picked != null && picked != Blocks.air) {
                int xx = (pos.getX() << 3) + xSelected;
                int yy = (pos.getY() << 3) + ySelected;
                int zz = (pos.getZ() << 3) + zSelected;
                BlockPos littlePos = new BlockPos(xx, yy, zz);
                return picked.getDamageValue((World) tilelb.getLittleWorld(),
                                             littlePos);
            }
        }
        return 0;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer entityplayer, boolean willHarvest) {
        Block id = world.getBlockState(pos).getBlock();

        if (id == ConfigurationLib.littleChunk) {
            TileEntityLittleChunk tile = (TileEntityLittleChunk) world.getTileEntity(pos);
            EntityItemLittleBlocksCollection collection = new EntityItemLittleBlocksCollection(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ConfigurationLib.littleChunk));
            if (!tile.isEmpty()) {
                if (FMLCommonHandler.instance().getSide() == Side.CLIENT
                    && FMLClientHandler.instance().getClient().playerController.isInCreativeMode()) {
                    this.onBlockClicked(world,
                                        pos,
                                        entityplayer);
                    return false;
                } else if (entityplayer.capabilities.isCreativeMode) {
                    this.onBlockClicked(world,
                                        pos,
                                        entityplayer);
                    return false;
                } else {
                    if (collection != null) {
                        for (int x1 = 0; x1 < tile.size; x1++) {
                            for (int y1 = 0; y1 < tile.size; y1++) {
                                for (int z1 = 0; z1 < tile.size; z1++) {
                                    IBlockState blockState = tile.getBlockState(x1, y1, z1);
                                    if (!blockState.getBlock().isAssociatedBlock(Blocks.air)) {
                                        ItemStack itemToDrop = this.dropLittleBlockAsNormalBlock(world,
                                                                                                 pos,
                                                                                                 blockState.getBlock(),
                                                                                                 blockState);
                                        if (itemToDrop != null) {
                                            collection.addItemToDrop(itemToDrop);
                                        }
                                    }
                                }
                            }
                        }
                        tile.clearContents();
                        tile.markDirty();
                        world.markBlockForUpdate(pos);
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
        							 pos,
                                     entityplayer,
                                     willHarvest);
    }

    private ItemStack dropLittleBlockAsNormalBlock(World world, BlockPos pos, Block block, IBlockState state) {
        boolean dropsBlocks = block.getDrops(
                world,
                pos,
                state,
                0).size() > 0 ? true : false;
        if (dropsBlocks) {
            Item idDropped = block.getItemDropped(
                    state,
                    world.rand,
                    0);
            int quantityDropped = block.quantityDropped(world.rand);
            int damageDropped = block.damageDropped(state);
            ItemStack itemstack = new ItemStack(idDropped, quantityDropped, damageDropped);

            if (quantityDropped > 0) {
                return itemstack;
            }
        }
        return null;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState blockState, EntityPlayer entityplayer, EnumFacing localSide, float hitX, float hitY, float hitZ) {
        // System.out.println("Activated");
        if (world.isRemote) {
            if (this.canPlayerPlaceBlockOrUseItem(world,
                                                  entityplayer)) {
                try {
                    BlockUtil.getLittleController().onPlayerRightClickFirst(
                            (EntityPlayerSP) entityplayer,
                            (WorldClient) LittleBlocks.proxy.getLittleWorld(
                                    world,
                                    false),
                            entityplayer.inventory.getCurrentItem(),
                            BlockUtil.getLittleChunkPos(pos).add(xSelected, ySelected, zSelected),
                            EnumFacing.getFront(side),
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
                block = itemBlock.getBlock();
            }
            if (block != null) {
                if (!BlockUtil.isBlockAllowed(block)) {
                    denyPlacement = true;
                    placementMessage = MessageLib.DENY_PLACEMENT;
                }
                if (block.hasTileEntity(block.getDefaultState())) {
                    if (!BlockUtil.isTileEntityAllowed(block.createTileEntity(world,
                                                                              block.getDefaultState()))) {
                        denyPlacement = true;
                        placementMessage = MessageLib.DENY_PLACEMENT;
                    }
                }
            }
            if (item != null) {
                if (item instanceof ItemBlock) {
                    Block blockItem = ((ItemBlock) item).getBlock();
                    if (blockItem.hasTileEntity(blockItem.getDefaultState())) {
                        if (!BlockUtil.isTileEntityAllowed(blockItem.createTileEntity(world,
                                                                                        blockItem.getDefaultState()))) {
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

    public void onServerBlockActivated(World world, EntityPlayer entityplayer, BlockPos pos, int direction, float hitX, float hitY, float hitZ) {
        EnumFacing side = EnumFacing.getFront(direction);
        if (!entityplayer.canPlayerEdit(BlockUtil.getParentPos(pos),
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
                                                 pos,
                                                 direction,
                                                 hitX,
                                                 hitY,
                                                 hitZ);
            } catch (ClassCastException e) {
                FMLCommonHandler.instance().getFMLLogger().warn(e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer entityplayer) {
        if (world.isRemote) {
            BlockUtil.getLittleController().clickBlock(BlockUtil.getLittleChunkPos(pos).add(xSelected, ySelected, zSelected),
                                                       EnumFacing.getFront(side));
        }
    }

    public void onServerBlockClicked(World world, BlockPos pos, EnumFacing side, EntityPlayer entityplayer) {
        if (entityplayer instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entityplayer;
            if (!FMLCommonHandler.instance().getMinecraftServerInstance().isBlockProtected(((ILittleWorld) world).getParentWorld(),
                                                                                           BlockUtil.getParentPos(pos),
                                                                                           entityplayer)) {
                BlockUtil.getLittleItemManager(player,
                                               world).onBlockClicked(pos,
                                                                     side);
            } else {
                PacketLib.sendBlockChange(world,
                                          entityplayer,
                                          pos);
            }
        }
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        return super.rotateBlock(world,
                                 pos,
                                 axis);
    }

    public boolean rotateLittleChunk(World world, BlockPos pos, EnumFacing axis) {
        TileEntity tileentity = world.getTileEntity(pos);
        if (tileentity != null && tileentity instanceof TileEntityLittleChunk) {
            ((TileEntityLittleChunk) tileentity).rotateContents(axis);
            tileentity.markDirty();
            world.markBlockForUpdate(pos);
        }
        return false;
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

    private void setBlockBoundsBasedOnSelection(World world, BlockPos pos) {
        float m = ConfigurationLib.littleBlocksSize;
        if (xSelected == -10) {
            this.setBlockBounds(0f,
                                0f,
                                0f,
                                0f,
                                0f,
                                0f);
        } else {
            TileEntityLittleChunk tile = (TileEntityLittleChunk) world.getTileEntity(pos);
            Block block = tile.getBlock(xSelected,
                                        ySelected,
                                        zSelected);
            // System.out.println("Block: " + block.getLocalizedName());
            if (block == null) {
                this.setBlockBounds(xSelected / m,
                                    ySelected / m,
                                    zSelected / m,
                                    (xSelected + 1) / m,
                                    (ySelected + 1) / m,
                                    (zSelected + 1) / m);
            } else {
                if (block != null) {
                    if (BlockStairs.isBlockStairs(block)) {
                        block.setBlockBounds(0,
                                             0,
                                             0,
                                             1,
                                             1,
                                             1);
                    } else {
                        block.setBlockBoundsBasedOnState(tile.getLittleWorld(), BlockUtil.getLittleChunkPos(pos).add(xSelected, ySelected, zSelected));
                    }
                    double  blockBoundsMinX = block.getBlockBoundsMinX(),
                            blockBoundsMinY = block.getBlockBoundsMinY(),
                            blockBoundsMinZ = block.getBlockBoundsMinZ(),
                            blockBoundsMaxX = block.getBlockBoundsMaxX(),
                            blockBoundsMaxY = block.getBlockBoundsMaxY(),
                            blockBoundsMaxZ = block.getBlockBoundsMaxZ();
                    float   minX = (float) (xSelected + blockBoundsMinX),
                            minY = (float) (ySelected + blockBoundsMinY),
                            minZ = (float) (zSelected + blockBoundsMinZ),
                            maxX = (float) (xSelected + blockBoundsMaxX),
                            maxY = (float) (ySelected + blockBoundsMaxY),
                            maxZ = (float) (zSelected + blockBoundsMaxZ);
                    this.setBlockBounds(
                            minX / m,
                            minY / m,
                            minZ / m,
                            maxX / m,
                            maxY / m,
                            maxZ / m);
                }
            }
        }
    }

    @Override
    public boolean canCollideCheck(IBlockState state, boolean rightClicked) {
        isFluid = rightClicked;
        return super.canCollideCheck(state,
                                     rightClicked);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        if (ConfigurationLib.littleBlocksClip) {
            return super.getCollisionBoundingBox(
                    world,
                    pos,
                    state);
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB axisalignedbb, List list, Entity entity) {
        TileEntity tileentity = world.getTileEntity(pos);
        if (tileentity != null && tileentity instanceof TileEntityLittleChunk) {
            TileEntityLittleChunk tile = (TileEntityLittleChunk) tileentity;

            float m = ConfigurationLib.littleBlocksSize;

            AxisAlignedBB bb = AxisAlignedBB.fromBounds(axisalignedbb.minX
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
                                block.addCollisionBoxesToList(
                                        (World) tile.getLittleWorld(),
                                        new BlockPos(
                                                (pos.getX() << 3) + xx,
                                                (pos.getY() << 3) + yy,
                                                (pos.getZ() << 3) + zz),
                                        tile.getBlockState(xx, yy, zz),
                                        bb,
                                        bbs,
                                        entity);
                            }
                        }
                    }
                }
            }
            for (AxisAlignedBB aabb : bbs) {
                /*aabb.setBounds(aabb.minX / m,
                               aabb.minY / m,
                               aabb.minZ / m,
                               aabb.maxX / m,
                               aabb.maxY / m,
                               aabb.maxZ / m);
                list.add(aabb);*/
                // TODO :: setBounds for Collision
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 player, Vec3 view) {
        TileEntityLittleChunk tile = (TileEntityLittleChunk) world.getTileEntity(pos);

        if (tile == null) {
            return null;
        }

        List<MovingObjectPosition> returns = new ArrayList<MovingObjectPosition>();

        returns = CollisionRayTrace.rayTraceLittleBlocks(this,
                                                         player,
                                                         view,
                                                         pos,
                                                         returns,
                                                         tile,
                                                         isFluid);
        player = player.addVector(-pos.getX(),
                                  -pos.getY(),
                                  -pos.getZ());
        view = view.addVector(-pos.getX(),
                              -pos.getY(),
                              -pos.getZ());

        returns = CollisionRayTrace.collisionRayTracer(this,
                                                       world,
                                                       player,
                                                       view,
                                                       pos,
                                                       returns);
        if (!returns.isEmpty()) {
            MovingObjectPosition min = null;
            double distMin = 0;
            boolean isLiquid = false;
            for (MovingObjectPosition ret : returns) {
                double dist = (double) ret.hitVec.squareDistanceTo(player);

                Block retBlock = tile.getBlock(ret.getBlockPos());
                if (retBlock != Blocks.air) {
                    isLiquid = retBlock instanceof IFluidBlock;
                    if (isLiquid && isFluid) {
                        isLiquid = !(retBlock instanceof BlockStaticLiquid && tile.getBlockMetadata(ret.getBlockPos()) == 0);
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
                side = (byte) min.sideHit.getIndex();
                xSelected = min.getBlockPos().getX();
                ySelected = min.getBlockPos().getY();
                zSelected = min.getBlockPos().getZ();
                if (isFluid) {
                    littleBlock = tile.getBlock(xSelected,
                                                ySelected,
                                                zSelected);
                }
                boolean rayTraced = false;
                if (littleBlock != Blocks.air) {
                    if (littleBlock != null) {
                        if (!(littleBlock.hasTileEntity(
                                tile.getBlockState(
                                        xSelected,
                                        ySelected,
                                        zSelected))
                                && tile.getTileEntity(
                                    xSelected,
                                    ySelected,
                                    zSelected) == null)) {

                            try {
                                littleBlock.collisionRayTrace(
                                        (World) tile.getLittleWorld(),
                                        BlockUtil.getLittleChunkPos(pos).add(
                                                xSelected,
                                                ySelected,
                                                zSelected
                                        ),
                                        player,
                                        view);
                            } catch (ClassCastException e) {
                                FMLCommonHandler.instance().getFMLLogger().warn(e.getLocalizedMessage());
                            }
                        }
                    }
                }
                this.setBlockBoundsBasedOnSelection(world, pos);
                // TODO :: createVectorHelper
                return new MovingObjectPosition(hitVec = new Vec3(
                        (min.hitVec.xCoord * 8) % 1,
                        (min.hitVec.yCoord * 8) % 1,
                        (min.hitVec.zCoord * 8) % 1).addVector(
                        pos.getX(),
                        pos.getY(),
                        pos.getZ()),
                        min.sideHit,
                        pos
                /** ((Vec3) min.hitVec).addVector(x, y, z) **/
                );
            }
        }
        xSelected = -10;
        ySelected = -10;
        zSelected = -10;
        side = -1;
        hitVec = null;
        this.setBlockBoundsBasedOnSelection(world, pos);

        return null;
    }

    public MovingObjectPosition rayTraceBound(AxisAlignedBB bound, BlockPos pos, Vec3 player, Vec3 view) {
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
        return new MovingObjectPosition(tracedBound, EnumFacing.getFront(side), pos);
    }

    private boolean isVecInsideYZBounds(AxisAlignedBB bound, Vec3 Vec3) {
        if (Vec3 == null) {
            return false;
        } else {
            return Vec3.yCoord >= bound.minY && Vec3.yCoord <= bound.maxY
                   && Vec3.zCoord >= bound.minZ && Vec3.zCoord <= bound.maxZ;
        }
    }

    private boolean isVecInsideXZBounds(AxisAlignedBB bound, Vec3 vec) {
        if (vec == null) {
            return false;
        } else {
            return vec.xCoord >= bound.minX && vec.xCoord <= bound.maxX
                   && vec.zCoord >= bound.minZ && vec.zCoord <= bound.maxZ;
        }
    }

    private boolean isVecInsideXYBounds(AxisAlignedBB bound, Vec3 vec) {
        if (vec == null) {
            return false;
        } else {
            return vec.xCoord >= bound.minX && vec.xCoord <= bound.maxX
                   && vec.yCoord >= bound.minY && vec.yCoord <= bound.maxY;
        }
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess iblockaccess, BlockPos pos, IBlockState state, EnumFacing side) {
        int weakPower = super.isProvidingWeakPower(
                iblockaccess,
                pos,
                state,
                side);
        if (weakPower > 0) {
            return weakPower;
        } else {
            TileEntityLittleChunk tile = (TileEntityLittleChunk) iblockaccess.getTileEntity(pos);
            if (tile != null) {
                int maX = tile.size, maY = tile.size, maZ = tile.size;
                int startX = 0, startY = 0, startZ = 0;

                switch (side.getIndex()) {
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
                                IBlockState littleState = tile.getBlockState(xx, yy, zz);
                                if (littleBlock != null) {
                                    return littleBlock.isProvidingWeakPower(tile.getLittleWorld(),
                                                                            new BlockPos(
                                                                                    (pos.getX() << 3) + xx,
                                                                                    (pos.getY() << 3) + yy,
                                                                                    (pos.getZ() << 3) + zz),
                                                                            littleState,
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
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
        super.onNeighborBlockChange(
                world,
                pos,
                state,
                block);
        if (updateEveryone) {
            TileEntityLittleChunk tile = (TileEntityLittleChunk) world.getTileEntity(pos);
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
                                        littleBlock.onNeighborBlockChange(
                                                (World) tile.getLittleWorld(),
                                                new BlockPos(
                                                        (pos.getX() << 3) + xx,
                                                        (pos.getY() << 3) + yy,
                                                        (pos.getZ() << 3) + zz
                                                ),
                                                state,
                                                block);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                super.onNeighborBlockChange(
                        world,
                        pos,
                        state,
                        block);
            }
        }
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, EffectRenderer effectRenderer) {
        return LittleBlockDiggingFX.doBlockDestroyEffects(world,
                                                          pos,
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
    public int getLightValue(IBlockAccess world, BlockPos pos) {
        TileEntityLittleChunk tile = (TileEntityLittleChunk) world.getTileEntity(pos);
//        try {
//            if (tile != null) return tile.getLightlevel();
//        } catch (ClassCastException e) {
//            FMLCommonHandler.instance().getFMLLogger().warn(e.getLocalizedMessage());
//            return super.getLightValue(world,
//                                       x,
//                                       y,
//                                       z);
//        }
        return super.getLightValue(world, pos);
    }
}
