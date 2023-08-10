# Binary sizes (release mode)
- Rust: 10.7 MB
- Java: 55.7 MB (plus JRE ~124 MB)
- Go:
- Node: 

# Performance numbers


# Rust (release build)
Total execution time for 1000000 requests: 350.052781717s (~5:50 min)
Min request time: 223.5µs
Max request time: 40.805167ms
Avg request time: 350.052µs
Median request time: 293.292µs

Total execution time for 10000 requests: 2.702996538s
Min request time: 195.583µs
Max request time: 19.207625ms
Avg request time: 270.299µs
Median request time: 258.333µs

# Rust (debug build)
Total execution time for 10000 requests: 19.578517991s
Min request time: 1.835958ms
Max request time: 93.613666ms
Avg request time: 1.957851ms
Median request time: 1.907625ms


# Java (Spring Boot, Hibernate, Undertow)
Total execution time for 1000000 requests: 1489.879897145s (~24:50 min)
Min request time: 785.333µs
Max request time: 296.268209ms
Avg request time: 1.489879ms
Median request time: 1.41375ms

# Java (Spring Boot, pure JDBC, GraalVM non-native)
Total execution time for 1000000 requests: 505.483530755s (8:25 min)
Min request time: 349µs
Max request time: 81.126625ms
Avg request time: 505.483µs
Median request time: 426.709µs

# Java (Spring Boot, pure JDBC, OpenJDK)
Total execution time for 1000000 requests: 617.477230381s (10:17 min)
Min request time: 351.375µs
Max request time: 111.861708ms
Avg request time: 617.477µs
Median request time: 489.125µs

# Java (Spring Boot, pure JDBC, OpenJDK, no Zxcvbn)
Total execution time for 1000000 requests: 254.37222998s (4:14 min)
Min request time: 135.125µs
Max request time: 109.73825ms
Avg request time: 254.372µs
Median request time: 178.458µs

# Java (Spring Boot, pure JDBC, GraalVM non-native, no Zxcvbn)
Total execution time for 1000000 requests: 266.730708424s (4:26 min)
Min request time: 133.583µs
Max request time: 89.0385ms
Avg request time: 266.73µs
Median request time: 184.708µs

# Java (Spring Boot, pure JDBC, GraalVM native, no Zxcvbn)
Total execution time for 1000000 requests: 227.118148435s (3:47 min)
Min request time: 85.833µs
Max request time: 39.9165ms
Avg request time: 227.118µs
Median request time: 146.584µs


# Go (release binary, file-based SQLite)
Total execution time for 1000000 requests: 872.868440764s (~14:32 min)
Min request time: 643.083µs
Max request time: 997.49425ms
Avg request time: 872.868µs
Median request time: 769.333µ

# Go (release build, in-memory SQLite)
Total execution time for 1000000 requests: 811.703522364s (~13:31 min)
Min request time: 626.042µs
Max request time: 44.212042ms
Avg request time: 811.703µs
Median request time: 751.5µs

# Node Express 
Total execution time for 1000000 requests: 978.544048653s (~16:18 min)
Min request time: 681.875µs
Max request time: 72.223917ms
Avg request time: 978.544µs
Median request time: 825.667µs

# Node Koa
Total execution time for 1000000 requests: 775.911226477s (~12:56 min)
Min request time: 613.792µs
Max request time: 29.1275ms
Avg request time: 775.911µs
Median request time: 708.083µs

# Node Express
Total execution time for 1000000 requests: 892.707699658s (~14:62 min)
Min request time: 646.25µs
Max request time: 51.269291ms
Avg request time: 892.707µs
Median request time: 745.833µs