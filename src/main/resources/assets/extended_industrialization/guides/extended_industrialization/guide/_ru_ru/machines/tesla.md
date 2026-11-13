---
navigation:
  title: "Тесла"
  icon: "tesla_coil"
  parent: extended_industrialization:machines.md
categories:
  - machines
item_ids:
  - extended_industrialization:tesla_calibrator
  - extended_industrialization:tesla_handheld_receiver
  - extended_industrialization:tesla_interdimensional_upgrade
  - extended_industrialization:tesla_coil
  - extended_industrialization:tesla_receiver
  - extended_industrialization:lv_tesla_receiver_hatch
  - extended_industrialization:mv_tesla_receiver_hatch
  - extended_industrialization:hv_tesla_receiver_hatch
  - extended_industrialization:ev_tesla_receiver_hatch
  - extended_industrialization:superconductor_tesla_receiver_hatch
  - extended_industrialization:tesla_tower
  - extended_industrialization:aluminum_tesla_winding
  - extended_industrialization:annealed_copper_tesla_winding
  - extended_industrialization:copper_tesla_winding
  - extended_industrialization:electrum_tesla_winding
  - extended_industrialization:superconductor_tesla_winding
---

# Тесла

Катушки и приёмники Теслы позволяют передавать ЭЕ по беспроводной сети с дополнительными затратами. В
сети Теслы может быть только один передатчик (катушка Теслы или башня Теслы); встроенного
ограничения числа приёмников Теслы нет. Каждый передатчик имеет собственный радиус действия и пассивное энергопотребление.

## Калибратор Теслы

Чтобы связать передатчик Теслы с приёмниками, сначала нажмите **<KeyBind id="key.sneak" />**
+ **<KeyBind id="key.use" />**, держа калибратор Теслы и нацелившись на передатчик. Затем
нажмите **<KeyBind id="key.use" />** калибратором по нужному приёмнику, чтобы связать его.

<RecipeFor id="extended_industrialization:tesla_calibrator" />

## Передатчики Теслы

Передатчик Теслы служит источником энергии каждой сети Теслы.

Передатчик может передавать энергию только приёмнику с точно таким же напряжением. Например,
катушка Теслы с усовершенствованным корпусом механизма не может питать приёмник Теслы
без корпуса, но может питать приёмник с таким же усовершенствованным корпусом механизма.

Пассивное потребление ЭЕ/т катушки Теслы зависит от напряжения установленного корпуса или его отсутствия.

<RecipeFor id="extended_industrialization:tesla_coil" />

Напряжение передачи башни Теслы определяется установленными энергетическими шлюзами ввода.

Пассивное потребление ЭЕ/т, предел передачи и радиус действия башни Теслы
зависят от обмоток. Характеристики каждой обмотки указаны в её всплывающей подсказке.

<Row>
	<GameScene zoom="0.75" interactive={true} fullWidth={false}>
		<MultiblockShape controller="extended_industrialization:tesla_tower" />
	</GameScene>
	<RecipeFor id="extended_industrialization:tesla_tower" />
</Row>

## Приёмники Теслы

Приёмники Теслы получают энергию, отправляемую передатчиком.

Приёмник Теслы накапливает полученную энергию и выдаёт её через выходную грань. Энергию
также можно извлекать кабелями, как из любого другого блока с энергетическим выходом.

<RecipeFor id="extended_industrialization:tesla_receiver" />

Приёмный шлюз Теслы получает энергию так же, как обычный приёмник
Теслы, но служит энергетическим шлюзом ввода мультиблока. Он объединяет приёмник
и шлюз ввода в одном блоке, заменяя два отдельных устройства.

<RecipeFor id="extended_industrialization:lv_tesla_receiver_hatch" />