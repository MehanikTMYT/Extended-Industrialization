package net.swedz.extended_industrialization;

import aztech.modern_industrialization.api.energy.CableTier;
import com.google.common.collect.Sets;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.swedz.tesseract.neoforge.model.ModelGenerators;
import net.swedz.tesseract.neoforge.registry.common.CommonModelBuilders;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.extended_industrialization.component.RainbowDataComponent;
import net.swedz.extended_industrialization.item.ElectricToolItem;
import net.swedz.extended_industrialization.item.PhotovoltaicCellItem;
import net.swedz.extended_industrialization.item.RobotAutoFeederItem;
import net.swedz.extended_industrialization.item.SteamChainsawItem;
import net.swedz.extended_industrialization.item.machineconfig.MachineConfigCardItem;
import net.swedz.extended_industrialization.item.nanosuit.NanoSuitArmorItem;
import net.swedz.extended_industrialization.item.nanosuit.ability.NanoSuitAbility;
import net.swedz.extended_industrialization.item.nanosuit.decoration.NanoSuitDecoration;
import net.swedz.extended_industrialization.item.teslalinkable.TeslaCalibratorItem;
import net.swedz.extended_industrialization.item.teslalinkable.TeslaHandheldReceiverItem;
import net.swedz.tesseract.neoforge.registry.SortOrder;
import net.swedz.tesseract.neoforge.registry.common.CommonModelBuilders;
import net.swedz.tesseract.neoforge.registry.common.CommonRegistrations;
import net.swedz.tesseract.neoforge.registry.common.MICommonCapabitilies;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public final class EIItems
{
	public static final class Registry
	{
		public static final  DeferredRegister.Items ITEMS   = DeferredRegister.createItems(EI.ID);
		private static final Set<ItemHolder>        HOLDERS = Sets.newHashSet();
		
		private static void init(IEventBus bus)
		{
			ITEMS.register(bus);
		}
		
		public static void include(ItemHolder holder)
		{
			HOLDERS.add(holder);
		}
	}
	
	public static void init(IEventBus bus)
	{
		Registry.init(bus);
	}
	
	public static final ItemHolder<SteamChainsawItem> STEAM_CHAINSAW          = create("steam_chainsaw", "Steam Chainsaw", SteamChainsawItem::new, EISortOrder.GEAR).tag(ItemTags.AXES, ItemTags.HOES, ItemTags.SWORDS, Tags.Items.TOOLS_SHEAR).register();
	public static final ItemHolder<ElectricToolItem>  ELECTRIC_CHAINSAW       = create("electric_chainsaw", "Electric Chainsaw", (p) -> new ElectricToolItem(p, ElectricToolItem.Type.CHAINSAW), EISortOrder.GEAR).tag(ItemTags.AXES, ItemTags.HOES, ItemTags.SWORDS, Tags.Items.TOOLS_SHEAR).withCapabilities(MICommonCapabitilies::simpleEnergyItem).register();
	public static final ItemHolder<ElectricToolItem>  ELECTRIC_MINING_DRILL   = create("electric_mining_drill", "Electric Mining Drill", (p) -> new ElectricToolItem(p, ElectricToolItem.Type.DRILL), EISortOrder.GEAR).tag(ItemTags.PICKAXES, ItemTags.SHOVELS).withCapabilities(MICommonCapabitilies::simpleEnergyItem).register();
	public static final ItemHolder<ElectricToolItem>  NANO_SABER              = create("nano_saber", "Nano Saber", (p) -> new ElectricToolItem(p, ElectricToolItem.Type.SABER), EISortOrder.GEAR).tag(ItemTags.CAULDRON_CAN_REMOVE_DYE, EITags.Items.RAINBOW_DYEABLE, ItemTags.SWORDS).withRegistrationListener(CommonRegistrations::cauldronClearDye).withRegistrationListener(RainbowDataComponent::cauldronClearDyeAndRainbow).withCapabilities(MICommonCapabitilies::simpleEnergyItem).register();
	public static final ItemHolder<ElectricToolItem>  NANO_QUANTUM_SABER      = create("nano_quantum_saber", "Quantum Nano Saber", (p) -> new ElectricToolItem(p, ElectricToolItem.Type.SABER, true), EISortOrder.GEAR).tag(ItemTags.CAULDRON_CAN_REMOVE_DYE, EITags.Items.RAINBOW_DYEABLE, ItemTags.SWORDS).withRegistrationListener(CommonRegistrations::cauldronClearDye).withRegistrationListener(RainbowDataComponent::cauldronClearDyeAndRainbow).register();
	public static final ItemHolder<ElectricToolItem>  ULTIMATE_LASER_DRILL    = create("ultimate_laser_drill", "Ultimate Laser Drill", (p) -> new ElectricToolItem(p, ElectricToolItem.Type.ULTIMATE), EISortOrder.GEAR).tag(ItemTags.CAULDRON_CAN_REMOVE_DYE, EITags.Items.RAINBOW_DYEABLE, ItemTags.PICKAXES, ItemTags.SHOVELS, ItemTags.AXES, ItemTags.HOES, ItemTags.SWORDS, Tags.Items.TOOLS_SHEAR).withRegistrationListener(CommonRegistrations::cauldronClearDye).withRegistrationListener(RainbowDataComponent::cauldronClearDyeAndRainbow).withCapabilities(MICommonCapabitilies::simpleEnergyItem).register();
	public static final ItemHolder<NanoSuitArmorItem> NANO_HELMET             = createNanosuitArmor("nano_helmet", "Nano Helmet", ArmorType.HELMET, NanoSuitAbility.NIGHT_VISION).register();
	public static final ItemHolder<NanoSuitArmorItem> NANO_CHESTPLATE         = createNanosuitArmor("nano_chestplate", "Nano Chestplate", ArmorType.CHESTPLATE).register();
	public static final ItemHolder<NanoSuitArmorItem> NANO_GRAVICHESTPLATE    = createNanosuitArmor("nano_gravichestplate", "Nano Gravichestplate", ArmorType.CHESTPLATE, EIArmorMaterials.NANO_GRAVICHESTPLATE, NanoSuitAbility.GRAVICHESTPLATE).register();
	public static final ItemHolder<NanoSuitArmorItem> NANO_LEGGINGS           = createNanosuitArmor("nano_leggings", "Nano Leggings", ArmorType.LEGGINGS, NanoSuitAbility.SPEED).register();
	public static final ItemHolder<NanoSuitArmorItem> NANO_BOOTS              = createNanosuitArmor("nano_boots", "Nano Boots", ArmorType.BOOTS, NanoSuitAbility.STEP).register();
	public static final ItemHolder<NanoSuitArmorItem> NANO_QUANTUM_HELMET     = createNanosuitArmor("nano_quantum_helmet", "Quantum Nano Helmet", ArmorType.HELMET, EIArmorMaterials.NANO_QUANTUM, NanoSuitAbility.NIGHT_VISION).register();
	public static final ItemHolder<NanoSuitArmorItem> NANO_QUANTUM_CHESTPLATE = createNanosuitArmor("nano_quantum_chestplate", "Quantum Nano Chestplate", ArmorType.CHESTPLATE, EIArmorMaterials.NANO_QUANTUM, NanoSuitAbility.QUANTUM_FLIGHT).register();
	public static final ItemHolder<NanoSuitArmorItem> NANO_QUANTUM_LEGGINGS   = createNanosuitArmor("nano_quantum_leggings", "Quantum Nano Leggings", ArmorType.LEGGINGS, EIArmorMaterials.NANO_QUANTUM, NanoSuitAbility.SPEED).register();
	public static final ItemHolder<NanoSuitArmorItem> NANO_QUANTUM_BOOTS      = createNanosuitArmor("nano_quantum_boots", "Quantum Nano Boots", ArmorType.BOOTS, EIArmorMaterials.NANO_QUANTUM, NanoSuitAbility.STEP).register();
	
	public static final ItemHolder<Item>                TIN_CAN           = create("tin_can", "Tin Can", Item::new, EISortOrder.OTHER_GEAR).register();
	public static final ItemHolder<Item>                CANNED_FOOD       = create("canned_food", "Canned Food", Item::new, EISortOrder.OTHER_GEAR).withProperties((p) -> p.food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3f).build(), net.minecraft.world.item.component.Consumables.defaultFood().consumeSeconds(0.8f).build()).usingConvertsTo(TIN_CAN.asItem())).tag(ItemTags.WOLF_FOOD, ItemTags.CAT_FOOD).register();
	public static final ItemHolder<RobotAutoFeederItem> ROBOT_AUTO_FEEDER = create("robot_auto_feeder", "Robot Auto Feeder", RobotAutoFeederItem::new, EISortOrder.OTHER_GEAR).withCapabilities(MICommonCapabitilies::simpleEnergyItem).register();
	
	public static final ItemHolder<MachineConfigCardItem>     MACHINE_CONFIG_CARD     = create("machine_config_card", "Machine Config Card", MachineConfigCardItem::new, EISortOrder.OTHER_GEAR).register();
	public static final ItemHolder<TeslaCalibratorItem>       TESLA_CALIBRATOR        = create("tesla_calibrator", "Tesla Calibrator", TeslaCalibratorItem::new, EISortOrder.OTHER_GEAR).withClientRegistrationListener(EIItems::itemPropertyTeslaNetworkSelected).register();
	public static final ItemHolder<TeslaHandheldReceiverItem> TESLA_HANDHELD_RECEIVER = create("tesla_handheld_receiver", "Tesla Handheld Receiver", TeslaHandheldReceiverItem::new, EISortOrder.OTHER_GEAR).withClientRegistrationListener(EIItems::itemPropertyTeslaNetworkSelected).register();
	
	public static final ItemHolder<Item> TESLA_INTERDIMENSIONAL_UPGRADE = create("tesla_interdimensional_upgrade", "Tesla Interdimensional Upgrade", Item::new, EISortOrder.OTHER_GEAR).withProperties((p) -> p.stacksTo(1).rarity(Rarity.EPIC)).register();
	
	public static final ItemHolder<Item> NETHERITE_ROTARY_BLADE = create("netherite_rotary_blade", "Netherite Rotary Blade", Item::new, EISortOrder.PARTS).register();
	public static final ItemHolder<Item> STEEL_COMBINE          = create("steel_combine", "Steel Combine", Item::new, EISortOrder.PARTS).register();
	
	public static final ItemHolder<Item> MULCH              = create("mulch", "Mulch", Item::new, EISortOrder.RESOURCES).withProperties((p) -> p.food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3f).build(), net.minecraft.world.item.component.Consumables.defaultFood().consumeSeconds(0.8f).build())).register();
	public static final ItemHolder<Item> NETHERITE_DUST     = create("netherite_dust", "Netherite Dust", Item::new, EISortOrder.RESOURCES).tag(EITags.itemCommon("dusts"), EITags.itemCommon("dusts/netherite")).register();
	public static final ItemHolder<Item> CRYSTALLIZED_HONEY = create("crystallized_honey", "Crystallized Honey", Item::new, EISortOrder.RESOURCES).withProperties((p) -> p.food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3f).build(), net.minecraft.world.item.component.Consumables.defaultFood().consumeSeconds(0.8f).build())).register();
	public static final ItemHolder<Item> GRANITE_DUST       = create("granite_dust", "Granite Dust", Item::new, EISortOrder.RESOURCES).tag(EITags.itemCommon("dusts"), EITags.itemCommon("dusts/granite")).register();
	
	public static final ItemHolder<PhotovoltaicCellItem> LV_PHOTOVOLTAIC_CELL = createPhotovoltaicCell("lv", "LV", CableTier.LV, 32, 10 * 60 * 20);
	public static final ItemHolder<PhotovoltaicCellItem> MV_PHOTOVOLTAIC_CELL = createPhotovoltaicCell("mv", "MV", CableTier.MV, 128, 10 * 60 * 20);
	public static final ItemHolder<PhotovoltaicCellItem> HV_PHOTOVOLTAIC_CELL = createPhotovoltaicCell("hv", "HV", CableTier.HV, 512, 10 * 60 * 20);
	
	public static final ItemHolder<Item> SILK_TOUCH_MODULE = createEnchantmentModule("silk_touch", "Silk Touch").register();
	public static final ItemHolder<Item> LOOTING_MODULE    = createEnchantmentModule("looting", "Looting").register();
	
	public static Set<ItemHolder> values()
	{
		return Set.copyOf(Registry.HOLDERS);
	}
	
	public static ItemHolder valueOf(String id)
	{
		return Registry.HOLDERS.stream()
				.filter((holder) -> holder.identifier().id().equals(id))
				.findFirst()
				.orElseThrow();
	}
	
	public static <Type extends Item> ItemHolder<Type> create(String id, String englishName, Function<Item.Properties, Type> creator, SortOrder sortOrder)
	{
		ItemHolder<Type> holder = new ItemHolder<>(EI.id(id), englishName, Registry.ITEMS, creator).sorted(sortOrder);
		Registry.include(holder);
		return holder;
	}
	
	private static int[] getNanoEmissiveLayers(boolean quantum)
	{
		return quantum ? new int[]{0, 2} : new int[]{0};
	}
	
