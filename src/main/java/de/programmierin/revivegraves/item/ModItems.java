package de.programmierin.revivegraves.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import de.programmierin.revivegraves.ReviveGraves;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;


public class ModItems {
    public static final Item REVIVE_TOKEN = registerItem("revive_token", new Item(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ReviveGraves.MOD_ID,"revive_token")))));


    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, Identifier.of(ReviveGraves.MOD_ID, name), item);
    }

    public static void registerModItems(){
        ReviveGraves.LOGGER.info("Registering mod items for " + ReviveGraves.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {

        });
    }
}