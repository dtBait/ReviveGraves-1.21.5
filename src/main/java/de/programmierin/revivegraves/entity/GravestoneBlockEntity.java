package de.programmierin.revivegraves.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
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

        // Baue dein NBT-Tag mit allen gewünschten Flags
        NbtCompound tag = new NbtCompound();
        tag.putString("CustomName",        Text.literal(name).toString());
        tag.putBoolean("CustomNameVisible", true);
        tag.putBoolean("Invisible",         true);
        tag.putBoolean("NoGravity",         true);
        tag.putBoolean("Invulnerable",      true);
        tag.putBoolean("Small",             true);
        tag.putBoolean("NoBasePlate",       true);
        tag.putBoolean("Marker",            true);

        // Erzeuge den ArmorStand mit Initializer, der dein NBT liest
        ArmorStandEntity stand = EntityType.ARMOR_STAND.create(
                world,
                entity -> entity.readNbt(tag),    // hier setzt er Small, NoBasePlate etc.
                pos,
                SpawnReason.TRIGGERED,
                true,   // alignPosition (zentriert auf dem Block)
                false   // invertFacing
        );
        if (stand == null) return;

        // CustomName wurde durch NBT gesetzt, falls du Text.literal benutzt,
        // kannst du es hier alternativ so setzen:
        stand.setCustomName(Text.literal(name));
        stand.setCustomNameVisible(true);

        // Position berechnen wie gehabt
        BlockState bs = world.getBlockState(pos);
        Direction facing = bs.get(HorizontalFacingBlock.FACING);
        double offset = 0.25;
        double dx = -facing.getOffsetX() * offset;
        double dz = -facing.getOffsetZ() * offset;

        double x = pos.getX() + 0.5 + dx;
        double y = pos.getY() + 1.1;
        double z = pos.getZ() + 0.5 + dz;
        stand.refreshPositionAndAngles(x, y, z, 0f, 0f);

        // Jetzt wirklich in die Welt spawnen
        world.spawnEntity(stand);

        // UUID merken und speichern
        this.hologram = stand.getUuid();
        markDirty();
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        if (owner != null) {
            nbt.putString("Owner", owner.toString());
        }
        if (hologram != null) {
            nbt.putString("Hologram", hologram.toString());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        if (nbt.contains("Owner")) {
            // entpacke das Optional<String> mit orElseThrow()
            owner = UUID.fromString(
                    nbt.getString("Owner")
                            .orElseThrow(() -> new IllegalStateException("Owner-UUID fehlt im NBT"))
            );
        }
        if (nbt.contains("Hologram")) {
            hologram = UUID.fromString(
                    nbt.getString("Hologram")
                            .orElseThrow(() -> new IllegalStateException("Hologram-UUID fehlt im NBT"))
            );
        }
    }


}
