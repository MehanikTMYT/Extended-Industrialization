package net.swedz.extended_industrialization.datagen.server.provider.recipes;

import aztech.modern_industrialization.MIFluids;
import aztech.modern_industrialization.MIItem;
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIBlocks;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.EITags;
import net.swedz.extended_industrialization.material.EIMaterials;
import net.swedz.extended_industrialization.port.material.MIMaterials;
import net.swedz.extended_industrialization.port.material.part.MIMaterialParts;
import net.swedz.tesseract.neoforge.compat.mi.recipe.MIMachineRecipeBuilder;
import net.swedz.tesseract.neoforge.compat.vanilla.recipe.ShapedRecipeBuilder;

import java.util.function.Consumer;

public final class CommonRecipesServerDatagenProvider extends RecipesServerDatagenProvider
{
	public CommonRecipesServerDatagenProvider(net.minecraft.core.HolderLookup.Provider registries, RecipeOutput output)
	{
		super(registries, output);
	}
	
	private void addBasicCraftingRecipes(String path, String name, boolean assembler, ItemLike result, int resultCount, Consumer<ShapedRecipeBuilder> crafting, RecipeOutput output)
	{
		ShapedRecipeBuilder shapedRecipeBuilder = new ShapedRecipeBuilder(registries);
		crafting.accept(shapedRecipeBuilder);
		shapedRecipeBuilder.output(result, resultCount);
		shapedRecipeBuilder.offerTo(output, EI.id(path + "/craft/" + name));
		
		if(assembler)
		{
			MIMachineRecipeBuilder.fromShapedToAssembler(shapedRecipeBuilder).offerTo(output, EI.id(path + "/assembler/" + name));
		}
	}
	
	private void components(RecipeOutput output)
	{
		addBasicCraftingRecipes(
				"component", "netherite_rotary_blade", true,
				EIItems.NETHERITE_ROTARY_BLADE, 1,
				(r) -> r
						.define('N', EITags.itemCommon("dusts/netherite"))
						.define('R', EITags.itemCommon("gears/stainless_steel"))
						.pattern(" N ")
						.pattern("NRN")
						.pattern(" N "),
				output
		);
		
		addBasicCraftingRecipes(
				"component", "steel_combine", false,
				EIItems.STEEL_COMBINE, 1,
				(r) -> r
						.define('C', "modern_industrialization:steel_curved_plate")
						.define('B', "modern_industrialization:steel_bolt")
						.define('R', EITags.itemCommon("rods/steel"))
						.pattern("CCC")
						.pattern("BRB")
						.pattern("CCC"),
				output
		);
		addMachineRecipe(
				"component/assembler", "steel_combine", MIMachineRecipeTypes.ASSEMBLER,
				8, 10 * 20,
				(b) -> b
						.itemIn("modern_industrialization:steel_curved_plate", 6)
						.itemIn(EITags.itemCommon("rods/steel"), 1)
						.fluidIn(MIFluids.SOLDERING_ALLOY, 50)
						.itemOut(EIItems.STEEL_COMBINE, 1),
				output
		);
		
		addBasicCraftingRecipes(
				"component", "tin_can", true,
				EIItems.TIN_CAN, 2,
				(r) -> r
						.define('T', "modern_industrialization:tin_curved_plate")
						.pattern("T")
						.pattern("T"),
				output
		);
	}
	
