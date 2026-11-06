package net.swedz.extended_industrialization.client.nanosuit.decorations;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.client.nanosuit.NanoArmorLayer;
import net.swedz.extended_industrialization.item.nanosuit.NanoSuitArmorItem;
import net.swedz.extended_industrialization.item.nanosuit.decoration.NanoSuitDecoration;

public final class WingNanoSuitDecorationModel extends NanoSuitDecorationModel
{
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(EI.id("nano_armor"), "wing");
	
	private final ModelPart body;

	public static LayerDefinition createLayer()
	{
		float wingRotation = 0.2f;
		
		var deformation = new CubeDeformation(0);
		var mesh = createMesh(deformation, 0);
		var root = mesh.getRoot();
		
		var body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
		
		body.addOrReplaceChild(
				"left_wing",
				CubeListBuilder.create()
						.texOffs(0, 0)
						.mirror()
						.addBox(
								0, -12, 5,
								32, 32, 0
						),
				PartPose.rotation(0, -wingRotation, 0)
		);
		
		body.addOrReplaceChild(
				"right_wing",
				CubeListBuilder.create()
						.texOffs(0, 0)
						.mirror()
						.addBox(
								0, -12, 5,
								-32, 32, 0
						),
				PartPose.rotation(0, wingRotation, 0)
		);
		
		return LayerDefinition.create(mesh, 64, 32);
	}
	
	public WingNanoSuitDecorationModel(ModelPart root)
	{
		super(root, NanoSuitDecoration.WING);
		this.body = root.getChild("body");
	}
	
	private static Identifier getTexture(int layerIndex)
	{
		return EI.id("textures/models/armor/nano_decorations/wing%s.png".formatted(layerIndex == 1 ? "_overlay" : ""));
	}
	
	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight,
					   NanoSuitArmorItem item, ItemStack stack, EquipmentSlot slot)
	{
		for(int layerIndex = 0; layerIndex <= 1; layerIndex++)
		{
			var texture = getTexture(layerIndex);
			var renderType = NanoArmorLayer.decorationRenderType(layerIndex, item, texture, false);
			submitNodeCollector.submitModelPart(this.body, poseStack, renderType, packedLight, OverlayTexture.NO_OVERLAY, null, -1, null);
		}
	}
}
