# Binary sizes (release mode)
- Rust: 10.7 MB
- Java: 55.7 MB (plus JRE ~124 MB)
- Go:
- Node: 

# Performance numbers


# Rust (release build)
Total execution time for 1000000 requests: 387.388836795s (~6:45 min)
Min request time: 237.958µs
Max request time: 29.220334ms
Avg request time: 387.388µs
Median request time: 328.167µs

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


# Java (Spring Boot, Hibernate, Tomcat)
Total execution time for 1000000 requests: 2707.453974442s (~45 min)
Min request time: 977.333µs
Max request time: 222.552917ms
Avg request time: 2.707453ms
Median request time: 2.03925ms

# Java (Spring Boot, Hibernate, Undertow)
Total execution time for 10000 requests: 20.762249658s
Min request time: 1.008792ms
Max request time: 195.077084ms
Avg request time: 2.076224ms
Median request time: 1.604583ms
