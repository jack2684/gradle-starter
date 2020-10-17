Order simulation 
==================
Author: Junjie Guan

Getting Started
---------------

```
./gradlew run
```

Or, run with customized order ingestion rate `gw run --args='$rate'`. Example:
```
./gradlew run --args='10'
```

Note that the simulation time is 10x of real world time. Meaning, **it takes only 1 seconds to simulate 10 seconds**.

Example:
```
$ gw run --args='20'
Disabling Carbon Black and Crowd Strike...

> Task :run
=========Reading CLI inputs=========

data.Order ingestion rate: Main.CliArgs(ingestionRate=20.0)/sec
=========Loading Orders=========
Load orders done. Number of orders: 132

=========Initiating Shelves=========
Initiating shelves done:
core.Shelf(name=Hot core.Shelf, temp=HOT, capacity=10, shelfDecayModifier=1, orders=[])
core.Shelf(name=Cold core.Shelf, temp=COLD, capacity=10, shelfDecayModifier=1, orders=[])
core.Shelf(name=Frozen core.Shelf, temp=FROZEN, capacity=10, shelfDecayModifier=1, orders=[])
core.Shelf(name=Overflow core.Shelf, temp=ANY, capacity=15, shelfDecayModifier=2, orders=[])

=========simulation.Simulation Started=========
data.Order simulation.Simulation 100% │████████████████│ 132/132 (0:00:01 / 0:00:00)

=========Printing Report=========
 TIME   HOT_SHELF  COLD_SHELF   FROZEN_SHELF  OVERFLOW_SHELF | DELIVERED   EXPIRED  TRASH |COMPLETED
    0           0           0              0               0 |         0         0      0 |      0
    1           5           9              6               0 |         0         0      0 |      0
    2          10          11             11               7 |         1         0     10 |     11
    3           7           7              8              14 |        14         0     17 |     31
    4           9           8              7              14 |        25         0     26 |     51
    5           6           6              7              14 |        41         0     33 |     74
    6           8           6             10              14 |        49         0     34 |     83
    7           8           9              6              11 |        63         1     34 |     98
    8           3           5              2               7 |        79         2     34 |    115
    9           1           2              1               3 |        89         2     34 |    125
   10           1           0              0               0 |        95         2     34 |    131
   11           0           0              0               0 |        96         2     34 |    132

=========Done=========
```

Contribute
---------------
After making modification, use this command to run test:
```
./gradlew test
```