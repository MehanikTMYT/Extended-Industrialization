---
navigation:
  title: "Фермер"
  icon: "steam_farmer"
  parent: extended_industrialization:machines.md
categories:
  - machines
item_ids:
  - extended_industrialization:steam_farmer
  - extended_industrialization:electric_farmer
---

# Фермер

<Row>
	<RecipeFor id="extended_industrialization:steam_farmer" />
	<RecipeFor id="extended_industrialization:electric_farmer" />
</Row>

Фермер выполняет не рецепты, а сельскохозяйственные задачи: вспахивает и увлажняет почву,
сажает, удобряет и собирает урожай, растения и деревья. Для работы фермера
не обязательно использовать все эти задачи; иногда отдельные задачи лучше
отключить. Например, при выращивании деревьев не нужны вспашка и увлажнение почвы.

Фермеру требуется достаточное питание. Паровой фермер потребляет пар,
эквивалентный 32 ЭЕ/т, а электрический фермер — 64 ЭЕ/т.

Землю в составе мультиблока фермера можно
заменить любым блоком с тегом `#extended_industrialization:farmer_dirt` .

## Задачи

### Вспашка почвы

Если включить эту задачу в настройках формы мультиблока, входящие в
его состав блоки земли превратятся в пашню, как при использовании мотыги.

### Увлажнение почвы

При подаче воды через шлюз ввода фермер поддерживает
всю пашню увлажнённой, поэтому размещать воду поблизости не требуется.

### Посадка

При подаче посадочного материала через шлюз ввода фермер высаживает его на подходящие
блоки в рабочей области. Можно сажать только предметы с тегом `#extended_industrialization:farmer_plantable` .

### Удобрение

Удобрение доступно только электрическому фермеру. При подаче подходящего жидкого удобрения (указанного в EMI) через
шлюз ввода фермер воздействует на растения в рабочей области подобно костной муке. Это
действует и на растения, обычно не поддающиеся костной муке, например кактусы и сахарный тростник.

### Сбор

Полностью выросшее растение (например, созревшая пшеница или дерево, выросшее из саженца)
разрушается, а его добыча сохраняется, если в шлюзе вывода достаточно места.

Предметы с тегом `#extended_industrialization:farmer_voidable` имеют низкий приоритет сохранения в шлюзах вывода. Если
при сборе блока для них нет места, они уничтожаются. Например, это
относится к <ItemLink id="minecraft:stick" /> и <ItemLink id="minecraft:apple" /> : можно закрепить слоты шлюзов вывода
только за брёвнами и саженцами, не заботясь о хранении этих побочных предметов.

## Паровой фермер

<GameScene zoom="1" interactive={true} fullWidth={true}>
    <MultiblockShape controller="extended_industrialization:steam_farmer" />
    <MultiblockShape controller="extended_industrialization:steam_farmer" useBigShape={true} x="-10" z="-10" />
</GameScene>

## Электрический фермер

<GameScene zoom="1" interactive={true} fullWidth={true}>
    <MultiblockShape controller="extended_industrialization:electric_farmer" />
    <MultiblockShape controller="extended_industrialization:electric_farmer" useBigShape={true} x="-12" z="-12" />
</GameScene>