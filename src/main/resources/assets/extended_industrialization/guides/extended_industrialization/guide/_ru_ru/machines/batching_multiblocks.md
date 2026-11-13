---
navigation:
  title: "Мультиблоки с пакетной обработкой"
  icon: "processing_array"
  parent: extended_industrialization:machines.md
categories:
  - machines
item_ids:
  - extended_industrialization:large_steam_furnace
  - extended_industrialization:large_steam_macerator
  - extended_industrialization:large_electric_furnace
  - extended_industrialization:large_electric_macerator
  - extended_industrialization:processing_array
---

# Мультиблоки с пакетной обработкой

Некоторые мультиблоки работают как обычные механизмы своего типа, но
обрабатывают увеличенное количество ресурсов одновременно. Мультиблок, способный выполнять рецепты
партиями по Y, может за раз потреблять в 1x-Yx больше
ресурсов и выдавать результаты в соответствии с размером партии.
Потребление ЭЕ/т также умножается на количество одновременно выполняемых обработок.

Размер партии зависит от мультиблока. Конкретные
значения указаны во всплывающей подсказке его предмета.

Как и другие механизмы, они не могут выполнять разные рецепты одновременно.

## Большая печь

Размер партии большой электрической печи зависит от катушек
в мультиблоке, подобно конструкции электрической доменной печи. Размер партии
для каждой катушки указан в её всплывающей подсказке.

<Row>
	<RecipeFor id="extended_industrialization:large_steam_furnace" />
	<RecipeFor id="extended_industrialization:large_electric_furnace" />
</Row>

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="extended_industrialization:large_steam_furnace" />
    <MultiblockShape controller="extended_industrialization:large_electric_furnace" x="-6" y="-1" z="-6" />
</GameScene>

## Большой измельчитель

<Row>
	<RecipeFor id="extended_industrialization:large_steam_macerator" />
	<RecipeFor id="extended_industrialization:large_electric_macerator" />
</Row>

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="extended_industrialization:large_steam_macerator" />
    <MultiblockShape controller="extended_industrialization:large_electric_macerator" x="-6" z="-6" />
</GameScene>

## Обрабатывающий массив

Обрабатывающий массив пакетно выполняет рецепты любого одноблочного электрического
производственного механизма, помещённого в его интерфейс. Размер партии ограничен
размером массива и количеством установленных в нём механизмов.

<RecipeFor id="extended_industrialization:processing_array" />

<GameScene zoom="2" interactive={true} fullWidth={true}>
    <MultiblockShape controller="extended_industrialization:processing_array" />
    <MultiblockShape controller="extended_industrialization:processing_array" useBigShape={true} x="-6" z="-8" />
</GameScene>