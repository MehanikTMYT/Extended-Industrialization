from pathlib import Path
import re
r=Path('src/main/java/net/swedz/extended_industrialization')
for p in r.rglob('*.java'):
 s=p.read_text()
 # Parse balanced method call to retain nested translated component calls.
 for method,target in [('displayClientMessage','sendOverlayMessage')]:
  while '.'+method+'(' in s:
   a=s.index('.'+method+'(');b=a+len(method)+2;depth=1;i=b
   while depth:
    depth+=(s[i]=='(')-(s[i]==')');i+=1
   args=s[b:i-1]; args=re.sub(r',\s*true\s*$', '',args)
   if args.endswith(', false'):target='sendSystemMessage';args=args[:-7]
   else:target='sendOverlayMessage'
   s=s[:a]+'.'+target+'('+args+')'+s[i:]
 s=s.replace('.getIncorrectBlocksForDrops()', '.incorrectBlocksForDrops()').replace('ToolMaterial.DIAMOND.getSpeed()', 'ToolMaterial.DIAMOND.speed()')
 s=s.replace('public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected)', 'public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel level, Entity entity, net.minecraft.world.entity.EquipmentSlot slot)')
 s=s.replace('public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected)', 'public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel level, Entity entity, net.minecraft.world.entity.EquipmentSlot slot)')
 s=s.replace('public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag)', 'public void appendHoverText(ItemStack stack, TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltip, TooltipFlag flag)')
 if '/item/' in str(p):s=s.replace('tooltip.add(', 'tooltip.accept(')
 if p.name in ['SteamChainsawItem.java','ElectricToolItem.java']:
  s=s.replace('public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)', 'public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)')
  # Only return within hurtEnemy.
  s=re.sub(r'(public void hurtEnemy\([^}]+)return true;',r'\1return;',s)
  s=s.replace('spawnShearedDrop(level,', 'spawnShearedDrop((net.minecraft.server.level.ServerLevel)level,')
  s=s.replace('public int getEnchantmentLevel(ItemStack stack, Holder<Enchantment> enchantment)', 'public int getEnchantmentLevel(net.minecraft.world.item.ItemInstance stack, Holder<Enchantment> enchantment)')
  s=s.replace('return this.getAllEnchantments(stack, enchantment.unwrapLookup()).getLevel(enchantment);','return this.getAllEnchantmentsFromInstance(stack, enchantment.unwrapLookup()).getLevel(enchantment);')
  a=s.index('public ItemEnchantments getAllEnchantments(ItemStack stack, HolderLookup.RegistryLookup<Enchantment> lookup)')
  b=s.index('{',a)
  s=s[:b+1]+'''\n        return getAllEnchantmentsFromInstance(stack, lookup);
    }
    private ItemEnchantments getAllEnchantmentsFromInstance(net.minecraft.world.item.ItemInstance stack, HolderLookup.RegistryLookup<Enchantment> lookup) {
'''+s[b+1:]
  s=s.replace('super.getAllEnchantments(stack, lookup)', 'stack.getTagEnchantments()')
 if p.name=='SteamChainsawItem.java':
  s=s.replace('isNotSilkTouch(ItemStack stack)', 'isNotSilkTouch(net.minecraft.world.item.ItemInstance stack)')
  s=s.replace('super(material, properties);','super(material, 0, 0, properties);')
  a=s.index('\t\t\t\tif(burnt.hasCraftingRemainingItem())');b=s.index('\n\t\t\t}',a)
  s=s[:a]+'''                var remainder = burnt.getCraftingRemainder();
                if (remainder != null && getAmount(stack) == 0) {
                    var remaining = remainder.create();
                    setResource(stack, ItemResource.of(remaining));
                    setAmount(stack, remaining.getCount());
                }'''+s[b:]
  s=s.replace('var otherStorage = fillSource.getCapability(Capabilities.FluidHandler.ITEM);', 'var nativeStorage = net.neoforged.neoforge.transfer.access.ItemAccess.forStack(fillSource).getCapability(Capabilities.Fluid.ITEM);\n\t\tvar otherStorage = nativeStorage == null ? null : net.neoforged.neoforge.fluids.capability.IFluidHandler.of(nativeStorage);')
  s=s.replace('player.playNotifySound(SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 1f, 1f);', 'player.playSound(SoundEvents.BUCKET_FILL, 1f, 1f);')
 if p.name=='ElectricToolItem.java':
  s=s.replace('public static Mode getMode(ItemStack stack)', 'public static Mode getMode(net.minecraft.world.item.ItemInstance stack)')
  s=s.replace('public boolean hasEnergy(ItemStack stack)', 'public boolean hasEnergy(net.minecraft.world.item.ItemInstance stack)')
  s=s.replace('player, true, minedState.getFluidState()', 'player, stack, true, minedState.getFluidState()')
  s=s.replace('addCooldown(this, 20)', 'addCooldown(stack, 20)')
  s=s.replace('List.of(Blocks.COBWEB)', 'net.minecraft.core.HolderSet.direct(Blocks.COBWEB.builtInRegistryHolder())')
  s=re.sub(r'(\t\t\t\t\t\t[12])\n(\t\t\t\t\))',r'\1,\n\t\t\t\t\t\ttrue\n\2',s)
  s=s.replace('ItemAbilities.DEFAULT_PICKAXE_ACTIONS', 'Set.<ItemAbility>of()').replace('ItemAbilities.DEFAULT_SWORD_ACTIONS', 'Set.of(ItemAbilities.SWORD_DIG)')
  s=s.replace('public boolean canPerformAction(ItemStack stack, ItemAbility ability)', 'public boolean canPerformAction(net.minecraft.world.item.ItemInstance stack, ItemAbility ability)')
  s=s.replace('public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player)', 'public boolean canAttackBlock(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player)')
 p.write_text(s)
p=r/'item/EIEnergyItem.java';s=p.read_text().replace(' default boolean tryUseEnergy', ' default long getStoredEnergy(net.minecraft.world.item.ItemInstance stack) { return stack.getOrDefault(getEnergyComponent(), 0L); }\n default boolean tryUseEnergy');p.write_text(s)
