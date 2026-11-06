package net.swedz.extended_industrialization.client.nanosuit.decorations;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.item.nanosuit.NanoSuitArmorItem;
import net.swedz.extended_industrialization.item.nanosuit.decoration.NanoSuitDecoration;

public abstract class NanoSuitDecorationModel extends HumanoidModel<HumanoidRenderState>
{
	protected final ModelPart          root;
	protected final NanoSuitDecoration decoration;

	public NanoSuitDecorationModel(ModelPart root, NanoSuitDecoration decoration)
	{
		super(root);
		this.root = root;
		this.decoration = decoration;
	}

	public final boolean test(EquipmentSlot slot, ItemStack stack)
	{
		return stack.getItem() instanceof NanoSuitArmorItem item &&
			   decoration.equipmentSlot() == slot && decoration.isActiveFor(item, stack);
	}

	public abstract void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight,
								NanoSuitArmorItem item, ItemStack stack, EquipmentSlot slot);
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

	public void setAllVisible(boolean visible)
	{
		for(var part : root().getAllParts()) part.visible = visible;
	}
}
