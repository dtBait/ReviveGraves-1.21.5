package de.programmierin.revivegraves.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.UUID;

public class GravestoneBlockEntity extends BlockEntity {
    private UUID owner;
    private UUID hologram;
    private final BlockPos pos;

    public GravestoneBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GRAVESTONE, pos, state);
        this.pos = pos;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        markDirty();
    }

    public UUID getOwner() {
        return owner;
    }

    public UUID getHologram() {
        return hologram;
    }

    public void spawnHologram(ServerWorld world) {
        if (owner == null || hologram != null) return;

        ServerPlayerEntity player = world.getServer()
                .getPlayerManager()
                .getPlayer(owner);
        if (player == null) return;
        String name = player.getGameProfile().getName();

        NbtCompound tag = new NbtCompound();
        tag.putString("CustomName",        "{\"text\":\"" + name + "\"}");
        tag.putBoolean("CustomNameVisible", true);
        tag.putBoolean("Invisible",         true);
        tag.putBoolean("NoGravity",         true);
        tag.putBoolean("Invulnerable",      true);
        tag.putBoolean("Small",             true);
        tag.putBoolean("NoBasePlate",       true);
        tag.putBoolean("Marker",            true);

        ArmorStandEntity stand = EntityType.ARMOR_STAND.create(world);
        if (stand == null) return;
        stand.readNbt(tag);
        stand.setInvisible(true);

        BlockState blockState = world.getBlockState(pos);
        Direction facing = blockState.get(HorizontalFacingBlock.FACING);
        double offset = 0.25;
        double dx = -facing.getOffsetX() * offset;
        double dz = -facing.getOffsetZ() * offset;

        double x = pos.getX() + 0.5 + dx;
        double y = pos.getY() + 1.1;
        double z = pos.getZ() + 0.5 + dz;
        stand.refreshPositionAndAngles(x, y, z, 0f, 0f);

        world.spawnEntity(stand);

        this.hologram = stand.getUuid();
        markDirty();
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        if (owner != null) nbt.putUuid("Owner", owner);
        if (hologram != null) nbt.putUuid("Hologram", hologram);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        if (nbt.containsUuid("Owner"))    owner    = nbt.getUuid("Owner");
        if (nbt.containsUuid("Hologram")) hologram = nbt.getUuid("Hologram");
    }
}
