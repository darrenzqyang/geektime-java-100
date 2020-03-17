import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * 要实现自定义收集器，只需要实现java.util.stream.Collector<T, A, R>接口即可，这个接口包含五个无参方法：[supplier， accumulator， combiner， finisher， characteristics]。
 * 泛型含义如下：
 * <p>
 * T：缩减操作的输入元素的类型
 * A：还原操作的可变累积类型（通常隐藏为实现细节）
 * R：还原操作的结果类型
 * T不必说，收集什么泛型的列表，就输入什么类型，比如我对一个Student列表进行收集计算，那么T肯定是Student。
 * A是计算过程中用来盛放计算结果的容器，一般都是List，Set等等。
 * R就比较好理解，就是收集完成后返回的类型，需要注意的是，当characteristics()中包含Characteristics.IDENTITY_FINISH时，。
 *
 * @author: darren
 * @data: 2020-03-17 15:32
 */
public class MostPopularCollector implements Collector<Object, Map<Object, AtomicInteger>, Optional<Object>> {


    /**
     * 该方法返回一个Supplier<A>类型的结果，表示在计算过程中，如何初始化一个临时容器，比如A=List，那么一般返回ArrayList::new
     *
     * @return
     */
    @Override
    public Supplier<Map<Object, AtomicInteger>> supplier() {
        return ConcurrentHashMap::new;
    }

    /**
     * accumulator 核心方法，关键的计算逻辑都放在这里，定义了如何把一个个元素放入临时容器中，返回类型为BiConsumer<A, T>
     *
     * @return
     */
    @Override
    public BiConsumer<Map<Object, AtomicInteger>, Object> accumulator() {
        return (map, obj) -> map.computeIfAbsent(obj, k -> new AtomicInteger()).addAndGet(1);
    }

    /**
     * 用于在并发计算的情况下，对各路并发计算的结果进行合并，方法返回的lambda，两个参数就是进行合并的两路计算器，lambda要求最后返回合并的结果
     *
     * @return
     */
    @Override
    public BinaryOperator<Map<Object, AtomicInteger>> combiner() {
        return (m1, m2) -> {
            m1.forEach((key, value) ->
                    m2.put(key, new AtomicInteger(value.addAndGet(m2.get(key) == null ? 0 : m2.get(key).get())))
            );
            return m2;
        };
    }

    /**
     * 输出最后的结果
     *
     * @return
     */
    @Override
    public Function<Map<Object, AtomicInteger>, Optional<Object>> finisher() {
        return acc -> Optional.ofNullable(acc.entrySet()
                .stream()
                .max((Comparator.comparingInt(o -> o.getValue().get())))
                .get().getKey());
    }

    /**
     * characteristics 表示收集计算的方式，返回类型为Set<Characteristics>,其中Characteristics是一个枚举类型，指示收集器属性的特征，可用于优化缩减实现。
     * 它只有三个值[CONCURRENT, UNORDERED, IDENTITY_FINISH]，
     * 注释翻译过来分别是：1.表示此收集器是并发的，这意味着结果容器可以支持与来自多个线程的相同结果容器同时调用的累加器函数。
     * 如果CONCURRENT收集器也不是UNORDERED，那么只有在应用于无序数据源时才应同时评估它。
     * 2.指示集合操作不承诺保留输入元素的遭遇顺序。（如果结果容器没有内在顺序，例如Set，则可能是这样。）
     * 3.表示整理器功能是标识功能，可以省略。 如果设置，则必须是从A到R的未经检查的强制转换成功的情况。
     *
     * @return
     */
    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(Characteristics.CONCURRENT));
    }

}
