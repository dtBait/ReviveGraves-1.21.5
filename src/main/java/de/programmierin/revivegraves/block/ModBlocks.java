package de.programmierin.revivegraves.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import de.programmierin.revivegraves.ReviveGraves;
import de.programmierin.revivegraves.block.custom.GravestoneBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block GRAVESTONE = registerBlock("gravestone",
            new GravestoneBlock(
                    AbstractBlock.Settings.create()
                            .strength(50.0f, 1200.0f)
                            .requiresTool()
                            .nonOpaque()
            )
    );

    private static Block registerBlockWithoutBlockItem(String name, Block block) {
        return Registry.register(Registries.BLOCK,
                Identifier.of(ReviveGraves.MOD_ID, name), block);
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK,
                Identifier.of(ReviveGraves.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM,
                Identifier.of(ReviveGraves.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        ReviveGraves.LOGGER.info("Registering Mod Blocks for " + ReviveGraves.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {

        });
    }
}