/*
/*
	private static <T extends Item> Function<ItemHolder<T>, Consumer<ModelGenerators>> nanoItemModel(boolean generated, boolean quantum, boolean starry, BiFunction<ItemHolder<T>, ItemModelBuilder, Consumer<ModelGenerators>> extra)
	{
		return (item) -> (generators) ->
		{
			// implementation commented
		};
	}
*/
	
	public static ItemHolder<NanoSuitArmorItem> createNanosuitArmor(String id, String englishName, ArmorType armorType, ArmorMaterial material, Optional<NanoSuitAbility> ability)
	{
		boolean quantum = material == EIArmorMaterials.NANO_QUANTUM;
		TagKey<Item> armorTag = switch (armorType)
		{
			case HELMET -> ItemTags.HEAD_ARMOR;
			case CHESTPLATE -> ItemTags.CHEST_ARMOR;
			case LEGGINGS -> ItemTags.LEG_ARMOR;
			case BOOTS -> ItemTags.FOOT_ARMOR;
			default ->
					throw new IllegalArgumentException("Cannot get tag for armor type %s".formatted(armorType.name()));
		};
		return create(id, englishName, (p) -> new NanoSuitArmorItem(material, armorType, p.rarity(quantum ? Rarity.EPIC : ability.map(NanoSuitAbility::rarity).orElse(Rarity.UNCOMMON)), ability, quantum), EISortOrder.GEAR.and(quantum).and(armorType))
				.tag(armorTag, Tags.Items.ARMORS, ItemTags.TRIMMABLE_ARMOR, ItemTags.CAULDRON_CAN_REMOVE_DYE, EITags.Items.RAINBOW_DYEABLE)
				.withRegistrationListener(CommonRegistrations::cauldronClearDye)
				.withRegistrationListener(RainbowDataComponent::cauldronClearDyeAndRainbow)
				.withCapabilities(MICommonCapabitilies::simpleEnergyItem);
	}
	
	public static ItemHolder<NanoSuitArmorItem> createNanosuitArmor(String id, String englishName, ArmorType armorType, ArmorMaterial material, NanoSuitAbility ability)
	{
		return createNanosuitArmor(id, englishName, armorType, material, Optional.of(ability));
	}
	
	public static ItemHolder<NanoSuitArmorItem> createNanosuitArmor(String id, String englishName, ArmorType armorType, NanoSuitAbility ability)
	{
		return createNanosuitArmor(id, englishName, armorType, EIArmorMaterials.NANO, Optional.of(ability));
	}
	
	public static ItemHolder<NanoSuitArmorItem> createNanosuitArmor(String id, String englishName, ArmorType armorType, ArmorMaterial material)
	{
		return createNanosuitArmor(id, englishName, armorType, material, Optional.empty());
	}
	
	public static ItemHolder<NanoSuitArmorItem> createNanosuitArmor(String id, String englishName, ArmorType armorType)
	{
		return createNanosuitArmor(id, englishName, armorType, EIArmorMaterials.NANO, Optional.empty());
	}
	
	public static ItemHolder<PhotovoltaicCellItem> createPhotovoltaicCell(String id, String name, CableTier tier, int euPerTick, int durationTicks)
	{
		return create("%s_photovoltaic_cell".formatted(id), "%s Photovoltaic Cell".formatted(name), (p) -> new PhotovoltaicCellItem(p, tier, euPerTick, durationTicks, 0), EISortOrder.PARTS)
				.tag(EITags.Items.PHOTOVOLTAIC_CELL)
				
				.register();
	}
	
	public static ItemHolder<Item> createEnchantmentModule(String id, String name)
	{
		return create(id + "_module", name + " Module", Item::new, EISortOrder.OTHER_GEAR)
				.withProperties((p) -> p
						.stacksTo(1)
						.rarity(Rarity.RARE)
						.component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true))
				;
	}
	
	public static void itemPropertyTeslaNetworkSelected(Item item)
	{
		// Legacy item model predicate requires a new item model JSON definition; see PORTING.md.
	}
}
