package de.programmierin.revivegraves.block.custom;

import com.mojang.serialization.MapCodec;
import de.programmierin.revivegraves.entity.GravestoneBlockEntity;
import de.programmierin.revivegraves.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class GravestoneBlock extends HorizontalFacingBlock implements BlockEntityProvider {
    public static final MapCodec<GravestoneBlock> CODEC = createCodec(GravestoneBlock::new);
    private static final VoxelShape SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);

    public GravestoneBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, this.getDefaultState().get(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GravestoneBlockEntity(pos, state);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public ActionResult onUse(BlockState state,
                              World world,
                              BlockPos pos,
                              PlayerEntity clicker,
                              BlockHitResult hit) {
        if (world.isClient) return ActionResult.PASS;

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof GravestoneBlockEntity gbe)) {
            return super.onUse(state, world, pos, clicker, hit);
        }

        UUID ownerUuid = gbe.getOwner();
        ServerPlayerEntity dead = ((ServerWorld) world)
                .getServer()
                .getPlayerManager()
                .getPlayer(ownerUuid);

        if (dead != null && dead.interactionManager.getGameMode() == GameMode.SPECTATOR) {

            ItemStack main = clicker.getMainHandStack();
            ItemStack off  = clicker.getOffHandStack();
            if (main.getItem() == ModItems.REVIVE_TOKEN) {
                main.decrement(1);
            } else if (off.getItem() == ModItems.REVIVE_TOKEN) {
                off.decrement(1);
            }

            ServerWorld serverWorld = (ServerWorld) world;
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.5;
            dead.teleport(serverWorld, x, y, z, dead.getYaw(), dead.getPitch());
            dead.changeGameMode(GameMode.SURVIVAL);

            serverWorld.spawnParticles(
                    ParticleTypes.TOTEM_OF_UNDYING,
                    x, y, z,
                    30,
                    0.3, 0.5, 0.3,
                    0.0
            );

            serverWorld.playSound(
                    null,
                    pos,
                    SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
                    SoundCategory.BLOCKS,
                    1f,
                    1f
            );

            UUID holoId = gbe.getHologram();
            if (holoId != null) {
                Entity holo = serverWorld.getEntity(holoId);
                if (holo != null) {

                    holo.discard();
                }
            }

            world.removeBlock(pos, false);
            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, clicker, hit);
    }
}