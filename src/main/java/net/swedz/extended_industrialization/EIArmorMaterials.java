package net.swedz.extended_industrialization;

import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.equipment.*;
import net.minecraft.world.item.DyeColor;
import net.neoforged.bus.api.IEventBus;
import net.swedz.tesseract.neoforge.helper.ColorHelper;
import java.util.Map;

public final class EIArmorMaterials {
    public static final ArmorMaterial NANO = create("nano", false);
    public static final ArmorMaterial NANO_GRAVICHESTPLATE = create("nano_gravichestplate", false);
    public static final ArmorMaterial NANO_QUANTUM = create("nano_quantum", true);
    public static final int NANO_COLOR = ColorHelper.getVibrantColor(DyeColor.LIME);
    public static final int NANO_GRAVICHESTPLATE_COLOR = ColorHelper.getVibrantColor(DyeColor.LIGHT_BLUE);
    public static void init(IEventBus bus) { } // Equipment materials are values, no longer a registry.
    private static ArmorMaterial create(String name, boolean quantum) {
        return new ArmorMaterial(1,
            quantum ? Map.of() : Map.of(ArmorType.HELMET, 6, ArmorType.CHESTPLATE, 6, ArmorType.LEGGINGS, 6, ArmorType.BOOTS, 6),
            0, SoundEvents.ARMOR_EQUIP_NETHERITE, quantum ? 0 : 3, 0.1f,
            TagKey.create(Registries.ITEM, EI.id("repairs_nano_armor")),
            ResourceKey.create(EquipmentAssets.ROOT_ID, EI.id(name)));
    }
}
