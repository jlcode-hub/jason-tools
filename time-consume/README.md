# 采用AOP和滑动窗口统计接口的调用情况

- 1、使用`@EnableTimeConsume` 加载时间打印类，`jason.tools.time-consume.enabled` 来控制是否启用统计

- 2、在需要打印调用时间的接口或者方法上添加`@TimeConsume`
  - `key`：接口唯一标识，为空时采用类名加方法名进行标识
  - `period`：接口的统计周期大小（滑动窗口的大小），单位：秒，可使用环境变量如：`${jason.tools.time-consume.period}`
  - `block`：默认周期单位（滑动窗口的单位块大小，该参数将直接影响性能和窗口滑动的平滑程度），单位：秒，可使用环境变量如：`${jason.tools.time-consume.block}`


- 3、`jason.tools.time-consume.printMethod` 控制是否打印方法的调用信息
- 4、`jason.tools.time-consume.statisticPrintInterval` 统计信息的打印间隔，单位：秒
- 5、`jason.tools.time-consume.defaultPeriod` 默认统计周期长度（滑动窗口的大小），单位：秒
- 6、`jason.tools.time-consume.defaultBlock` 默认周期单位（滑动窗口的单位块大小，该参数将直接影响性能和窗口滑动的平滑程度），单位：秒
