package dev.mee42

import dev.mee42.parse.fullParse


val stdlib = """
+ => Int -> Int -> Int
+ = add

- => Int -> Int -> Int
- = sub

* => Int -> Int -> Int
* = times
mult => Int -> Int -> Int
mult = times

/ => Int -> Int -> Int
/ = div

""".trimIndent()
val standardLibrary = VariableSetBuilder.internal {
    intBiFunction("add", Int::plus)
    intBiFunction("sub", Int::minus)
    intBiFunction("times", Int::times)
    intBiFunction("div", Int::div)
    intBiFunction("==") { a, b -> if(a == b) 1 else 0 }
    func("if") {
        type("(Int -> Int) -> (Int -> Int) -> Int -> Int")
        this.executor { (`if`,`else`, predicate), s ->
            if(predicate.evaluate(s).int() != 0){
                (`if` as FunctionalValue).evaluate(listOf(predicate.evaluate(s)), s)
            } else {
                (`else` as FunctionalValue).evaluate(listOf(predicate.evaluate(s)), s)
            }
        }
    }
    intBiFunction("max") { a, b -> if(a > b) a else b }
}.let { it + fullParse(stdlib, it.typedVariables) }


private fun VariableSetBuilder.intBiFunction(symbol: String, bifunction: (Int, Int) -> Int) {
    func(symbol) {
        type("Int -> Int -> Int")
        execute { valueInt( bifunction(int(0), int(1))) }
    }
}
private fun Value.int(): Int = ((this as? InstantValue)?.value as? Int) ?: error("no")
private fun InternalFunctionBuilder.execute(block: InternalFunctionBuilderHelper.() -> Value) {
    executor { list, state -> block(InternalFunctionBuilderHelper(state, list)) }
}

private class InternalFunctionBuilderHelper(val variableSet: VariableSet, private val values: List<Value>) {
    fun int(i: Int): Int {
        val value = values[i].evaluate(variableSet) as? InstantValue ?: error("not an int value")
        if(value.type != Type.INT) error("type is not of int")
        return value.value as Int
    }
    fun valueInt(i: Int): Value {
        return InstantValue(Type.INT, i)
    }
}
