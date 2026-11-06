package net.swedz.extended_industrialization.datagen.server.provider.loottable;

import net.minecraft.advancements.criterion.DamageSourcePredicate;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.EntityEquipmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.TagPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.EILootModifiers;
import net.swedz.extended_industrialization.EITags;
import net.swedz.extended_industrialization.item.ElectricToolItem;
import net.swedz.extended_industrialization.lootmodifier.AutoSmeltLootModifier;
import net.swedz.extended_industrialization.lootmodifier.BeheadingLootModifier;
import net.swedz.extended_industrialization.lootmodifier.ElectricToolModePredicate;
import net.swedz.tesseract.neoforge.compat.mi.TesseractMILootConditions;
import net.swedz.tesseract.neoforge.compat.mi.loot.itempredicate.SimpleEnergyItemHasChargePredicate;

public final class LootModifierDatagenProvider extends GlobalLootModifierProvider
{
	public LootModifierDatagenProvider(GatherDataEvent event)
	{
		super(event.getGenerator().getPackOutput(), event.getLookupProvider(), EI.ID);
	}
	
	private ItemPredicate.Builder nanoSaberPredicate(ElectricToolItem.Mode mode, HolderLookup.Provider registries)
	{
		var components = DataComponentMatchers.components()
				.partial(TesseractMILootConditions.ItemSubPredicates.SIMPLE_ENERGY_ITEM_HAS_CHARGE.get(), new SimpleEnergyItemHasChargePredicate());
		if(mode != null)
		{
			components.partial(EILootModifiers.DataComponentPredicates.ELECTRIC_TOOL_MODE.get(), new ElectricToolModePredicate(mode));
		}
		return ItemPredicate.Builder.item()
				.of(registries.lookupOrThrow(Registries.ITEM), EIItems.NANO_SABER.asItem())
				.withComponents(components.build());
	}
	
	private void addBeheading(HolderLookup.Provider registries, EntityType entityType, float chance, Item headItem)
	{
		var typeKey = EntityType.getKey(entityType);
		this.add(
				"nano_saber/beheading/%s/%s".formatted(typeKey.getNamespace(), typeKey.getPath()),
				new BeheadingLootModifier(
						new LootItemCondition[]{
								LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().of(registries.lookupOrThrow(Registries.ENTITY_TYPE), entityType)).build(),
								LootItemRandomChanceCondition.randomChance(chance).build(),
								AnyOfCondition.anyOf(
										DamageSourceCondition.hasDamageSource(new DamageSourcePredicate.Builder()
												.tag(new TagPredicate<>(EITags.DamageTypes.NANO_SABER_SWEEP_BEHEADING, true))),
										LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER, new EntityPredicate.Builder()
												.equipment(new EntityEquipmentPredicate.Builder()
														.mainhand(this.nanoSaberPredicate(ElectricToolItem.Mode.BEHEADING, registries).build()))
												.build()
										).build()
								).build()
						},
						0,
						new ItemStack(headItem)
				)
		);
	}
	
	@Override
	protected void start(HolderLookup.Provider registries)
	{
		this.add(
				"nano_saber/auto_smelting",
				new AutoSmeltLootModifier(
						new LootItemCondition[]{
								AnyOfCondition.anyOf(
										DamageSourceCondition.hasDamageSource(new DamageSourcePredicate.Builder()
												.tag(new TagPredicate<>(EITags.DamageTypes.NANO_SABER_SWEEP, true))),
										LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER, new EntityPredicate.Builder()
												.equipment(new EntityEquipmentPredicate.Builder()
														.mainhand(this.nanoSaberPredicate(null, registries).build()))
												.build()
										).build()
								).build()
						},
						0
				)
		);
		
		// This is the same drop chance as wither skeleton skulls with looting 10
		float headChance = 0.125f;
		this.addBeheading(registries, EntityType.WITHER_SKELETON, headChance, Items.WITHER_SKELETON_SKULL);
		this.addBeheading(registries, EntityType.SKELETON, headChance, Items.SKELETON_SKULL);
		this.addBeheading(registries, EntityType.ZOMBIE, headChance, Items.ZOMBIE_HEAD);
		this.addBeheading(registries, EntityType.CREEPER, headChance, Items.CREEPER_HEAD);
		this.addBeheading(registries, EntityType.PIGLIN, headChance, Items.PIGLIN_HEAD);
	}
}
