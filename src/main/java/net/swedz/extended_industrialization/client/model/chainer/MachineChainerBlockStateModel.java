package net.swedz.extended_industrialization.client.model.chainer;

import aztech.modern_industrialization.client.machines.models.CasingModels;
import aztech.modern_industrialization.machines.models.MachineCasing;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Based on {@link aztech.modern_industrialization.client.machines.models.MachineBlockStateModel}
 */
public final class MachineChainerBlockStateModel implements DynamicBlockStateModel
{
	public static float Z_OFFSET = 5e-4f; // Cannot be lower due to Embeddium compact vertex format
	
	private final MachineCasing     casing;
	private final Material.Baked[]  overlays;
	private final MachineModelClientData defaultData;
	
	MachineChainerBlockStateModel(MachineCasing casing, Material.Baked[] overlays)
	{
		this.casing = casing;
		this.overlays = overlays;
		this.defaultData = new MachineModelClientData(casing, Direction.NORTH);
	}
	
	public Material.Baked[] getSprites()
	{
		return overlays;
	}
	
	public static Material.Baked getSprite(Material.Baked[] sprites, Direction side, Direction facingDirection, boolean isActive)
	{
		int spriteId = 1;
		if(side == facingDirection)
		{
			spriteId = 0;
		}
		else if(side == facingDirection.getOpposite())
		{
			spriteId = 1;
		}
		else if(side.getAxis().isHorizontal())
		{
			if(facingDirection.getAxis().isVertical())
			{
				spriteId = facingDirection == Direction.UP ? 2 : 3;
			}
			else
			{
				spriteId = switch (facingDirection)
				{
					case NORTH -> side == Direction.WEST ? 4 : 5;
					case SOUTH -> side == Direction.WEST ? 5 : 4;
					case WEST -> side == Direction.NORTH ? 5 : 4;
					case EAST -> side == Direction.NORTH ? 4 : 5;
					default -> throw new IllegalStateException("Unexpected value: " + facingDirection);
				};
			}
		}
		else if(side.getAxis().isVertical() && facingDirection.getAxis().isHorizontal())
		{
			spriteId = switch (facingDirection)
			{
				case NORTH -> side == Direction.UP ? 2 : 3;
				case SOUTH -> side == Direction.UP ? 3 : 2;
				case WEST -> 4;
				case EAST -> 5;
				default -> throw new IllegalStateException("Unexpected value: " + facingDirection);
			};
		}
		return sprites[spriteId];
	}
	
	public QuadCollection assembleOverlayQuads(MachineModelClientData data)
	{
		QuadCollection.Builder quads = new QuadCollection.Builder();
		
		for(var side : Direction.values())
		{
			Material.Baked sprite = getSprite(overlays, side, data.frontDirection, false);
			if(sprite != null)
			{
				quads.addCulledFace(side, ModelHelper.bakeSprite(side, sprite.sprite(), -Z_OFFSET));
			}
		}
		
		if(data.outputDirection != null)
		{
			var outputIndexes = MachineChainerOverlaysJson.OUTPUT_SPRITE_INDEXES;
			quads.addCulledFace(data.outputDirection, ModelHelper.bakeSprite(data.outputDirection, overlays[outputIndexes[0]].sprite(), -3 * Z_OFFSET));
			if(data.itemAutoExtract)
			{
				quads.addCulledFace(data.outputDirection, ModelHelper.bakeSprite(data.outputDirection, overlays[outputIndexes[1]].sprite(), -3 * Z_OFFSET));
			}
			if(data.fluidAutoExtract)
			{
				quads.addCulledFace(data.outputDirection, ModelHelper.bakeSprite(data.outputDirection, overlays[outputIndexes[2]].sprite(), -3 * Z_OFFSET));
			}
		}
		
		return quads.build();
	}
	
	public BakedQuad getActiveOverlayQuad(MachineModelClientData data, Direction direction)
	{
		var facing = data.frontDirection;
		if(facing == null)
		{
			return null;
		}
		Material.Baked sprite = getSprite(overlays, direction, facing, true);
		if(sprite == null)
		{
			return null;
		}
		return ModelHelper.bakeSprite(direction, sprite.sprite(), -2 * Z_OFFSET);
	}
	
	@Override
	public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts)
	{
		var data = level.getModelData(pos).get(MachineModelClientData.KEY);
		if(data == null)
		{
			data = defaultData;
		}
		
		var casing = Objects.requireNonNullElse(data.casing, this.casing);
		CasingModels.getCasingModel(casing).model().collectParts(level, pos, state, random, parts);
		
		var overlayQuads = assembleOverlayQuads(data);
		parts.add(new SimpleModelWrapper(overlayQuads, true, CasingModels.getCasingModel(casing).model().particleMaterial()));
	}
	
	@Override
	public net.minecraft.client.resources.model.sprite.Material.Baked particleMaterial()
	{
		return CasingModels.getCasingModel(casing).model().particleMaterial();
	}
	
	@Override
	@net.minecraft.client.resources.model.geometry.BakedQuad.MaterialFlags
	public int materialFlags()
	{
		return net.minecraft.client.resources.model.geometry.BakedQuad.FLAG_TRANSLUCENT;
	}
}
