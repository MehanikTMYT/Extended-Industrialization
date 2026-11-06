from pathlib import Path
import re
root=Path('src/main/java/net/swedz/extended_industrialization')
for p in root.rglob('*.java'):
 s=p.read_text()
 for a,b in [('addItemInput','itemIn'),('addItemOutput','itemOut'),('addFluidInput','fluidIn'),('addFluidOutput','fluidOut')]:s=s.replace('.'+a+'(','.'+b+'(')
 if '/datagen/server/provider/recipes/' in str(p):
  s=s.replace('GatherDataEvent.Client event','net.minecraft.core.HolderLookup.Provider registries, RecipeOutput output').replace('super(event);','super(registries, output);').replace('super(event.getGenerator().getPackOutput(), event.getLookupProvider());','super(registries, output);')
  if p.name=='RecipesServerDatagenProvider.java':
   s=s.replace('protected static void add','protected void add').replace('new MIMachineRecipeBuilder(recipeType','new MIMachineRecipeBuilder(registries, recipeType')
   s=s.replace('\t@Override\n\tpublic String getName()', '\tpublic final void generate() { buildRecipes(output); }\n\t@Override protected final void buildRecipes() { buildRecipes(output); }\n\tprotected abstract void buildRecipes(RecipeOutput output);\n\tpublic String getName()')
  else:
   s=s.replace('private static void add','private void add')
  s=s.replace('new MIMachineRecipeBuilder(', 'new MIMachineRecipeBuilder(registries, ')
  s=s.replace('registries, registries, ', 'registries, ')
  s=s.replace('createRecipesFor(material, output)', 'createRecipesFor(material, registries, output)')
 if p.name=='MIMachineMaterialRecipeContext.java':
  s=s.replace('Material material, RecipeOutput recipes)', 'Material material, net.minecraft.core.HolderLookup.Provider registries, RecipeOutput recipes)').replace('super(registry, material, recipes)', 'super(registry, material, registries, recipes)').replace('new MIMachineRecipeBuilder(type','new MIMachineRecipeBuilder(registries, type')
 p.write_text(s)
p=root/'datagen/server/DatagenDelegatorServer.java';s=p.read_text()
classes=re.findall(r'add\(event, (\w+Recipes\w*)::new\);',s)
s=re.sub(r'\t\tadd\(event, \w+Recipes\w*::new\);\n','',s)
code='''        event.getGenerator().addProvider(true, new net.minecraft.data.recipes.RecipeProvider.Runner(event.getGenerator().getPackOutput(), event.getLookupProvider()) {
            @Override protected net.minecraft.data.recipes.RecipeProvider createRecipeProvider(HolderLookup.Provider registries, net.minecraft.data.recipes.RecipeOutput output) {
                return new net.minecraft.data.recipes.RecipeProvider(registries, output) {
                    @Override protected void buildRecipes() {
'''+''.join('                        new '+c+'(registries, output).generate();\n' for c in classes)+'''                    }
                };
            }
            @Override public String getName() { return "Extended Industrialization recipes"; }
        });
'''
s=s.replace('add(event, BlockTagDatagenProvider::new);',code+'\n\t\tadd(event, BlockTagDatagenProvider::new);');p.write_text(s)
