package net.swedz.extended_industrialization.client.nanosuit;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.swedz.extended_industrialization.client.nanosuit.decorations.NanoSuitDecorationModel;
import net.swedz.extended_industrialization.item.nanosuit.NanoSuitArmorItem;

import java.util.List;

import static net.swedz.extended_industrialization.EIClientRenderTypes.*;

public final class NanoArmorLayer<S extends HumanoidRenderState, M extends HumanoidModel<S>> extends RenderLayer<S, M>
{
	public static RenderType renderType(boolean useQuantumShader, boolean stars, boolean cull, int layerIndex, NanoSuitArmorItem item, Identifier texture, boolean isColored)
	{
		if(layerIndex == 1 && useQuantumShader && item.isQuantum())
		{
			return NANO_QUANTUM.apply(new NanoQuantumState(texture, stars, 1, 0.5f, cull));
		}
		else
		{
			return (cull ? ARMOR_CUTOUT_CULL_WITH_TRANSPARENCY : ARMOR_CUTOUT_NO_CULL_WITH_TRANSPARENCY).apply(texture, isColored);
		}
	}
	
	public static RenderType armorRenderType(int layerIndex, NanoSuitArmorItem item, Identifier texture, boolean isColored)
	{
		return renderType(true, true, false, layerIndex, item, texture, isColored);
	}
	
	public static RenderType decorationRenderType(int layerIndex, NanoSuitArmorItem item, Identifier texture, boolean isColored)
	{
		return renderType(true, false, true, layerIndex, item, texture, isColored);
	}
	
	private static net.minecraft.client.resources.model.EquipmentAssetManager EQUIPMENT_ASSETS;
	
	public static void init(net.minecraft.client.resources.model.EquipmentAssetManager equipmentAssets)
	{
		EQUIPMENT_ASSETS = equipmentAssets;
	}
	
	private final NanoArmorModel innerModel;
	private final NanoArmorModel outerModel;
	private final List<NanoSuitDecorationModel> decorations;
	
	public NanoArmorLayer(RenderLayerParent<S, M> renderer, NanoArmorModel innerModel, NanoArmorModel outerModel, List<NanoSuitDecorationModel> decorations)
	{
		super(renderer);
		this.innerModel = innerModel;
		this.outerModel = outerModel;
		this.decorations = decorations;
	}
	
	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, S state, float yRot, float xRot)
	{
		// This order is intentional, the head needs to render after the chestplate or else the chest becomes invisible behind the visor
		this.renderArmorPiece(poseStack, submitNodeCollector, state.chestEquipment, EquipmentSlot.CHEST, packedLight, state, this.outerModel);
		this.renderArmorPiece(poseStack, submitNodeCollector, state.headEquipment, EquipmentSlot.HEAD, packedLight, state, this.outerModel);
		this.renderArmorPiece(poseStack, submitNodeCollector, state.legsEquipment, EquipmentSlot.LEGS, packedLight, state, this.innerModel);
		this.renderArmorPiece(poseStack, submitNodeCollector, state.feetEquipment, EquipmentSlot.FEET, packedLight, state, this.innerModel);
		
		for(var decoration : this.decorations)
		{
			decoration.copyFrom(this.getParentModel());
			decoration.setAllVisible(false);
		}
		this.renderArmorPieceDecorations(poseStack, submitNodeCollector, state.headEquipment, EquipmentSlot.HEAD, packedLight, state);
		this.renderArmorPieceDecorations(poseStack, submitNodeCollector, state.chestEquipment, EquipmentSlot.CHEST, packedLight, state);
		this.renderArmorPieceDecorations(poseStack, submitNodeCollector, state.legsEquipment, EquipmentSlot.LEGS, packedLight, state);
		this.renderArmorPieceDecorations(poseStack, submitNodeCollector, state.feetEquipment, EquipmentSlot.FEET, packedLight, state);
	}
	
	private void renderArmorPiece(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, ItemStack stack, EquipmentSlot slot, int packedLight, S state, NanoArmorModel model)
	{
		if(stack.getItem() instanceof NanoSuitArmorItem item && item.getEquipmentSlot(stack) == slot)
		{
			model.copyFrom(this.getParentModel());
			model.slot = slot;
			model.setPartVisibility();
			
			boolean usesInnerModel = slot == EquipmentSlot.LEGS;
			
			var armorMaterial = item.getMaterial().value();
			var extensions = IClientItemExtensions.of(stack);
			int fallbackColor = extensions.getDefaultDyeColor(stack);
			
			var layerType = usesInnerModel ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS : EquipmentClientInfo.LayerType.HUMANOID;
			
			var clientInfo = EQUIPMENT_ASSETS != null ? EQUIPMENT_ASSETS.get(armorMaterial.assetId()) : net.minecraft.client.resources.model.EquipmentAssetManager.MISSING;
			var layers = clientInfo.layers().get(layerType);
			if(layers == null || layers.isEmpty())
			{
				return;
			}
			for(int layerIndex = layers.size() - 1; layerIndex >= 0; --layerIndex)
			{
				var armorMaterialLayer = layers.get(layerIndex);
				int layerColor = extensions.getArmorLayerTintColor(stack, armorMaterialLayer, layerIndex, fallbackColor);
				if(layerColor != 0)
				{
					boolean isColored = layerColor != -1;
					var texture = ClientHooks.getArmorTexture(stack, layerType, armorMaterialLayer, item.getMaterial().value().assetId().location());
					var renderType = armorRenderType(layerIndex, item, texture, isColored);
					for(var part : model.parts())
					{
						submitNodeCollector.submitModelPart(part, poseStack, renderType, packedLight, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, null, layerColor, null);
					}
				}
			}
		}
	}
	
	private void renderArmorPieceDecorations(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, ItemStack stack, EquipmentSlot slot, int packedLight, S state)
	{
		if(stack.getItem() instanceof NanoSuitArmorItem item && item.getEquipmentSlot(stack) == slot)
		{
			for(NanoSuitDecorationModel decoration : decorations)
			{
				boolean shouldRender = decoration.test(slot, stack);
				decoration.setAllVisible(shouldRender);
				
				if(shouldRender)
				{
					decoration.submit(poseStack, submitNodeCollector, packedLight, item, stack, slot);
				}
			}
		}
	}
}
