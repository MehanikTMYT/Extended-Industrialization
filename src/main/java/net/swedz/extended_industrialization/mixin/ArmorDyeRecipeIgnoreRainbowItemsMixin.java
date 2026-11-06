package net.swedz.extended_industrialization.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.DyeRecipe;
import net.swedz.extended_industrialization.EITags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DyeRecipe.class)
public class ArmorDyeRecipeIgnoreRainbowItemsMixin
{
	@WrapOperation(
			method = "matches(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/world/level/Level;)Z",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/crafting/Ingredient;test(Lnet/minecraft/world/item/ItemStack;)Z"
			)
	)
	private boolean isDyeable(net.minecraft.world.item.crafting.Ingredient ingredient, ItemStack stack,
							  Operation<Boolean> original)
	{
		return original.call(ingredient, stack) &&
			   !stack.is(EITags.Items.RAINBOW_DYEABLE);
	}
}