	private void photovoltaicCells(RecipeOutput output)
	{
		addMachineRecipe(
				"photovoltaic_cell", "lv", MIMachineRecipeTypes.PACKER,
				4, 40 * 20,
				(r) -> r
						.itemIn(EITags.itemCommon("glass_panes"), 4)
						.itemIn(EITags.itemCommon("plates/silver"), 2)
						.itemIn("modern_industrialization:rubber_sheet", 2)
						.itemOut(EIItems.LV_PHOTOVOLTAIC_CELL, 1),
				output
		);
		addMachineRecipe(
				"photovoltaic_cell", "lv_synthetic_rubber", MIMachineRecipeTypes.ASSEMBLER,
				8, 40 * 20,
				(r) -> r
						.itemIn(EITags.itemCommon("glass_panes"), 4)
						.itemIn(EITags.itemCommon("plates/silver"), 2)
						.fluidIn(MIFluids.SYNTHETIC_RUBBER, 10)
						.itemOut(EIItems.LV_PHOTOVOLTAIC_CELL, 1),
				output
		);
		addMachineRecipe(
				"photovoltaic_cell", "lv_styrene_rubber", MIMachineRecipeTypes.ASSEMBLER,
				8, 40 * 20,
				(r) -> r
						.itemIn(EITags.itemCommon("glass_panes"), 4)
						.itemIn(EITags.itemCommon("plates/silver"), 2)
						.fluidIn(MIFluids.STYRENE_BUTADIENE_RUBBER, 2)
						.itemOut(EIItems.LV_PHOTOVOLTAIC_CELL, 1),
				output
		);
		
		addMachineRecipe(
				"photovoltaic_cell", "mv", MIMachineRecipeTypes.ASSEMBLER,
				8, 40 * 20,
				(r) -> r
						.itemIn(EITags.itemCommon("glass_panes"), 4)
						.itemIn("modern_industrialization:silicon_n_doped_plate", 1)
						.itemIn(EITags.itemCommon("plates/silver"), 4)
						.itemIn("modern_industrialization:silicon_p_doped_plate", 1)
						.fluidIn(MIFluids.SYNTHETIC_RUBBER, 40)
						.itemOut(EIItems.MV_PHOTOVOLTAIC_CELL, 1),
				output
		);
		addMachineRecipe(
				"photovoltaic_cell", "mv_styrene_rubber", MIMachineRecipeTypes.ASSEMBLER,
				8, 40 * 20,
				(r) -> r
						.itemIn(EITags.itemCommon("glass_panes"), 4)
						.itemIn("modern_industrialization:silicon_n_doped_plate", 1)
						.itemIn(EITags.itemCommon("plates/silver"), 4)
						.itemIn("modern_industrialization:silicon_p_doped_plate", 1)
						.fluidIn(MIFluids.STYRENE_BUTADIENE_RUBBER, 8)
						.itemOut(EIItems.MV_PHOTOVOLTAIC_CELL, 1),
				output
		);
		
		addMachineRecipe(
				"photovoltaic_cell", "hv", MIMachineRecipeTypes.ASSEMBLER,
				8, 40 * 20,
				(r) -> r
						.itemIn(EITags.itemCommon("glass_panes"), 4)
						.itemIn("modern_industrialization:silicon_wafer", 2)
						.itemIn(EITags.itemCommon("plates/silver"), 8)
						.fluidIn(MIFluids.SYNTHETIC_RUBBER, 160)
						.fluidIn(MIFluids.POLYETHYLENE, 25)
						.itemOut(EIItems.HV_PHOTOVOLTAIC_CELL, 1),
				output
		);
		addMachineRecipe(
				"photovoltaic_cell", "hv_styrene_rubber", MIMachineRecipeTypes.ASSEMBLER,
				8, 40 * 20,
				(r) -> r
						.itemIn(EITags.itemCommon("glass_panes"), 4)
						.itemIn("modern_industrialization:silicon_wafer", 2)
						.itemIn(EITags.itemCommon("plates/silver"), 8)
						.fluidIn(MIFluids.STYRENE_BUTADIENE_RUBBER, 32)
						.fluidIn(MIFluids.POLYETHYLENE, 25)
						.itemOut(EIItems.HV_PHOTOVOLTAIC_CELL, 1),
				output
		);
	}
	
	private void nanoSuitPiece(String id, ItemLike baseArmor, int pieces, ItemLike result, Consumer<MIMachineRecipeBuilder> recipeBuilder, RecipeOutput output)
	{
		addMachineRecipe(
				"tool", id, MIMachineRecipeTypes.ASSEMBLER,
				8, 10 * 20,
				(builder) ->
				{
					builder
							.itemIn(baseArmor, 1)
							.itemIn(EITags.itemCommon("plates/carbon"), 4 * pieces)
							.itemIn(MIItem.ELECTRONIC_CIRCUIT, 4)
							.itemIn("modern_industrialization:silicon_battery", 2)
							.itemIn(MIItem.LARGE_MOTOR, 4)
							.fluidIn(MIFluids.POLYETHYLENE, 4000)
							.fluidIn(MIFluids.NYLON, 2000)
							.itemOut(result, 1);
					if(recipeBuilder != null)
					{
						recipeBuilder.accept(builder);
					}
				},
				output
		);
	}
	
