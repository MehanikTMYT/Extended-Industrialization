package net.swedz.extended_industrialization.client.model.chainer;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import java.util.Map;
import aztech.modern_industrialization.client.util.ModelHelper;
import aztech.modern_industrialization.machines.models.MachineCasing;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Based on {@link aztech.modern_industrialization.client.machines.models.MachineUnbakedModel}
 */
public record MachineChainerUnbakedModel(MachineCasing casing, Map<String, Identifier> overlays) implements CustomUnbakedBlockStateModel
{
	public static final MapCodec<MachineChainerUnbakedModel> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
			MachineCasing.CODEC.fieldOf("casing").forGetter(MachineChainerUnbakedModel::casing),
			Codec.unboundedMap(Codec.STRING, Identifier.CODEC).optionalFieldOf("overlays", Map.of()).forGetter(MachineChainerUnbakedModel::overlays)
	).apply(instance, MachineChainerUnbakedModel::new));
	
	@Override
	public MapCodec<? extends CustomUnbakedBlockStateModel> codec()
	{
		return CODEC;
	}
	
	@Override
	public BlockStateModel bake(ModelBaker baker)
	{
		Material.Baked[] sprites = new Material.Baked[9];
		for(int i = 0; i < MachineChainerOverlaysJson.KEYS.length; i++)
		{
			var id = overlays.get(MachineChainerOverlaysJson.KEYS[i]);
			if(id != null)
			{
				sprites[i] = baker.materials().get(new Material(id), () -> "machine chainer unbaked model");
			}
		}
		return new MachineChainerBlockStateModel(casing, sprites);
	}
	
	@Override
	public void resolveDependencies(Resolver resolver)
	{
	}
}
