# Minecraft 26.1.2 port

Target: Minecraft **26.1.2**, NeoForge **26.1.2.126**, **Java 25**.
Branch: `port/26.1.2`. This is a build-verified port snapshot, not a runtime-QA certification.

## Build and dependencies

Use the checked-in wrapper with:

```sh
JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64 ./gradlew compileJava --console=plain
JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64 ./gradlew build --console=plain
```

The build resolves fleet dependencies from `/root/build-farm/build-farm/m2/repository`:

- Modern Industrialization `2.6.0-26.1.2-fleet01`
- Tesseract NeoForge API `1.13.7-26.1.2-fleet.1`
- Applied Energistics `26.1.12-SNAPSHOT` (API at compile time)
- GuideME `26.1.14-beta`
- EMI `1.1.24-semi151a6c88+26.1.2`

The existing build also retains the compile-only Tesseract common API `1.0.2` dependency. Dependency runtime compatibility still needs testing with the fleet jars. JEI and Create are not build/runtime dependencies; recipe-viewer support is EMI-only. Old optional Accessories/Curios version properties and metadata are not evidence of 26.1.2 integration support.

## Verified on 2026-11-06

Starting HEAD was `1cf72964`, with existing uncommitted port work. The earlier reported regression was 374 → 750 → 744 compiler errors. This completion pass measured **231 → 148 → 76 → 20 → 1 → 0** after repairing the invalid `ResourceLocation` substitutions and subsequent API failures.

- `compileJava`: **BUILD SUCCESSFUL**, 28 seconds, zero errors.
- First full `build`: **BUILD SUCCESSFUL**, 1 minute 29 seconds.
- Final full `build`, after source/resource review fixes: **BUILD SUCCESSFUL**, 11 seconds, zero compiler errors (21 deprecation/removal warnings).
- Gradle reports `compileTestJava NO-SOURCE` and `test NO-SOURCE`; there is no automated gameplay test suite here.
- `git diff --check -- src gradle.properties`: passed.

Logs from this pass are in `/root/home/forks/.agent-sandbox/ei-port-completion-20261106/`: `compile-01.log` through `compile-06.log`, `build-01.log`, and `build-final.log`. Earlier `.port-tools` diagnostics were preserved and are not source changes.

Artifact: `build/libs/extended-industrialization-0.0.0-port.1+26.1.2.jar` (2,215,115 bytes).

SHA-256: `14f4478f732933a1213b87321ce3e87651d592001055b063035fed60b448b40e`.

The actual JAR was checked for ZIP integrity, main/client classes, expanded NeoForge metadata, Minecraft version restriction, mixin configuration, and the chainer blockstate resource. Main class file major version is **69** (Java 25). A separate sources JAR is also produced.

## API changes and preserved work

Minecraft/NeoForge transformed sources and `javap` on the actual local jars were used to inspect contracts. Minecraft 26.1.2 uses `Identifier`, `GuiGraphicsExtractor`, geometry `QuadCollection`, `BlockStateModel`, and `Material.Baked`; old `ResourceLocation`/`BakedModel` APIs cannot be imported into this target.

The port keeps the electric-tool outline extraction event, the Steam Chainsaw tooltip height change, the four earlier block-entity fluid/energy integer conversions, and the earlier removal of obsolete block/item model datagen providers. The obsolete, unreferenced chainer `BakedModel` implementation is replaced by the existing dynamic blockstate model, now registered through `RegisterBlockStateModels` with its blockstate JSON migrated. Tesla rendering code that had been replaced with TODO stubs was restored and compiled against the new renderer contracts.

GUI blits now supply a render pipeline and texture dimensions; tooltip callbacks retain MI's `renderTooltip` name while using the extractor's next-frame tooltip API. Loot modifiers use the actual item/equipment predicate builders and the provider's registry context. Component persistence uses `ValueInput`/`ValueOutput`, item codecs, compound child lists, and integer-stream lists. Energy extraction uses transactions. Texture atlases are registered through the NeoForge event instead of a mixin into the removed vanilla atlas map.

## Disabled or unavailable functionality

- The Tesseract GuideME bridge remains disabled: its old `TesseractGuide` extension is not registered. The base GuideME guide and custom multiblock compiler remain registered; interactive guide scenes still need runtime verification.
- Legacy `ItemProperties.register` predicates for nanosuit decoration inventory variants and selected-Tesla-network item icons are not registered. Their new item-model JSON/property equivalents have not been implemented. This affects inventory model switching, not the underlying stored components or world armor decoration renderer.
- `BlockModelsDatagenProvider`, `ItemModelsDatagenProvider`, and Tesseract's removed texture datagen hook are unavailable. Existing generated resources are retained; model-generation callbacks removed from the Tesseract material API are omitted. Casing/winding material parts remain explicitly registered as blocks. Client datagen has not been run or validated.
- The unused old Fabric/Continuity `BakedModel` reflection helper was removed. Fabric renderer-wrapper compatibility is not supplied by this NeoForge port.
- JEI/Create integrations are not included. Optional mod-slot integrations require separate compatible dependencies and runtime validation.

## Remaining runtime QA

No Minecraft client or dedicated server was launched during this completion pass. Successful compilation and packaging do not prove successful mod loading, mixin application, resource reload, recipe decoding, or world compatibility.

Before distribution, test a fresh client and dedicated server with the exact dependency versions above; exercise machine registration and recipe/data loading, EMI categories, guide scenes, fluid/energy transfer, transformer charging, Tesla networks and arcs/plasma, beacon effect persistence/rendering, farmer/tree state reloads, chainer overlays/highlights, electric tools, armor/decorations, dye/rainbow recipes, and the Steam Chainsaw tooltip. Audit retained generated resources for the new item/equipment/recipe schemas and missing textures/models, including legacy item-model predicates. Test world save/reload and machine upgrades; do not assume old-version saves are compatible. Client/server datagen and optional integrations remain unverified.
