**Question**

Design and implement a thread safe task queue, which users can perform put/add task action on this queue while the queue is on. Once the queue turns off users cannot change the queue.

for example,

```java
interface TaskQueue {
  add(Task)
  len() int
  get() (Task, bool)
  done(Task)
  shutdown()
  is_closed() bool
}
```

提示：
- 注意代码可读性，添加必要的注释
- 注意代码风格与规范，添加必要的单元测试和文档
-  注意异常处理，尝试优化性能



**Use it** (in the future)

- Download this repo

<font color=#A9A9A9 face="黑体">git clone https://github.com/deqianzou/PingCAP-TaskQueue </font>

- Build

<font color=#A9A9A9 face="黑体">mvn package -Pdist,native -Dtar -Dmaven.test.skip=true</font>



**TODO**

*  Implement an available service which utilizes the queue.
*  Optimize the performance.
*  Optimize unit test.

**Contact**

For more information or if you have any advice, please contact me. Deqian Zou 18217356623@163.com or deqianzou@gmail.com