import java.util.stream.IntStream;

/**
 * @author: darren
 * @data: 2020-03-17 13:01
 */
public class TestLambda {

    public static void main(String[] args) {
        int x = 2;
        IntStream.of(1, 2, 3).map(i -> i * 2).map(i -> i * x);
    }

    /**
     * 上面这段代码会对 IntStream 中的元素进行两次映射。我们知道，映射方法 map 所接收的参数是 IntUnaryOperator（这是一个函数式接口）。
     * 也就是说，在运行过程中我们需要将 i->i2 和 i->ix 这两个 Lambda 表达式转化成 IntUnaryOperator 的实例。
     * 这个转化过程便是由 invokedynamic 来实现的。
     *
     *
     *
     * 在编译过程中，Java 编译器会对 Lambda 表达式进行解语法糖（desugar），
     * 生成一个方法来保存 Lambda 表达式的内容。该方法的参数列表不仅包含原本 Lambda 表达式的参数，
     * 还包含它所捕获的变量。在上面那个例子中，第一个 Lambda 表达式没有捕获其他变量，
     * 而第二个 Lambda 表达式（也就是 i->i*x）则会捕获局部变量 x。
     * 这两个 Lambda 表达式对应的方法如下所示。可以看到，所捕获的变量同样也会作为参数传入生成的方法之中。
     *
     *
     *
     // i -> i * 2
     private static int lambda$0(int);
     Code:
     0: iload_0
     1: iconst_2
     2: imul
     3: ireturn

     // i -> i * x
     private static int lambda$1(int, int);
     Code:
     0: iload_1
     1: iload_0
     2: imul
     3: ireturn
     *
     *
     *
     * 第一次执行 invokedynamic 指令时，它所对应的启动方法会通过 ASM 来生成一个适配器类。
     * 这个适配器类实现了对应的函数式接口，在我们的例子中，也就是 IntUnaryOperator。
     * 启动方法的返回值是一个 ConstantCallSite，其链接对象为一个返回适配器类实例的方法句柄。
     *
     *
     *
     * 根据 Lambda 表达式是否捕获其他变量，启动方法生成的适配器类以及所链接的方法句柄皆不同。
     * 如果该 Lambda 表达式没有捕获其他变量，那么可以认为它是上下文无关的。
     * 因此，启动方法将新建一个适配器类的实例，并且生成一个特殊的方法句柄，始终返回该实例。
     * 如果该 Lambda 表达式捕获了其他变量，那么每次执行该 invokedynamic 指令，我们都要更新这些捕获了的变量，
     * 以防止它们发生了变化。另外，为了保证 Lambda 表达式的线程安全，我们无法共享同一个适配器类的实例。
     * 因此，在每次执行 invokedynamic 指令时，所调用的方法句柄都需要新建一个适配器类实例。
     * 在这种情况下，启动方法生成的适配器类将包含一个额外的静态方法，来构造适配器类的实例。
     * 该方法将接收这些捕获的参数，并且将它们保存为适配器类实例的实例字段。
     * 你可以通过虚拟机参数 -Djdk.internal.lambda.dumpProxyClasses=/DUMP/PATH 导出这些具体的适配器类。
     *
     *
     *
     *
     *
     // i->i*2 对应的适配器类
     final class LambdaTest$$Lambda$1 implements IntUnaryOperator {
     private LambdaTest$$Lambda$1();
     Code:
     0: aload_0
     1: invokespecial java/lang/Object."<init>":()V
     4: return

     public int applyAsInt(int);
     Code:
     0: iload_1
     1: invokestatic LambdaTest.lambda$0:(I)I
     4: ireturn
     }

     // i->i*x 对应的适配器类
     final class LambdaTest$$Lambda$2 implements IntUnaryOperator {
     private final int arg$1;

     private LambdaTest$$Lambda$2(int);
     Code:
     0: aload_0
     1: invokespecial java/lang/Object."<init>":()V
     4: aload_0
     5: iload_1
     6: putfield arg$1:I
     9: return

     private static java.util.function.IntUnaryOperator get$Lambda(int);
     Code:
     0: new LambdaTest$$Lambda$2
     3: dup
     4: iload_0
     5: invokespecial "<init>":(I)V
     8: areturn

     public int applyAsInt(int);
     Code:
     0: aload_0
     1: getfield arg$1:I
     4: iload_1
     5: invokestatic LambdaTest.lambda$1:(II)I
     8: ireturn
     }

     可以看到，捕获了局部变量的 Lambda 表达式多出了一个 get$Lambda 的方法。
     启动方法便会所返回的调用点链接至指向该方法的方法句柄。也就是说，每次执行 invokedynamic 指令时，
     都会调用至这个方法中，并构造一个新的适配器类实例。
     */


}
