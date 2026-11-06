package net.swedz.extended_industrialization.machines.component.beacon;

import aztech.modern_industrialization.machines.MachineComponent;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.swedz.tesseract.api.Assert;

import java.util.Collections;
import java.util.List;

public final class BeaconBeamComponent implements MachineComponent.ClientOnly
{
	private List<Section> beamSections = List.of();
	
	private boolean needsRematch = true;
	
	public BeaconBeamComponent()
	{
	}
	
	public boolean needsRematch()
	{
		return needsRematch;
	}
	
	public void requestRematch()
	{
		needsRematch = true;
	}
	
	public List<Section> getBeamSections()
	{
		return beamSections;
	}
	
	public boolean isActive()
	{
		return !beamSections.isEmpty();
	}
	
	private List<Section> calculateBeamSections(Level level, BlockPos controllerPos)
	{
		List<Section> sections = Lists.newArrayList();
		
		int maxHeight = level.getHeight(Heightmap.Types.WORLD_SURFACE, controllerPos.getX(), controllerPos.getZ());
		
		var currentSection = new Section(DyeColor.WHITE.getTextureDiffuseColor());
		sections.add(currentSection);
		
		var pos = controllerPos.above();
		for(int y = controllerPos.getY() + 1; y <= maxHeight; y++)
		{
			var state = level.getBlockState(pos);
			var colorMultiplier = state.getBeaconColorMultiplier(level, pos, controllerPos);
			
			if(colorMultiplier != null)
			{
				if(sections.size() <= 1)
				{
					currentSection = new Section(colorMultiplier);
					sections.add(currentSection);
				}
				else if(colorMultiplier == currentSection.color())
				{
					currentSection.increaseHeight();
				}
				else
				{
					currentSection = new Section(ARGB.average(currentSection.color(), colorMultiplier));
					sections.add(currentSection);
				}
			}
			else
			{
				if(state.getLightDampening() >= 15 && !state.is(Blocks.BEDROCK))
				{
					return List.of();
				}
				
				currentSection.increaseHeight();
			}
			
			pos = pos.above();
		}
		
		return Collections.unmodifiableList(sections);
	}
	
	public void rematch(Level level, BlockPos controllerPos)
	{
		Assert.that(!level.isClientSide());
		
		beamSections = this.calculateBeamSections(level, controllerPos);
		needsRematch = false;
	}
	
	@Override
	public void writeClientNbt(net.minecraft.world.level.storage.ValueOutput tag)
	{
		var list = tag.list("beam_sections", com.mojang.serialization.Codec.INT_STREAM);
		for(var section : beamSections) list.add(java.util.stream.IntStream.of(section.color(), section.height()));
	}
	
	@Override
	public void readClientNbt(net.minecraft.world.level.storage.ValueInput tag)
	{
		List<Section> sections = Lists.newArrayList();
		for(var entry : tag.listOrEmpty("beam_sections", com.mojang.serialization.Codec.INT_STREAM))
		{
			var values = entry.toArray();
			if(values.length == 2) sections.add(new Section(values[0], values[1]));
		}
		beamSections = Collections.unmodifiableList(sections);
	}
	
	public static final class Section
	{
		private final int color;
		
		private int height;
		
		private Section(int color, int height)
		{
			this.color = color;
			this.height = height;
		}
		
		private Section(int color)
		{
			this(color, 1);
		}
		
		public int color()
		{
			return color;
		}
		
		public int height()
		{
			return height;
		}
		
		private void increaseHeight()
		{
			height++;
		}
	}
}
