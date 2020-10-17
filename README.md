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