package net.swedz.extended_industrialization.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.swedz.extended_industrialization.EICriterionTriggers;

import java.util.Optional;

public final class KilledByLethalTeslaCoilTrigger extends SimpleCriterionTrigger<KilledByLethalTeslaCoilTrigger.TriggerInstance>
{
	public static Criterion<TriggerInstance> killedEntity(EntityPredicate.Builder entityPredicate)
	{
		return EICriterionTriggers.KILLED_BY_LETHAL_TESLA_COIL.get().createCriterion(
				new TriggerInstance(
						Optional.empty(),
						Optional.of(EntityPredicate.wrap(entityPredicate))
				)
		);
	}
	
	@Override
	public Codec<TriggerInstance> codec()
	{
		return TriggerInstance.CODEC;
	}
	
	public void trigger(ServerPlayer player, Entity entity)
	{
		var context = EntityPredicate.createContext(player, entity);
		this.trigger(player, (instance) -> instance.matches(player, context));
	}
	
	public record TriggerInstance(
			Optional<ContextAwarePredicate> player,
			Optional<ContextAwarePredicate> entityPredicate
	) implements SimpleCriterionTrigger.SimpleInstance
	{
		public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> instance
				.group(
						EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
						EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("entity").forGetter(TriggerInstance::entityPredicate)
				)
				.apply(instance, TriggerInstance::new));
		
		// PORT26: CriterionValidator удалён в26.x — используем дефолт SimpleInstance.validate (ValidationContextSource)
		
		public boolean matches(ServerPlayer player, LootContext context)
		{
			return entityPredicate.isEmpty() ||
				   entityPredicate.get().matches(context);
		}
	}
}
