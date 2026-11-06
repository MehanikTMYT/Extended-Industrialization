package net.swedz.extended_industrialization;

import aztech.modern_industrialization.client.machines.MachineBlockEntityRenderer;
import aztech.modern_industrialization.client.machines.multiblocks.MultiblockMachineBER;
import aztech.modern_industrialization.client.machines.multiblocks.MultiblockTankBER;
import aztech.modern_industrialization.machines.MachineBlock;
import aztech.modern_industrialization.machines.blockentities.multiblocks.LargeTankMultiblockBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.event.RegisterTextureAtlasesEvent;
import net.neoforged.neoforge.client.fluid.FluidTintSources;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.swedz.extended_industrialization.client.NanoGravichestplateHudRenderer;
import net.swedz.extended_industrialization.client.ber.beacon.ElectricBeaconBlockEntityRenderer;
import net.swedz.extended_industrialization.client.ber.chainer.MachineChainerHighlightRenderer;
import net.swedz.extended_industrialization.client.ber.tesla.TeslaPartMultiblockRenderer;
import net.swedz.extended_industrialization.client.ber.tesla.TeslaPartSingleBlockRenderer;
import net.swedz.extended_industrialization.client.ber.tesla.behavior.TeslaBehavior;
import net.swedz.extended_industrialization.client.entity.NanoSaberSweepEntityRenderer;
import net.swedz.extended_industrialization.client.model.tesla.TeslaParticleGeneratorModel;
import net.swedz.extended_industrialization.client.model.tesla.TeslaUnbakedModel;
import net.swedz.extended_industrialization.item.ElectricToolItem;
import net.swedz.extended_industrialization.item.SteamChainsawItem;
import net.swedz.extended_industrialization.item.tooltip.SteamChainsawTooltipComponent;
import net.swedz.extended_industrialization.machines.blockentity.MachineChainerMachineBlockEntity;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.beacon.ElectricBeaconMachineBlockEntity;
import net.swedz.extended_industrialization.network.packet.ModifyElectricToolSpeedPacket;
import org.lwjgl.glfw.GLFW;
import net.swedz.tesseract.api.Assert;
import net.swedz.tesseract.config.ConfigManager;
import net.swedz.tesseract.neoforge.config.ModConfigFileAccess;
import net.swedz.tesseract.neoforge.item.DynamicDyedItem;
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder;

@Mod(value = EI.ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = EI.ID, value = Dist.CLIENT)
public final class EIClient
{
	public EIClient(IEventBus bus, ModContainer container)
	{
		setupConfig(bus, container);
		
		EIKeybinds.init(bus);
		
		NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, (event) ->
		{
			for(var keybind : EIKeybinds.Registry.getMappings())
			{
				while(keybind.holder().get().consumeClick())
				{
					keybind.action().run();
				}
			}
		});
		