	private void nanoSuit(RecipeOutput output)
	{
		nanoSuitPiece(
				"nano_suit_helmet",
				Items.NETHERITE_HELMET, 5, EIItems.NANO_HELMET,
				(r) -> r
						.itemIn(EITags.itemCommon("glass_panes"), 4)
						.itemIn(MIItem.REDSTONE_CONTROL_MODULE, 1),
				output
		);
		nanoSuitPiece(
				"nano_suit_chestplate",
				Items.NETHERITE_CHESTPLATE, 8, EIItems.NANO_CHESTPLATE,
				null,
				output
		);
		nanoSuitPiece(
				"nano_suit_leggings",
				Items.NETHERITE_LEGGINGS, 7, EIItems.NANO_LEGGINGS,
				(r) -> r.itemIn(MIItem.REDSTONE_CONTROL_MODULE, 1),
				output
		);
		nanoSuitPiece(
				"nano_suit_boots",
				Items.NETHERITE_BOOTS, 4, EIItems.NANO_BOOTS,
				(r) -> r.itemIn(MIItem.REDSTONE_CONTROL_MODULE, 1),
				output
		);
		
		addMachineRecipe(
				"tool", "nano_suit_gravichestplate_upgrade", MIMachineRecipeTypes.PACKER,
				32, 10 * 20,
				(b) -> b
						.itemIn(EIItems.NANO_CHESTPLATE, 1)
						.itemIn(MIItem.GRAVICHESTPLATE, 1)
						.itemOut(EIItems.NANO_GRAVICHESTPLATE, 1),
				output
		);
		addMachineRecipe(
				"tool", "nano_suit_gravichestplate_downgrade", MIMachineRecipeTypes.UNPACKER,
				32, 10 * 20,
				(b) -> b
						.itemIn(EIItems.NANO_GRAVICHESTPLATE, 1)
						.itemOut(EIItems.NANO_CHESTPLATE, 1)
						.itemOut(MIItem.GRAVICHESTPLATE, 1),
				output
		);
		
		addMachineRecipe(
				"tool", "nano_suit_helmet_quantum_upgrade", MIMachineRecipeTypes.PACKER,
				1000000, 10 * 20,
				(b) -> b
						.itemIn(EIItems.NANO_HELMET, 1)
						.itemIn(MIItem.QUANTUM_UPGRADE, 1)
						.itemOut(EIItems.NANO_QUANTUM_HELMET, 1),
				output
		);
		addMachineRecipe(
				"tool", "nano_suit_chestplate_quantum_upgrade", MIMachineRecipeTypes.PACKER,
				1000000, 10 * 20,
				(b) -> b
						.itemIn(EIItems.NANO_CHESTPLATE, 1)
						.itemIn(MIItem.QUANTUM_UPGRADE, 1)
						.itemOut(EIItems.NANO_QUANTUM_CHESTPLATE, 1),
				output
		);
		addMachineRecipe(
				"tool", "nano_suit_leggings_quantum_upgrade", MIMachineRecipeTypes.PACKER,
				1000000, 10 * 20,
				(b) -> b
						.itemIn(EIItems.NANO_LEGGINGS, 1)
						.itemIn(MIItem.QUANTUM_UPGRADE, 1)
						.itemOut(EIItems.NANO_QUANTUM_LEGGINGS, 1),
				output
		);
		addMachineRecipe(
				"tool", "nano_suit_boots_quantum_upgrade", MIMachineRecipeTypes.PACKER,
				1000000, 10 * 20,
				(b) -> b
						.itemIn(EIItems.NANO_BOOTS, 1)
						.itemIn(MIItem.QUANTUM_UPGRADE, 1)
						.itemOut(EIItems.NANO_QUANTUM_BOOTS, 1),
				output
		);
	}
	
	private void tesla(RecipeOutput output)
	{
		addBasicCraftingRecipes(
				"tool", "tesla_calibrator", true,
				EIItems.TESLA_CALIBRATOR, 1,
				(r) -> r
						.define('S', MIMaterials.SILVER.get(MIMaterialParts.WIRE))
						.define('T', MIItem.TRANSISTOR)
						.define('G', EITags.itemCommon("glass_panes"))
						.define('D', MIItem.DIODE)
						.define('C', MIItem.ELECTRONIC_CIRCUIT)
						.pattern(" S ")
						.pattern("TGT")
						.pattern("DCD"),
				output
		);
		
		addBasicCraftingRecipes(
				"tool", "tesla_handheld_receiver", true,
				EIItems.TESLA_HANDHELD_RECEIVER, 1,
				(r) -> r
						.define('S', "%s:silver_tesla_top_load".formatted(EI.ID))
						.define('W', EIMaterials.ANNEALED_COPPER.get(EIMaterials.Parts.TESLA_WINDING))
						.define('T', MIItem.TRANSISTOR)
						.define('D', MIItem.DIODE)
						.define('C', MIItem.ELECTRONIC_CIRCUIT)
						.pattern("S  ")
						.pattern("WTT")
						.pattern("CDD"),
				output
		);
		
		addMachineRecipe(
				"upgrade", "tesla_interdimensional_upgrade", MIMachineRecipeTypes.ASSEMBLER,
				32, 60 * 20,
				(r) -> r
						.itemIn("%s:superconductor_tesla_winding".formatted(EI.ID), 8)
						.itemIn(MIItem.HIGHLY_ADVANCED_UPGRADE, 4)
						.itemIn(MIItem.PROCESSING_UNIT, 1)
						.fluidIn(MIFluids.POLYVINYL_CHLORIDE, 1000)
						.itemOut(EIItems.TESLA_INTERDIMENSIONAL_UPGRADE, 1),
				output
		);
		
		new ShapedRecipeBuilder(registries)
				.output("%s:tesla_particle_generator".formatted(EI.ID), 1)
				.define('T', "%s:silver_tesla_top_load".formatted(EI.ID))
				.define('C', MIItem.ELECTRONIC_CIRCUIT)
				.pattern("T")
				.pattern("C")
				.offerTo(output, EI.id("craft/tesla_particle_generator"));
	}
	
