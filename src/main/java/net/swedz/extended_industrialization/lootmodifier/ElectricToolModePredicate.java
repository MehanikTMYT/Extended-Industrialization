package net.swedz.extended_industrialization.lootmodifier;

import net.minecraft.world.item.ItemStack;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.swedz.extended_industrialization.item.ElectricToolItem;
import net.swedz.tesseract.neoforge.helper.CodecHelper;

public record ElectricToolModePredicate(ElectricToolItem.Mode mode) implements DataComponentPredicate
{
	public static final Codec<ElectricToolModePredicate> CODEC = CodecHelper.forLowercaseEnum(ElectricToolItem.Mode.class)
			.xmap(ElectricToolModePredicate::new, ElectricToolModePredicate::mode);
	
	@Override
	public boolean matches(DataComponentGetter components)
	{
		return components instanceof ItemStack stack &&
			   ElectricToolItem.getMode(stack) == mode;
	}
}
