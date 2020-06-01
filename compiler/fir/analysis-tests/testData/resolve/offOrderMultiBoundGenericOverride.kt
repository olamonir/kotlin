// FILE: Test.java
public class Test {

    public interface I1 {}
    public interface I2 {}

    public interface I12 extends I1, I2 {}

    public static class Base {
        public <P extends I1 & I2> void foo() {}
    }

    public static class Derived extends Base {
        @Override
        public <P extends I2 & I1> void foo() {}
    }
}

// FILE: main.kt
interface KI1
interface KI2
interface KI12 : KI1, KI2

open class KBase {
    open fun <P> foo()
    where P : KI1, P : KI2 {}
}

class KDerived : KBase() {
    override fun <P> foo()
    where P : KI2, P : KI1 {}
}


fun callJava(derived: Test.Derived) {
    derived.foo<Test.I12>()
}

fun callKotlin(derived: KDerived) {
    derived.foo<KI12>()
}