		NeoForge.EVENT_BUS.addListener(InputEvent.MouseScrollingEvent.class, (event) ->
		{
			if(InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_ALT))
			{
				var player = Minecraft.getInstance().player;
				var stack = player.getItemInHand(InteractionHand.MAIN_HAND);
				if(stack.getItem() instanceof ElectricToolItem tool && tool.getToolType().hasAdjustableSpeed())
				{
					boolean increase = event.getScrollDeltaY() > 0;
					int speed = ElectricToolItem.getToolSpeed(stack);
					if(increase ? speed < ElectricToolItem.SPEED_MAX : speed > ElectricToolItem.SPEED_MIN)
					{
						new ModifyElectricToolSpeedPacket(increase).sendToServer();
					}
					event.setCanceled(true);
				}
			}
		});
	}
	
	private static EIClientConfig CONFIG;
	
	public static EIClientConfig config()
	{
		Assert.notNull(CONFIG, "Config not yet loaded");
		return CONFIG;
	}
	
	private static void setupConfig(IEventBus bus, ModContainer container)
	{
		var file = new ModConfigFileAccess(container, ModConfig.Type.CLIENT);
		var instance = new ConfigManager(file)
				.build(EIClientConfig.class)
				.load();
		bus.addListener(FMLCommonSetupEvent.class, (event) -> instance.load(false));
		CONFIG = instance.config();
	}
	
	@SubscribeEvent
	private static void registerItemProperties(FMLClientSetupEvent event)
	{
		event.enqueueWork(() -> EIItems.values().forEach(ItemHolder::triggerClientRegistrationListener));
	}
	
	@SubscribeEvent
	private static void registerModelLoaders(ModelEvent.RegisterLoaders event)
	{
		event.register(TeslaUnbakedModel.LOADER_ID, TeslaUnbakedModel.LOADER);
		event.register(TeslaParticleGeneratorModel.LOADER_ID, TeslaParticleGeneratorModel.LOADER);
	}
	
	@SubscribeEvent
	private static void registerBlockEntityRenderers(FMLClientSetupEvent event)
	{
		for(var blockDef : EIBlocks.Registry.BLOCKS.getEntries())
		{
			if(blockDef.get() instanceof MachineBlock machine)
			{
				try
				{
					var blockEntity = machine.getBlockEntityInstance();
					var type = blockEntity.getType();
					
					BlockEntityRendererProvider provider = switch (blockEntity)
					{
						case ElectricBeaconMachineBlockEntity __ -> (BlockEntityRendererProvider) ElectricBeaconBlockEntityRenderer::new;
						case MachineChainerMachineBlockEntity __ -> (BlockEntityRendererProvider) MachineChainerHighlightRenderer::new;
						case TeslaBehavior __ -> switch (blockEntity)
						{
							case MultiblockMachineBlockEntity ___ -> (BlockEntityRendererProvider) TeslaPartMultiblockRenderer::new;
							default -> (BlockEntityRendererProvider) TeslaPartSingleBlockRenderer::new;
						};
						case LargeTankMultiblockBlockEntity __ -> (BlockEntityRendererProvider) MultiblockTankBER::new;
						case MultiblockMachineBlockEntity __ -> (BlockEntityRendererProvider) MultiblockMachineBER::new;
						default -> (BlockEntityRendererProvider) MachineBlockEntityRenderer::new;
					};
					BlockEntityRenderers.register(cast(type), provider);
				}
				catch (Exception ex)
				{
					throw new RuntimeException("Failed to register BER for %s".formatted(blockDef.getId()), ex);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends BlockEntity> BlockEntityType<T> cast(BlockEntityType<?> type)
	{
		return (BlockEntityType<T>) type;
	}
	
	@SubscribeEvent
	private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		event.registerEntityRenderer(EIEntities.NANO_SABER_SWEEP.get(), (EntityRendererProvider<NanoSaberSweepEntity, ?>) NanoSaberSweepEntityRenderer::new);
	}
	
	@SubscribeEvent
	private static void registerClientTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event)
	{
		event.register(SteamChainsawItem.SteamChainsawTooltipData.class, SteamChainsawTooltipComponent::new);
	}
	
	@SubscribeEvent
	private static void registerGuiLayers(RegisterGuiLayersEvent event)
	{
		event.registerAbove(VanillaGuiLayers.SELECTED_ITEM_NAME, EI.id("nano_gravichestplate_activation_status"), NanoGravichestplateHudRenderer::render);
	}
	
	@SubscribeEvent
	private static void registerStandaloneModels(ModelEvent.RegisterStandalone event)
	{
		EIStandaloneModels.register(event);
	}
	
	@SubscribeEvent
	private static void registerAtlases(RegisterTextureAtlasesEvent event)
	{
		event.register(new AtlasManager.AtlasConfig(EIClientSheets.NANO_SABER_SWEEP, EI.id("nano_saber_sweep"), false));
	}
	
	@SubscribeEvent
	private static void registerFluidModels(RegisterFluidModelsEvent event)
	{
		var still = new Material(Identifier.withDefaultNamespace("block/water_still"));
		var flowing = new Material(Identifier.withDefaultNamespace("block/water_flow"));
		for(var holder : EIFluids.values())
		{
			var model = new FluidModel.Unbaked(still, flowing, null, FluidTintSources.constant(((MIFluidHolder) holder).properties().color()), null);
			event.register(model, holder::asFluid);
		}
	}
}
