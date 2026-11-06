package net.swedz.extended_industrialization.machines.component.farmer.harvesting.harvestable;

import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.bus.api.Event;
import net.swedz.extended_industrialization.machines.component.farmer.block.FarmerBlockMap;
import net.swedz.extended_industrialization.machines.component.farmer.block.FarmerTree;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.FarmerListener;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.HarvestingContext;
import net.swedz.extended_industrialization.machines.component.farmer.harvesting.LootTableHarvestableBehavior;
import net.swedz.tesseract.neoforge.event.treegrowth.TreeGrowthEvent;

import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class TreeHarvestable implements LootTableHarvestableBehavior
{
	private final Map<BlockPos, FarmerTree> trees = Maps.newHashMap();
	
	@Override
	public boolean matches(HarvestingContext context)
	{
		return trees.containsKey(context.pos());
	}
	
	@Override
	public boolean isFullyGrown(HarvestingContext context)
	{
		return true;
	}
	
	@Override
	public List<BlockPos> getBlocks(HarvestingContext context)
	{
		return trees.get(context.pos()).blocks();
	}
	
	@Override
	public void harvested(HarvestingContext context)
	{
		trees.remove(context.pos());
	}
	
	private void onTreeGrow(TreeGrowthEvent event, FarmerBlockMap blockMap)
	{
		var origin = event.getPos();
		if(blockMap.containsDirtAt(origin.below()))
		{
			var blocks = event.getPositions();
			blocks.removeIf((pos) -> pos.getY() < origin.getY());
			blocks.sort(Collections.reverseOrder(Comparator.comparingInt(Vec3i::getY)));
			trees.put(origin, new FarmerTree(origin, blocks));
		}
	}
	
	@Override
	public List<FarmerListener<? extends Event>> getListeners(FarmerBlockMap blockMap)
	{
		return List.of(
				new FarmerListener<>(TreeGrowthEvent.class, (event) -> this.onTreeGrow(event, blockMap))
		);
	}
	
	@Override
	public void writeNbt(net.minecraft.world.level.storage.ValueOutput tag)
	{
		var treeList = tag.childrenList("trees");
		for(FarmerTree tree : trees.values())
		{
			var child = treeList.addChild();
			child.putLong("origin", tree.origin().asLong());
			var blocks = child.list("blocks", Codec.LONG);
			for(BlockPos block : tree.blocks())
			{
				blocks.add(block.asLong());
			}
		}
	}
	
	@Override
	public void readNbt(net.minecraft.world.level.storage.ValueInput tag)
	{
		for(var child : tag.childrenListOrEmpty("trees"))
		{
			var origin = child.getLong("origin");
			if(origin.isEmpty())
			{
				continue;
			}
			var base = BlockPos.of(origin.get());
			var blocks = child.listOrEmpty("blocks", Codec.LONG).stream().map(BlockPos::of).toList();
			trees.put(base, new FarmerTree(base, blocks));
		}
	}
}