	@Override
	protected void buildRecipes(RecipeOutput output, HolderLookup.Provider registries)
	{
		addMachineRecipe(
				"mixer", "mulch", MIMachineRecipeTypes.MIXER,
				2, 5 * 20,
				(r) -> r
						.itemIn(Items.DIRT, 1)
						.itemIn(MIItem.WOOD_PULP, 6)
						.itemOut(EIItems.MULCH, 1),
				output
		);
		
		addMachineRecipe(
				"macerator", "granite_dust", MIMachineRecipeTypes.MACERATOR,
				2, 5 * 20,
				(r) -> r
						.itemIn(Items.GRANITE, 1)
						.itemOut(EIItems.GRANITE_DUST, 4),
				output
		);
		addBasicCraftingRecipes(
				"compacting", "granite_from_dust", false,
				Items.GRANITE, 1,
				(r) -> r
						.define('G', EIItems.GRANITE_DUST)
						.pattern("GG")
						.pattern("GG"),
				output
		);
		addMachineRecipe(
				"packer", "granite_from_dust", MIMachineRecipeTypes.PACKER,
				2, 5 * 20,
				(r) -> r
						.itemIn(EITags.itemCommon("dusts/granite"), 4)
						.itemOut(Items.GRANITE, 1),
				output
		);
		
		addBasicCraftingRecipes(
				"casing", "steel_plated_bricks", true,
				EIBlocks.STEEL_PLATED_BRICKS.get().asItem(), 1,
				(r) -> r
						.define('S', MIMaterials.STEEL.get(MIMaterialParts.PLATE))
						.define('B', "modern_industrialization:fire_clay_bricks")
						.pattern("SSS")
						.pattern("SBS")
						.pattern("SSS"),
				output
		);
		
		components(output);
		
		addMachineRecipe(
				"distillery", "distilled_water_from_water", MIMachineRecipeTypes.DISTILLERY,
				8, 10 * 20,
				(r) -> r
						.fluidIn(Fluids.WATER, 1000)
						.fluidOut(EIFluids.DISTILLED_WATER, 500),
				output
		);
		
		photovoltaicCells(output);
		
		addBasicCraftingRecipes(
				"tool", "steam_chainsaw", false,
				EIItems.STEAM_CHAINSAW, 1,
				(r) -> r
						.define('F', Items.FURNACE)
						.define('D', Items.DIAMOND)
						.define('P', "modern_industrialization:iron_large_plate")
						.define('C', EITags.itemCommon("gears/copper"))
						.define('B', Items.BUCKET)
						.pattern("FDD")
						.pattern("PCD")
						.pattern("BPF"),
				output
		);
		
		addBasicCraftingRecipes(
				"tool", "electric_chainsaw", false,
				EIItems.ELECTRIC_CHAINSAW, 1,
				(r) -> r
						.define('U', MIItem.ADVANCED_UPGRADE)
						.define('R', MIItem.RUBBER_SHEET)
						.define('B', EIItems.NETHERITE_ROTARY_BLADE)
						.define('M', MIItem.ADVANCED_MOTOR)
						.define('C', "modern_industrialization:aluminum_cable")
						.pattern("URB")
						.pattern("MBR")
						.pattern("CMU"),
				output
		);
		
		addBasicCraftingRecipes(
				"tool", "electric_mining_drill", false,
				EIItems.ELECTRIC_MINING_DRILL, 1,
				(r) -> r
						.define('U', MIItem.ADVANCED_UPGRADE)
						.define('R', EIItems.NETHERITE_ROTARY_BLADE)
						.define('D', "modern_industrialization:stainless_steel_drill_head")
						.define('M', MIItem.ADVANCED_MOTOR)
						.define('L', MIItem.LARGE_MOTOR)
						.define('C', "modern_industrialization:aluminum_cable")
						.pattern("URD")
						.pattern("MLR")
						.pattern("CMU"),
				output
		);
		
		addMachineRecipe(
				"tool", "nano_saber", MIMachineRecipeTypes.ASSEMBLER,
				8, 10 * 20,
				(builder) -> builder
						.itemIn(Items.NETHERITE_SWORD, 1)
						.itemIn(EITags.itemCommon("plates/carbon"), 4)
						.itemIn(EITags.itemCommon("plates/stainless_steel"), 12)
						.itemIn(MIItem.ELECTRONIC_CIRCUIT, 4)
						.itemIn("modern_industrialization:silicon_battery", 2)
						.itemIn(Items.NETHER_STAR, 1)
						.itemIn(MIItem.LARGE_MOTOR, 2)
						.itemIn(MIItem.REDSTONE_CONTROL_MODULE, 1)
						.fluidIn(MIFluids.POLYETHYLENE, 4000)
						.fluidIn(MIFluids.CRYOFLUID, 1000)
						.itemOut(EIItems.NANO_SABER, 1),
				output
		);
		addMachineRecipe(
				"tool", "nano_saber_quantum_upgrade", MIMachineRecipeTypes.PACKER,
				1000000, 10 * 20,
				(b) -> b
						.itemIn(EIItems.NANO_SABER, 1)
						.itemIn(MIItem.QUANTUM_UPGRADE, 1)
						.itemOut(EIItems.NANO_QUANTUM_SABER, 1),
				output
		);
		
		addBasicCraftingRecipes(
				"tool", "robot_auto_feeder", false,
				EIItems.ROBOT_AUTO_FEEDER, 1,
				(r) -> r
						.define('M', MIItem.MOTOR)
						.define('B', "modern_industrialization:bronze_curved_plate")
						.define('R', MIItem.ROBOT_ARM)
						.define('b', "modern_industrialization:redstone_battery")
						.define('C', "modern_industrialization:electronic_circuit")
						.pattern("MBM")
						.pattern("BRB")
						.pattern("bCb"),
				output
		);
		
		addBasicCraftingRecipes(
				"tool", "machine_config_card", false,
				EIItems.MACHINE_CONFIG_CARD, 1,
				(r) -> r
						.define('G', EITags.itemCommon("glass_panes"))
						.define('I', "modern_industrialization:inductor")
						.define('C', "modern_industrialization:capacitor")
						.define('M', "modern_industrialization:motor")
						.define('A', "modern_industrialization:analog_circuit_board")
						.pattern("GGG")
						.pattern("ICI")
						.pattern("MAM"),
				output
		);
		
		addBasicCraftingRecipes(
				"tool", "ultimate_drill", false,
				EIItems.ULTIMATE_LASER_DRILL, 1,
				(r) -> r
						.define('D', EIItems.ELECTRIC_MINING_DRILL)
						.define('B', EIItems.NETHERITE_ROTARY_BLADE)
						.define('C', EIItems.ELECTRIC_CHAINSAW)
						.define('c', "modern_industrialization:cooling_cell")
						.define('S', "modern_industrialization:superconductor_coil")
						.define('U', "modern_industrialization:highly_advanced_upgrade")
						.define('s', "modern_industrialization:superconductor_cable")
						.pattern("DBC")
						.pattern("cSc")
						.pattern("UsU"),
				output
		);
		
		nanoSuit(output);
		
		tesla(output);
		
		addMachineRecipe(
				"assembler", "silk_touch_module", MIMachineRecipeTypes.ASSEMBLER,
				8, 10 * 20,
				(r) -> r
						.itemIn("#c:plates/stainless_steel", 8)
						.itemIn(MIItem.INVAR_ROTARY_BLADE, 2)
						.itemIn(MIItem.ROBOT_ARM, 4)
						.fluidIn(MIFluids.POLYETHYLENE, 1000)
						.itemOut(EIItems.SILK_TOUCH_MODULE, 1),
				output
		);
		addMachineRecipe(
				"assembler", "looting_module", MIMachineRecipeTypes.ASSEMBLER,
				8, 10 * 20,
				(r) -> r
						.itemIn("#c:plates/stainless_steel", 8)
						.itemIn(EIItems.NETHERITE_ROTARY_BLADE, 2)
						.itemIn(MIItem.ROBOT_ARM, 4)
						.fluidIn(MIFluids.POLYETHYLENE, 1000)
						.itemOut(EIItems.LOOTING_MODULE, 1),
				output
		);
	}
}
