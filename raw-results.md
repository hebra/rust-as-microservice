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

Total execution time for 999999 requests: 325.594397023s (~5:41 min)  
Min request time: 197.875µs  
Max request time: 31.898333ms  
Avg request time: 325.594µs  
Median request time: 272.917µs  

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

# Go (release binary, file-based SQLite)
Total execution time for 1000000 requests: 872.868440764s (~14:53 min)  
Min request time: 643.083µs  
Max request time: 997.49425ms  
Avg request time: 872.868µs  
Median request time: 769.333µs  

# Go (release build, in-memory SQLite)
Total execution time for 1000000 requests: 811.703522364s (~13:51 min)  
Min request time: 626.042µs  
Max request time: 44.212042ms  
Avg request time: 811.703µs  
Median request time: 751.5µs  

# Node Express 
Total execution time for 1000000 requests: 978.544048653s (~16:30 min)  
Min request time: 681.875µs  
Max request time: 72.223917ms  
Avg request time: 978.544µs  
Median request time: 825.667µs  

# Node Koa
Total execution time for 1000000 requests: 775.911226477s (~12.08 min)  
Min request time: 613.792µs  
Max request time: 29.1275ms  
Avg request time: 775.911µs  
Median request time: 708.083µs  
