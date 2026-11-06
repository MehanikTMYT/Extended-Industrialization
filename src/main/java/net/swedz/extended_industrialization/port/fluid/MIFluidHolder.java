package net.swedz.extended_industrialization.port.fluid;

import aztech.modern_industrialization.definition.FluidLike;
import aztech.modern_industrialization.fluid.MIBucketItem;
import aztech.modern_industrialization.fluid.MIFluid;
import aztech.modern_industrialization.fluid.MIFluidBlock;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.swedz.tesseract.neoforge.registry.MIFluidProperties;
import net.swedz.tesseract.neoforge.registry.SortOrder;
import net.swedz.tesseract.neoforge.registry.holder.FluidHolder;

import java.util.function.Consumer;

/**
 * Port of the old tesseract MIFluidHolder for the MI fluid classes of the 26.1.2 fleet:
 * MIFluid now takes an ItemLike bucket, MIFluidBlock takes (properties, color) and the
 * old MIFluidType class no longer exists, so a plain FluidType subclass is used.
 */
public class MIFluidHolder extends FluidHolder<MIFluid, MIFluidHolder.FakedMIFluidType, MIFluidBlock, MIBucketItem> implements FluidLike
{
	private final MIFluidProperties properties;
	
	public MIFluidHolder(Identifier location, String englishName,
						 DeferredRegister<Fluid> registerFluids,
						 DeferredRegister<FluidType> registerFluidTypes,
						 DeferredRegister.Blocks registerBlocks,
						 DeferredRegister.Items registerItems,
						 SortOrder bucketSortOrder,
						 MIFluidProperties properties)
	{
		super(
				location, englishName,
				registerFluids, (holder) -> new MIFluid(
						holder.block()::get,
						holder.bucketItem(),
						() -> holder.registerableFluidType().getOrThrow(),
						properties.color()
				),
				registerFluidTypes, (holder) ->
				{
					FluidType.Properties fluidTypeProperties = FluidType.Properties.create()
							.descriptionId(holder.block().get().getDescriptionId());
					if(properties.isGas())
					{
						fluidTypeProperties.density(-1000);
					}
					return new FakedMIFluidType(location.getNamespace(), holder.block().registerableBlock().get(), fluidTypeProperties);
				},
				registerBlocks, (__, props) -> new MIFluidBlock(props, properties.color()),
				registerItems, (holder, p) -> new MIBucketItem(holder.registerableFluid().getOrThrow(), properties.color(), p),
				bucketSortOrder
		);
		this.properties = properties;
	}
	
	public MIFluidProperties properties()
	{
		return properties;
	}
	
	public MIFluidHolder block(Consumer<net.swedz.tesseract.neoforge.registry.holder.BlockHolder<MIFluidBlock>> consumer)
	{
		super.block(consumer);
		return this;
	}
	
	public MIFluidHolder bucketItem(Consumer<net.swedz.tesseract.neoforge.registry.holder.ItemHolder<MIBucketItem>> consumer)
	{
		super.bucketItem(consumer);
		return this;
	}
	
	@Override
	public MIFluidHolder register()
	{
		super.register();
		return this;
	}
	
	public static final class FakedMIFluidType extends FluidType
	{
		private final String namespace;
		
		private final DeferredBlock<MIFluidBlock> fluidBlock;
		
		public FakedMIFluidType(String namespace, DeferredBlock<MIFluidBlock> fluidBlock, Properties properties)
		{
			super(properties);
			this.namespace = namespace;
			this.fluidBlock = fluidBlock;
		}
		
	}
}
