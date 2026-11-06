import sys,zipfile
from pathlib import Path
jars=[Path('build/neoForm/neoFormJoined26.1.2-1/steps/patchUserDev/outputs.jar')]+list(Path('/root/.gradle/caches/modules-2/files-2.1/net.neoforged/neoforge/26.1.2.126').glob('*/*sources.jar'))
for name in sys.argv[1:]:
 for jar in jars:
  with zipfile.ZipFile(jar) as z:
   ns=[n for n in z.namelist() if n.endswith('/'+name+'.java')]
   for n in ns:print(z.read(n).decode())
