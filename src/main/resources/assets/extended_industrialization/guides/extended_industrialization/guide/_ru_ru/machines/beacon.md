---
navigation:
  title: "Электрический маяк"
  icon: "electric_beacon"
  parent: extended_industrialization:machines.md
categories:
  - machines
item_ids:
  - extended_industrialization:electric_beacon
---

# Электрический маяк

<RecipeFor id="extended_industrialization:electric_beacon" />

Электрический маяк расходует зелья и накладывает их эффекты на всех игроков поблизости, потребляя
256 ЭЕ/т за каждый эффект. Время действия зелья в маяке равно половине
длительности его самого короткого эффекта. Обычно зелье даёт один эффект. Например, зелье скорости
на 8 минут обеспечит эффект в области на 4 минуты при расходе
256 ЭЕ/т. Зелье черепашьей мощи на 40 секунд действует лишь 20 секунд и
требует 512 ЭЕ/т, поскольку даёт два эффекта (замедление и сопротивление). Поскольку маяк
расходует зелья, рекомендуется автоматизировать их производство с помощью [пивоварни](brewery.md) для непрерывной работы маяка.

Радиус действия маяка больше, чем у обычного, и зависит
от количества слоёв под ним, без учёта стальной обшивки.

| Слои | Радиус |
|--------|-------|
| 1      | 30    |
| 2      | 50    |
| 3      | 70    |
| 4      | 90    |

<GameScene zoom="1.5" interactive={true} fullWidth={true}>
    <MultiblockShape controller="extended_industrialization:electric_beacon" />
    <MultiblockShape controller="extended_industrialization:electric_beacon" useBigShape={true} x="-9" y="2" z="-9" />
</GameScene>