---
navigation:
  title: "Соединитель механизмов"
  icon: "machine_chainer"
  parent: extended_industrialization:machines.md
categories:
  - machines
item_ids:
  - extended_industrialization:machine_chainer
  - extended_industrialization:machine_chainer_relay
---

# Соединитель механизмов

<GameScene zoom="2" interactive={true} fullWidth={true}>
	<ImportStructure src="machine_chainer_example.nbt" />
	<IsometricCamera yaw="180" pitch="30" />
</GameScene>

<RecipeFor id="extended_industrialization:machine_chainer" />

Соединитель механизмов подключает механизмы, бочки и другие блоки с инвентарём
и тегом `#extended_industrialization:machine_chainer/linkable` , расположенные по прямой длиной до 64
блоков. Их инвентари объединяются в общий инвентарь соединителя. Соединитель можно
направить в любую сторону, в том числе вверх и вниз.

Соединитель передаёт предметы, жидкости и энергию! Передача энергии ограничена
3x пропускной способности одного кабеля соответствующего уровня. Соединитель
не может обмениваться энергией с подключёнными механизмами другого напряжения.

## Реле соединителя механизмов

Реле подключается к соединителю, но не имеет инвентаря. Оно заполняет
промежутки в цепочке, позволяя не устанавливать в каждом из них механизм.

<RecipeFor id="extended_industrialization:machine_chainer_relay" />