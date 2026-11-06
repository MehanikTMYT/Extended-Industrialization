package net.swedz.extended_industrialization.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.item.nanosuit.NanoSuitArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public class NanoArmorDontUseVanillaRendererMixin
{
	@Inject(
			method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V",
			at = @At("HEAD"),
			cancellable = true
	)
	private void render(PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
						ItemStack itemStack, EquipmentSlot slot,
						int lightCoords, net.minecraft.client.renderer.entity.state.HumanoidRenderState state,
						CallbackInfo callback)
	{
		if(itemStack.getItem() instanceof NanoSuitArmorItem)
		{
			callback.cancel();
		}
	}
}
