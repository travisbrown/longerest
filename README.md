Usage
-----

Run the following:

``` bash
./sbt "runMain Generator c 2 longerest_designwards.csv"
```

Replace `c` with `s` for the superlative version.

To add columns from a design file:

``` bash
./sbt "runMain AddColumns longerest_e1.csv longerest_e1_results.csv out.csv"
```
