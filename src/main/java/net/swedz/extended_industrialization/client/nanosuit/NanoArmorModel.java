package net.swedz.extended_industrialization.client.nanosuit;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.swedz.extended_industrialization.EI;

import java.util.List;

public final class NanoArmorModel extends HumanoidModel<HumanoidRenderState>
{
	public static final ModelLayerLocation INNER_LAYER = new ModelLayerLocation(EI.id("nano_armor"), "inner_armor");
	public static final ModelLayerLocation OUTER_LAYER = new ModelLayerLocation(EI.id("nano_armor"), "outer_armor");
	
	public static LayerDefinition createLayer(CubeDeformation deformation)
	{
		var mesh = createMesh(deformation, 0);
		var root = createHumanoidAlias(mesh);
		root.addOrReplaceChild(
				"head",
				CubeListBuilder.create()
						.texOffs(0, 0)
						.addBox(
								-4, -8, -4,
								8, 8, 8,
								deformation
						),
				PartPose.offset(0, 0, 0)
		);
		root.addOrReplaceChild(
				"hat",
				CubeListBuilder.create()
						.texOffs(32, 0)
						.addBox(
								-4, -8, -4,
								8, 8, 8,
								deformation.extend(0.5f)
						),
				PartPose.offset(0, 0, 0)
		);
		root.addOrReplaceChild(
				"body",
				CubeListBuilder.create()
						.texOffs(16, 16)
						.addBox(
								-4, 0, -2,
								8, 12, 4,
								deformation
						),
				PartPose.offset(0, 0, 0)
		);
		root.addOrReplaceChild(
				"left_arm",
				CubeListBuilder.create()
						.texOffs(40, 16)
						.mirror()
						.addBox(
								-1, -2, -2,
								4, 12, 4,
								deformation
						),
				PartPose.offset(5, 2, 0)
		);
		root.addOrReplaceChild(
				"right_arm",
				CubeListBuilder.create()
						.texOffs(40, 16)
						.addBox(
								-3, -2, -2,
								4, 12, 4,
								deformation
						),
				PartPose.offset(-5, 2, 0)
		);
		root.addOrReplaceChild(
				"left_leg",
				CubeListBuilder.create()
						.texOffs(0, 16)
						.mirror()
						.addBox(
								-2, 0, -2,
								4, 12, 4,
								deformation.extend(-0.1f)
						),
				PartPose.offset(1.9f, 12, 0)
		);
		root.addOrReplaceChild(
				"right_leg",
				CubeListBuilder.create()
						.texOffs(0, 16)
						.addBox(
								-2, 0, -2,
								4, 12, 4,
								deformation.extend(-0.1f)
						),
				PartPose.offset(-1.9f, 12, 0)
		);
		return LayerDefinition.create(mesh, 64, 32);
	}
	
	private static PartDefinition createHumanoidAlias(MeshDefinition mesh)
	{
		var root = mesh.getRoot();
		root.addOrReplaceChild("head", new CubeListBuilder(), PartPose.ZERO);
		root.addOrReplaceChild("hat", new CubeListBuilder(), PartPose.ZERO);
		root.addOrReplaceChild("body", new CubeListBuilder(), PartPose.ZERO);
		root.addOrReplaceChild("left_arm", new CubeListBuilder(), PartPose.ZERO);
		root.addOrReplaceChild("right_arm", new CubeListBuilder(), PartPose.ZERO);
		root.addOrReplaceChild("left_leg", new CubeListBuilder(), PartPose.ZERO);
		root.addOrReplaceChild("right_leg", new CubeListBuilder(), PartPose.ZERO);
		return root;
	}
	
	public EquipmentSlot slot = EquipmentSlot.HEAD;
	
	public NanoArmorModel(ModelPart root)
	{
		super(root);
	}
	
	public List<ModelPart> parts()
	{
		return switch (slot)
		{
			case HEAD -> List.of(this.head, this.hat);
			case CHEST -> List.of(this.body, this.leftArm, this.rightArm);
			case LEGS -> List.of(this.body, this.leftLeg, this.rightLeg);
			case FEET -> List.of(this.leftLeg, this.rightLeg);
			default -> List.of();
		};
	}
	
	/**
	 * Copies the animated pose of the parent humanoid model onto this model.
	 */
	public void copyFrom(HumanoidModel<?> parent)
	{
		this.head.loadPose(parent.head.storePose());
		this.hat.loadPose(parent.hat.storePose());
		this.body.loadPose(parent.body.storePose());
		this.leftArm.loadPose(parent.leftArm.storePose());
		this.rightArm.loadPose(parent.rightArm.storePose());
		this.leftLeg.loadPose(parent.leftLeg.storePose());
		this.rightLeg.loadPose(parent.rightLeg.storePose());
	}
	
	public void setPartVisibility()
	{
		this.setAllVisible(false);
		for(var part : this.parts())
		{
			part.visible = true;
		}
	}

	public void setAllVisible(boolean visible)
	{
		for(var part : root().getAllParts()) part.visible = visible;
	}
}
