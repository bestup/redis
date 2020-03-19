# redis

redis应用的工程实例

## 1、redisson-serial
序列号的两种实现方式， 单体应用通过Lock实现。 分布式环境下可通过redis实现锁， 利用redis `incr` 指令递增序列， 源码可以直接使用。






