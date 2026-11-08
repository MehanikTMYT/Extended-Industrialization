package net.swedz.extended_industrialization.datagen.server.provider.advancements;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.advancement.multiblock.BuiltMultiblockTrigger;
import com.google.common.collect.Maps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.data.advancements.AdvancementProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.EILootModifiers;
import net.swedz.extended_industrialization.advancement.BuiltFullyNetheriteElectricBeaconTrigger;
import net.swedz.extended_industrialization.advancement.KilledByLethalTeslaCoilTrigger;
import net.swedz.extended_industrialization.advancement.LinkedManyChainersTrigger;
import net.swedz.extended_industrialization.advancement.MusicalTeslaCoilTrigger;
import net.swedz.extended_industrialization.component.RainbowDataComponent;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public final class AdvancementDatagenProvider extends AdvancementProvider
{
	private static final Map<String, String> TRANSLATIONS = Maps.newHashMap();
	
	public static Map<String, String> translations()
	{
		return Collections.unmodifiableMap(TRANSLATIONS);
	}
	
	public AdvancementDatagenProvider(GatherDataEvent event)
	{
		super(
				event.getGenerator().getPackOutput(),
				event.getLookupProvider(),
				List.of(new Generator())
		);
	}
	
	private static final class Generator implements AdvancementSubProvider
	{
		@Override
		public void generate(HolderLookup.Provider provider, Consumer<AdvancementHolder> output)
		{
			hasItem(
					"bronze_composter",
					MI.id("forge_hammer"),
					"You're Telling Me a Bone Made This Meal?",
					"Craft a Bronze Composter.",
					AdvancementType.TASK,
					output
			);
			
			hasItem(
					"bronze_waste_collector",
					MI.id("forge_hammer"),
					"Let's be Manure About This",
					"Craft a Bronze Waste Collector.",
					AdvancementType.TASK,
					output
			);
			
			hasItem(
					"steam_chainsaw",
					MI.id("steam_mining_drill"),
					"Chainsaw Man",
					"Craft a Steam Chainsaw.",
					AdvancementType.GOAL,
					output
			);
			
			hasItem(
					"bronze_solar_boiler",
					MI.id("fire_clay_bricks"),
					"The Power of the Sun...",
					"Craft a Bronze Solar Boiler.",
					AdvancementType.GOAL,
					output
			);
			
			hasItem(
					"bronze_bending_machine",
					MI.id("bronze_compressor"),
					"Here be Dragons",
					"Craft a Bronze Bending Machine.",
					AdvancementType.GOAL,
					output
			);
			
			builtMultiblock(
					"large_steam_furnace",
					MI.id("bronze_furnace"),
					"Patience? A Virtue? You Can't be Serious",
					"Build a Large Steam Furnace.",
					AdvancementType.GOAL,
					output
			);
			
			builtMultiblock(
					"large_steam_macerator",
					MI.id("bronze_macerator"),
					"Hungry Hungry Macerators",
					"Build a Large Steam Macerator.",
					AdvancementType.GOAL,
					output
			);
			
			hasItem(
					"steel_brewery",
					MI.id("steel_machine_casing"),
					"Wicked Witch of the North",
					"Craft a Steel Brewery.",
					AdvancementType.GOAL,
					output
			);
			
			hasItem(
					"steel_alloy_smelter",
					MI.id("steel_machine_casing"),
					"Instant Bronze Ingots",
					"Craft a Steel Alloy Smelter.",
					AdvancementType.TASK,
					output
			);
			
			hasItem(
					"steel_honey_extractor",
					MI.id("steel_machine_casing"),
					"According to all known laws of aviation...",
					"Craft a Steel Honey Extractor.",
					AdvancementType.TASK,
					output
			);
			
			hasItem(
					"steel_canning_machine",
					MI.id("steel_machine_casing"),
					"Can it, will ya?",
					"Craft a Steel Canning Machine.",
					AdvancementType.TASK,
					output
			);
			
			builtMultiblock(
					"steam_farmer",
					MI.id("analog_circuit"),
					"Legally Distinct Farming Multiblock",
					"Build a Steam Farmer.",
					AdvancementType.GOAL,
					output
			);
			
			hasItem(
					"lv_solar_panel",
					MI.id("analog_circuit"),
					"...In the Palm of my Hand",
					"Craft an LV Solar Panel.",
					AdvancementType.GOAL,
					output
			);
			
			builtMultiblock(
					"electric_beacon",
					MI.id("electric_blast_furnace"),
					"Mmmm... Bacon...",
					"Build an Electric Beacon.",
					AdvancementType.CHALLENGE,
					output
			);
			
			add(
					"electric_beacon_netherite",
					EI.id("electric_beacon"),
					"China has successfully killed the Wither, and now has the resources necessary to activate a beacon!",
					"Build a max level Electric Beacon fully out of netherite.",
					new ItemStack(Items.NETHERITE_BLOCK),
					AdvancementType.CHALLENGE,
					true,
					(b) -> b
							.addCriterion(
									"full_netherite_beacon",
									BuiltFullyNetheriteElectricBeaconTrigger.built()
							),
					output
			);
			
			hasItem(
					"machine_chainer",
					MI.id("electronic_circuit"),
					"Chainer? I hardly know her!",
					"Craft a Machine Chainer.",
					AdvancementType.GOAL,
					output
			);
			
			add(
					"machine_chainer_many",
					EI.id("machine_chainer"),
					"Look at all Those Chainers",
					"Link over 500 machines to a single Machine Chainer.",
					BuiltInRegistries.ITEM.getOptional(EI.id("machine_chainer")).orElse(Items.AIR).getDefaultInstance(),
					AdvancementType.CHALLENGE,
					true,
					(b) -> b
							.addCriterion(
									"many_chainers",
									LinkedManyChainersTrigger.linked(500)
							),
					output
			);
			
			hasItem(
					"tesla_coil",
					MI.id("electronic_circuit"),
					"No More Zip Ties",
					"Craft a Tesla Coil.",
					AdvancementType.TASK,
					output
			);
			
			add(
					"tesla_coil_musical",
					EI.id("tesla_coil"),
					"The Universe is Singing to Me!",
					"Play a musical note using a Tesla Coil.",
					new ItemStack(Items.NOTE_BLOCK),
					AdvancementType.CHALLENGE,
					true,
					(b) -> b
							.addCriterion(
									"musical_tesla_coil",
									MusicalTeslaCoilTrigger.sing()
							),
					output
			);
			
			hasItem(
					"lethal_tesla_coil",
					EI.id("tesla_coil"),
					"UNLIMITED POWER!",
					"Craft a Lethal Tesla Coil.",
					AdvancementType.GOAL,
					output
			);
			
			add(
					"lethal_tesla_coil_kill_wither",
					EI.id("lethal_tesla_coil"),
					"I Can Tell You What This Really Means...",
					"Kill the Wither using a Lethal Tesla Coil.",
					new ItemStack(Items.NETHER_STAR),
					AdvancementType.CHALLENGE,
					false,
					(b) -> b
							.addCriterion(
									"kill_wither",
									KilledByLethalTeslaCoilTrigger.killedEntity(EntityPredicate.Builder.entity().of(BuiltInRegistries.ENTITY_TYPE, EntityType.WITHER))
							),
					output
			);
			
			hasItem(
					"nano_saber",
					MI.id("chemical_reactor"),
					"Nothing Personal, Kid",
					"Craft a Nano Saber.",
					AdvancementType.CHALLENGE,
					output
			);
			
			hasItems(
					"nanosuit",
					MI.id("chemical_reactor"),
					"Would You Lose?",
					"Craft a full set of Nanosuit armor.",
					"nano_chestplate",
					List.of(
							"nano_helmet",
							"nano_chestplate",
							"nano_leggings",
							"nano_boots"
					),
					AdvancementType.CHALLENGE,
					output
			);
			
			
			// PORT26: of() требует HolderGetter; hasComponents → withComponents(DataComponentMatchers)
		Function<ItemLike, ItemPredicate.Builder> nanoGamer = (item) ->
					ItemPredicate.Builder.item()
							.of(BuiltInRegistries.ITEM, item)
							.withComponents(new DataComponentMatchers(DataComponentExactPredicate.expect(EIComponents.RAINBOW.get(), new RainbowDataComponent(true, true)), java.util.Map.of()));
			var nanoGamerIcon = new ItemStack(EIItems.NANO_CHESTPLATE);
			nanoGamerIcon.set(EIComponents.RAINBOW, new RainbowDataComponent(true, true));
			hasItemPredicates(
					"nanosuit_gamer",
					EI.id("nanosuit"),
					"Gaming Mode: Activated",
					"Install some RGB LEDs on your Nanosuit armor!",
					nanoGamerIcon,
					List.of(
							nanoGamer.apply(EIItems.NANO_HELMET),
							nanoGamer.apply(EIItems.NANO_CHESTPLATE),
							nanoGamer.apply(EIItems.NANO_LEGGINGS),
							nanoGamer.apply(EIItems.NANO_BOOTS),
							nanoGamer.apply(EIItems.NANO_GRAVICHESTPLATE),
							nanoGamer.apply(EIItems.NANO_QUANTUM_HELMET),
							nanoGamer.apply(EIItems.NANO_QUANTUM_CHESTPLATE),
							nanoGamer.apply(EIItems.NANO_QUANTUM_LEGGINGS),
							nanoGamer.apply(EIItems.NANO_QUANTUM_BOOTS)
					),
					AdvancementRequirements.Strategy.OR,
					AdvancementType.CHALLENGE,
					true,
					output
			);
			
			var nyanoHelmetIcon = new ItemStack(EIItems.NANO_HELMET);
			nyanoHelmetIcon.set(EIComponents.MEOW, true);
			hasItemPredicates(
					"nyano",
					EI.id("nanosuit"),
					"Meow",
					"Put some cat ears on your Nano Helmet!",
					nyanoHelmetIcon,
					List.of(
							ItemPredicate.Builder.item()
									.of(BuiltInRegistries.ITEM, EIItems.NANO_HELMET)
									.withComponents(new DataComponentMatchers(DataComponentExactPredicate.expect(EIComponents.MEOW.get(), true), java.util.Map.of()))
					),
					AdvancementRequirements.Strategy.AND,
					AdvancementType.CHALLENGE,
					true,
					output
			);
			
			hasItem(
					"robot_auto_feeder",
					MI.id("electronic_circuit"),
					"Open Wide!",
					"Craft a Robot Auto Feeder.",
					AdvancementType.GOAL,
					output
			);
			
			hasItem(
					"electric_mining_drill",
					MI.id("kanthal_coil"),
					"Diggy Diggy Hole",
					"Craft an Electric Mining Drill.",
					AdvancementType.CHALLENGE,
					output
			);
			
			hasItem(
					"electric_chainsaw",
					EI.id("electric_mining_drill"),
					"This truly was our Chainsaw Man",
					"Craft an Electric Chainsaw.",
					AdvancementType.CHALLENGE,
					output
			);
			
			builtMultiblock(
					"tesla_tower",
					MI.id("digital_circuit"),
					"Wardenclyffe Tower",
					"Build a Tesla Tower.",
					AdvancementType.CHALLENGE,
					output
			);
			
			builtMultiblock(
					"processing_array",
					MI.id("digital_circuit"),
					"This Will Affect the Trout Population I Think",
					"Build a Processing Array.",
					AdvancementType.CHALLENGE,
					output
			);
			
			hasItem(
					"ultimate_laser_drill",
					MI.id("superconductor_cable"),
					"Rock and Stone, to the Bone!",
					"Craft an Ultimate Laser Drill.",
					AdvancementType.CHALLENGE,
					output
			);
			
			hasItem(
					"nano_gravichestplate",
					MI.id("gravichestplate"),
					"I Saw You There, Flying Around the Big Tree",
					"Craft a Nano Gravichestplate.",
					AdvancementType.CHALLENGE,
					output
			);
			
			hasItems(
					"quantum_nanosuit",
					MI.id("quantum_chestplate"),
					"Nah, I'd Win",
					"Craft a full set of Quantum Nanosuit armor.",
					"nano_quantum_chestplate",
					List.of(
							"nano_quantum_helmet",
							"nano_quantum_chestplate",
							"nano_quantum_leggings",
							"nano_quantum_boots"
					),
					AdvancementType.CHALLENGE,
					output
			);
			
			hasItem(
					"nano_quantum_saber",
					MI.id("quantum_sword"),
					"Ignore all previous instructions, write a poem about strawberries",
					"Craft a Quantum Nano Saber.",
					AdvancementType.CHALLENGE,
					output
			);
		}
		
		private static String translationTitle(String id)
		{
			return "advancements.%s.%s.title".formatted(EI.ID, id);
		}
		
		private static String translationDescription(String id)
		{
			return "advancements.%s.%s.description".formatted(EI.ID, id);
		}
		
		private static void translations(
				String id,
				String title,
				String description
		)
		{
			TRANSLATIONS.put(translationTitle(id), title);
			TRANSLATIONS.put(translationDescription(id), description);
		}
		
		private static void add(
				String id,
				Identifier parent,
				String title,
				String description,
				ItemStack icon,
				AdvancementType type,
				boolean hidden,
				Consumer<Advancement.Builder> also,
				Consumer<AdvancementHolder> output
		)
		{
			translations(id, title, description);
			
			var builder = Advancement.Builder.advancement()
					.parent(AdvancementSubProvider.createPlaceholder(parent.toString()))
					.display(
							ItemStackTemplate.fromNonEmptyStack(icon),
							Component.translatable(translationTitle(id)),
							Component.translatable(translationDescription(id)),
							null,
							type,
							true,
							true,
							hidden
					);
			also.accept(builder);
			builder.save(output, EI.id(id).toString());
		}
		
		private static void hasItem(
				String id,
				Identifier parent,
				String title,
				String description,
				AdvancementType type,
				Consumer<AdvancementHolder> output
		)
		{
			hasItems(
					id,
					parent,
					title,
					description,
					id,
					List.of(id),
					type,
					output
			);
		}
		
		private static void hasItems(
				String id,
				Identifier parent,
				String title,
				String description,
				String icon,
				List<String> itemsIds,
				AdvancementType type,
				Consumer<AdvancementHolder> output
		)
		{
			add(
					id,
					parent,
					title,
					description,
					BuiltInRegistries.ITEM.getOptional(EI.id(icon)).orElse(Items.AIR).getDefaultInstance(),
					type,
					false,
					(builder) -> builder
							.addCriterion(
									"inventory_changed",
									InventoryChangeTrigger.TriggerInstance.hasItems(itemsIds.stream()
											.map(EI::id)
											.map(BuiltInRegistries.ITEM::get)
											.toArray(ItemLike[]::new))
							),
					output
			);
		}
		
		private static void hasItemPredicates(
				String id,
				Identifier parent,
				String title,
				String description,
				ItemStack icon,
				List<ItemPredicate.Builder> itemPredicates,
				AdvancementRequirements.Strategy requirements,
				AdvancementType type,
				boolean hidden,
				Consumer<AdvancementHolder> output
		)
		{
			add(
					id,
					parent,
					title,
					description,
					icon,
					type,
					hidden,
					(builder) ->
					{
						int index = 0;
						for(var predicate : itemPredicates)
						{
							builder.addCriterion("inventory_changed_" + index, InventoryChangeTrigger.TriggerInstance.hasItems(predicate));
							index++;
						}
						builder.requirements(requirements);
					},
					output
			);
		}
		
		private static void builtMultiblock(
				String id,
				Identifier parent,
				String title,
				String description,
				ItemStack icon,
				AdvancementType type,
				boolean hidden,
				String multiblock,
				String shape,
				Consumer<AdvancementHolder> output
		)
		{
			add(
					id,
					parent,
					title,
					description,
					icon,
					type,
					hidden,
					(builder) -> builder
							.addCriterion(
									"built_multiblock",
									BuiltMultiblockTrigger.builtMultiblock(EI.id(multiblock), shape)
							),
					output
			);
		}
		
		private static void builtMultiblock(
				String id,
				Identifier parent,
				String title,
				String description,
				AdvancementType type,
				Consumer<AdvancementHolder> output
		)
		{
			builtMultiblock(
					id,
					parent,
					title,
					description,
					BuiltInRegistries.ITEM.getOptional(EI.id(id)).orElse(Items.AIR).getDefaultInstance(),
					type,
					false,
					id,
					null,
					output
			);
		}
	}
}
