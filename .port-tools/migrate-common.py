from pathlib import Path
import zipfile,re,collections
z=zipfile.ZipFile('build/neoForm/neoFormJoined26.1.2-1/steps/transformSource/outputs.jar')
names=collections.defaultdict(list)
for n in z.namelist():
 if n.endswith('.java'):names[Path(n).stem].append(n[:-5].replace('/','.'))
for p in Path('src/main/java').rglob('*.java'):
 s=p.read_text()
 def move(m):
  old=m[1]; k=old.rsplit('.',1)[-1]
  if old.replace('.','/')+'.java' not in z.namelist() and len(names[k])==1:
   return 'import '+names[k][0]+';'
  return m[0]
 s=re.sub(r'import (net\.minecraft\.[\w.]+);',move,s)
 s=s.replace('net.minecraft.client.gui.GuiGraphics;', 'net.minecraft.client.gui.GuiGraphicsExtractor;').replace('GuiGraphics ', 'GuiGraphicsExtractor ')
 s=s.replace('FastColor.ARGB32.', 'ARGB.').replace('import net.minecraft.util.FastColor;', 'import net.minecraft.util.ARGB;')
 p.write_text(s)
