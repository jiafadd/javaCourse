各个GC的特点

 ![](http://jiafa-bucket.oss-cn-shenzhen.aliyuncs.com/img/1636883904286.png)

**1.使用 GCLogAnalysis.java 自己演练一遍串行 / 并行 /CMS/G1 的案例。（GCeasy） 统计的数据如下：**

| GC             | Minor GC(Avg time) | Full GC(Avg Time) |
| -------------- | ------------------ | ----------------- |
| Serial GC 1g   | 37.1ms             | 70.3ms            |
| Serial GC 4g   | 112ms              | n/a               |
| Parallel GC 1g | 24.33ms            | 80ms              |
| Parallel GC 4g | 129ms              | n/a               |

| GC     | Avg Pause GC Time |
| ------ | ----------------- |
| CMS 1g | 35.9ms            |
| CMS 4g | 60ms              |
| G1 1g  | 13.6ms            |
| G1 4g  | 27.5ms            |

- 对于GC和JVM来说，延迟和吞吐量是什么关系？

   延迟（latency） = 请求响应出入系统的时间 

  吞吐量 = 运行用户代码时间 / (运行用户代码时间 + 运行垃圾收集时间)

  对于Serial、CMS和G1这类更关注低延迟的性能的GC收集器，其垃圾收集的暂停时间会相对较短，但是对于Parallel GC来说，因为此GC的所有阶段都不能中断，即GC启动之后，属于⼀次性完成所有操作，于是单次 暂停的时间会较长，所以并⾏GC很容易出现⻓时间的卡顿。由上表也可以看出，ParallelGC在GC的时间是要大于SerialGC的。



- CMS有哪些阶段是需要STW的？CMS在哪方面有了提升？

  在日志中，CMS的过程如下

  ```cmd
  2021-11-14T10:57:39.611+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 361482K(699072K)] 396714K(1013632K), 0.0002421 secs] [Times: user=0.00 sy
  s=0.00, real=0.00 secs]
  2021-11-14T10:57:39.612+0800: [CMS-concurrent-mark-start]
  2021-11-14T10:57:39.615+0800: [CMS-concurrent-mark: 0.004/0.004 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
  2021-11-14T10:57:39.615+0800: [CMS-concurrent-preclean-start]
  2021-11-14T10:57:39.617+0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
  2021-11-14T10:57:39.617+0800: [CMS-concurrent-abortable-preclean-start]
  2021-11-14T10:57:39.660+0800: [GC (Allocation Failure) 2021-11-14T10:57:39.660+0800: [ParNew: 314560K->34942K(314560K), 0.0346569 secs] 676042K->47
  4026K(1013632K), 0.0349228 secs] [Times: user=0.23 sys=0.00, real=0.03 secs]
  2021-11-14T10:57:39.742+0800: [GC (Allocation Failure) 2021-11-14T10:57:39.742+0800: [ParNew: 314558K->34943K(314560K), 0.0399257 secs] 753642K->54
  7078K(1013632K), 0.0402989 secs] [Times: user=0.28 sys=0.05, real=0.04 secs]
  2021-11-14T10:57:39.818+0800: [GC (Allocation Failure) 2021-11-14T10:57:39.818+0800: [ParNew: 314559K->34943K(314560K), 0.0481968 secs] 826694K->62
  4983K(1013632K), 0.0484511 secs] [Times: user=0.25 sys=0.03, real=0.05 secs]
  2021-11-14T10:57:39.901+0800: [GC (Allocation Failure) 2021-11-14T10:57:39.901+0800: [ParNew: 314222K->34943K(314560K), 0.0433877 secs] 904263K->70
  3071K(1013632K), 0.0435279 secs] [Times: user=0.22 sys=0.05, real=0.04 secs]
  2021-11-14T10:57:39.945+0800: [CMS-concurrent-abortable-preclean: 0.006/0.328 secs] [Times: user=1.17 sys=0.13, real=0.33 secs]
  2021-11-14T10:57:39.945+0800: [GC (CMS Final Remark) [YG occupancy: 41813 K (314560 K)]2021-11-14T10:57:39.945+0800: [Rescan (parallel) , 0.0005649
   secs]2021-11-14T10:57:39.946+0800: [weak refs processing, 0.0001016 secs]2021-11-14T10:57:39.946+0800: [class unloading, 0.0002621 secs]2021-11-14
  T10:57:39.946+0800: [scrub symbol table, 0.0004029 secs]2021-11-14T10:57:39.947+0800: [scrub string table, 0.0001146 secs][1 CMS-remark: 668128K(69
  9072K)] 709941K(1013632K), 0.0016936 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
  2021-11-14T10:57:39.947+0800: [CMS-concurrent-sweep-start]
  2021-11-14T10:57:39.949+0800: [CMS-concurrent-sweep: 0.002/0.002 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
  2021-11-14T10:57:39.950+0800: [CMS-concurrent-reset-start]
  2021-11-14T10:57:39.952+0800: [CMS-concurrent-reset: 0.002/0.002 secs] [Times: user=0.03 sys=0.00, real=0.00 secs]
  ```

- 对于CMS的GC过程，可以分为五个步骤：
  - 1.CMS Initial Mark 初始标记，上图该过程STW时间为0.000241s。
  - 2.Concurrent Mark 并发标记
  - 3.Concurrent Preclean 并发预清理

  - 4.Final Remark 最终标记，上图该过程STW时间为0.0005649s。
  - 5.Concurrent Sweep 并发清理
  - 6.Concurrent Reset 并发重置

  CMS是针对老年代的垃圾回收，在该垃圾回收过程中，会有ParNewGC贯穿其中。在CMS的整个过程中，STW的时间都是非常低的。使用-XX:+UseConcMarkSweapGC启动垃圾收集器，时间开销主要还是在年轻代使用的ParNew垃圾收集器的消耗上。

- G1有哪些阶段是需要STW的？ G1和CMS的区别在哪里

  G1 GC最主要的设计⽬标是：将STW停顿的时间和分布，变成可预期且可配置的。 

```cmd
 java -XX:+PrintGC -XX:+PrintGCDateStamps -Xmx1g -Xms1g -XX:+UseG1GC GCLogAnalysis
 
正在执行...
2021-11-14T11:09:12.521+0800: [GC pause (G1 Evacuation Pause) (young) 63M->21M(1024M), 0.0059665 secs]
2021-11-14T11:09:12.539+0800: [GC pause (G1 Evacuation Pause) (young) 79M->41M(1024M), 0.0055642 secs]
2021-11-14T11:09:12.557+0800: [GC pause (G1 Evacuation Pause) (young) 97M->60M(1024M), 0.0056040 secs]
2021-11-14T11:09:12.583+0800: [GC pause (G1 Evacuation Pause) (young) 135M->81M(1024M), 0.0073159 secs]
2021-11-14T11:09:12.621+0800: [GC pause (G1 Evacuation Pause) (young) 199M->124M(1024M), 0.0114084 secs]
2021-11-14T11:09:12.748+0800: [GC pause (G1 Evacuation Pause) (young) 531M->244M(1024M), 0.0223546 secs]
2021-11-14T11:09:12.785+0800: [GC pause (G1 Evacuation Pause) (young) 341M->268M(1024M), 0.0143063 secs]
2021-11-14T11:09:12.842+0800: [GC pause (G1 Evacuation Pause) (young) 463M->315M(1024M), 0.0136896 secs]
2021-11-14T11:09:12.905+0800: [GC pause (G1 Evacuation Pause) (young) 572M->377M(1024M), 0.0192908 secs]
2021-11-14T11:09:12.961+0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 600M->434M(1024M), 0.0215620 secs]
2021-11-14T11:09:12.983+0800: [GC concurrent-root-region-scan-start]
2021-11-14T11:09:12.984+0800: [GC concurrent-root-region-scan-end, 0.0004783 secs]
2021-11-14T11:09:12.984+0800: [GC concurrent-mark-start]
2021-11-14T11:09:12.986+0800: [GC concurrent-mark-end, 0.0017880 secs]
2021-11-14T11:09:12.986+0800: [GC remark, 0.0017479 secs]
2021-11-14T11:09:12.988+0800: [GC cleanup 451M->438M(1024M), 0.0017595 secs]
2021-11-14T11:09:12.990+0800: [GC concurrent-cleanup-start]
2021-11-14T11:09:12.990+0800: [GC concurrent-cleanup-end, 0.0001841 secs]
2021-11-14T11:09:13.068+0800: [GC pause (G1 Evacuation Pause) (young) 804M->515M(1024M), 0.0305523 secs]
2021-11-14T11:09:13.101+0800: [GC pause (G1 Evacuation Pause) (mixed) 527M->432M(1024M), 0.0134596 secs]
2021-11-14T11:09:13.126+0800: [GC pause (G1 Evacuation Pause) (mixed) 494M->445M(1024M), 0.0049497 secs]
2021-11-14T11:09:13.131+0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 446M->446M(1024M), 0.0022533 secs]
2021-11-14T11:09:13.134+0800: [GC concurrent-root-region-scan-start]
2021-11-14T11:09:13.134+0800: [GC concurrent-root-region-scan-end, 0.0005936 secs]
2021-11-14T11:09:13.134+0800: [GC concurrent-mark-start]
2021-11-14T11:09:13.137+0800: [GC concurrent-mark-end, 0.0021994 secs]
2021-11-14T11:09:13.137+0800: [GC remark, 0.0018277 secs]
2021-11-14T11:09:13.139+0800: [GC cleanup 468M->467M(1024M), 0.0010877 secs]
2021-11-14T11:09:13.140+0800: [GC concurrent-cleanup-start]
2021-11-14T11:09:13.140+0800: [GC concurrent-cleanup-end, 0.0001137 secs]
2021-11-14T11:09:13.195+0800: [GC pause (G1 Evacuation Pause) (young) 779M->523M(1024M), 0.0215235 secs]
2021-11-14T11:09:13.220+0800: [GC pause (G1 Evacuation Pause) (mixed) 540M->459M(1024M), 0.0147644 secs]
2021-11-14T11:09:13.236+0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 463M->458M(1024M), 0.0024991 secs]
2021-11-14T11:09:13.238+0800: [GC concurrent-root-region-scan-start]
2021-11-14T11:09:13.239+0800: [GC concurrent-root-region-scan-end, 0.0004475 secs]
2021-11-14T11:09:13.239+0800: [GC concurrent-mark-start]
2021-11-14T11:09:13.241+0800: [GC concurrent-mark-end, 0.0020550 secs]
2021-11-14T11:09:13.242+0800: [GC remark, 0.0013307 secs]
2021-11-14T11:09:13.243+0800: [GC cleanup 473M->470M(1024M), 0.0007397 secs]
2021-11-14T11:09:13.244+0800: [GC concurrent-cleanup-start]
2021-11-14T11:09:13.244+0800: [GC concurrent-cleanup-end, 0.0001008 secs]
2021-11-14T11:09:13.300+0800: [GC pause (G1 Evacuation Pause) (young) 791M->525M(1024M), 0.0173848 secs]
2021-11-14T11:09:13.322+0800: [GC pause (G1 Evacuation Pause) (mixed) 548M->465M(1024M), 0.0146080 secs]
2021-11-14T11:09:13.337+0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 465M->465M(1024M), 0.0021463 secs]
2021-11-14T11:09:13.339+0800: [GC concurrent-root-region-scan-start]
2021-11-14T11:09:13.339+0800: [GC concurrent-root-region-scan-end, 0.0002909 secs]
2021-11-14T11:09:13.339+0800: [GC concurrent-mark-start]
2021-11-14T11:09:13.341+0800: [GC concurrent-mark-end, 0.0017279 secs]
2021-11-14T11:09:13.341+0800: [GC remark, 0.0012693 secs]
2021-11-14T11:09:13.343+0800: [GC cleanup 477M->474M(1024M), 0.0008215 secs]
2021-11-14T11:09:13.343+0800: [GC concurrent-cleanup-start]
2021-11-14T11:09:13.344+0800: [GC concurrent-cleanup-end, 0.0001601 secs]
2021-11-14T11:09:13.403+0800: [GC pause (G1 Evacuation Pause) (young) 749M->522M(1024M), 0.0143666 secs]
2021-11-14T11:09:13.423+0800: [GC pause (G1 Evacuation Pause) (mixed) 549M->460M(1024M), 0.0165588 secs]
2021-11-14T11:09:13.441+0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 463M->461M(1024M), 0.0035567 secs]
2021-11-14T11:09:13.444+0800: [GC concurrent-root-region-scan-start]
2021-11-14T11:09:13.445+0800: [GC concurrent-root-region-scan-end, 0.0003204 secs]
2021-11-14T11:09:13.445+0800: [GC concurrent-mark-start]
2021-11-14T11:09:13.447+0800: [GC concurrent-mark-end, 0.0019749 secs]
2021-11-14T11:09:13.447+0800: [GC remark, 0.0012401 secs]
2021-11-14T11:09:13.448+0800: [GC cleanup 478M->475M(1024M), 0.0004916 secs]
2021-11-14T11:09:13.449+0800: [GC concurrent-cleanup-start]
2021-11-14T11:09:13.449+0800: [GC concurrent-cleanup-end, 0.0001409 secs]
执行结束!共生成对象次数:11908
```

- 由日志可以看出：
  - 前一部分中，主要是**G1 Evacuation Pause**，也称为**年轻代模式转移暂停**。在应⽤程序刚启动时，G1还没有采集到什么⾜够的信息，这时候就处于初始的 fully-young 模式。当年轻代空间⽤满后，应⽤线程会被暂停，年轻代内存块中的存活对象被拷⻉到存活区。如果还没有存活区，则任意选择⼀部分空闲的内存块作为存活区。 拷⻉的过程称为转移(Evacuation)，与其他垃圾收集器年轻代的标记-复制类似。

  - 接着就是并发标记的阶段：该阶段与CMS比较相似
    - 1.Inital Mark做初始标记，需要短暂STW
    - 2.Root Region Scan Root区扫描 
    - 3.Concurrent Mark 并发标记
    - 4.Remark 再次标记，需要短暂STW
    - 5.Cleanup 清理，需要短暂STW
  - Evacuation Pause mixed阶段，**转移暂停: 混合模式**。并发标记完成之后，G1将执⾏⼀次混合收集（mixed collection），就是不只清理年轻代，还将⼀部分⽼年代区域也加⼊到 回收集中。

- 堆内存从1g到4g的变化，在该GCLogAnalysis类的测试中，将堆内存从1g调到4g，可以明显得看到：
  - 堆中的新生代内存等比例地增加，新生代的GC次数减少，所以对于有的垃圾回收器，对象的年龄还不足进入老年代。
  - 对于新生代的GC过程中，GC时间也有了明显的呈倍数地增加。

