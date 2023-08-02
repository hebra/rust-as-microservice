# Binary sizes (release mode)
- Rust: 10.7 MB
- Java: 55.7 MB (plus JRE ~124 MB)
- Go:
- Node: 

# Performance numbers


# Rust
Total execution time for 1000000 requests: 387.388836795s (~6:45 min)
Min request time: 237.958µs
Max request time: 29.220334ms
Avg request time: 387.388µs
Median request time: 328.167µs


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
