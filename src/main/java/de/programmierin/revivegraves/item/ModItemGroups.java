package de.programmierin.revivegraves.item;

import de.programmierin.revivegraves.ReviveGraves;
import de.programmierin.revivegraves.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup REVIVEGRAVES_ITEMS_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(ReviveGraves.MOD_ID, "revivegraves_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.REVIVE_TOKEN))
                    .displayName(Text.translatable("ReviveGraves"))
                    .entries((displayContext, entries) -> {

                        entries.add(ModItems.REVIVE_TOKEN);
                        entries.add(ModBlocks.GRAVESTONE);

                    }).build());

    public static void registerItemGroups() {
        ReviveGraves.LOGGER.info("Registering Item Groups for " + ReviveGraves.MOD_ID);
    }
}
