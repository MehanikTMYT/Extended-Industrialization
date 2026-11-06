from pathlib import Path
import re
for p in Path('src/main/java').rglob('*.java'):
 s=p.read_text(); delta=0
 matches=list(re.finditer(r'(writeNbt|readNbt|writeClientNbt|readClientNbt)\(CompoundTag (\w+), HolderLookup.Provider registries(, boolean \w+)?\)\s*\{',s))
 for m in reversed(matches):
  start=m.start(); body_start=m.end(); depth=1;end=body_start
  while depth and end<len(s):
   depth+= (s[end]=='{')-(s[end]=='}');end+=1
  body=s[body_start:end-1];name=m[2];writing=m[1].startswith('write')
  sig=m[1]+'(net.minecraft.world.level.storage.'+('ValueOutput' if writing else 'ValueInput')+' '+name+(m[3] or '')+')\n\t{'
  if not writing:
   for t,default in [('Int','0'),('Long','0L'),('Float','0f'),('Double','0d'),('Boolean','false'),('String','""')]:
    body=re.sub(r'\b'+name+r'\.get'+t+r'\(("[^"]*")\)',name+'.get'+t+r'Or(\1, '+default+')',body)
   body=body.replace('registries',name+'.lookup()')
  s=s[:start]+sig+body+s[end-1:]
 p.write_text(s)
