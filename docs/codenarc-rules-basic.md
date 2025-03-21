---
layout: default
title: CodeNarc - Basic Rules
---  

# Basic Rules  ("*rulesets/basic.xml*")

## AssertWithinFinallyBlock Rule

Checks for *assert* statements within a *finally* block. An *assert* can throw an exception,
hiding the original exception, if there is one.

Here is an example of code that produces a violation:

```
    int myMethod(int count) {
        try {
            doSomething()
        } finally {
            assert count > 0        // violation
        }
    }
```



## AssignmentInConditional Rule

*Since CodeNarc 0.13*

An assignment operator (=) was used in a conditional test. This is usually a typo, and the comparison operator (==) was intended.

Example of violations:

```
    if ((value = true)) {
        // should be ==
    }

    while (value = true) {
        // should be ==
    }

    (value = true) ? x : y
    (value = true) ?: x

    // the following code has no violations
    if (value == true) {
    }

    value == true ? x : y
    value == true ?: x
```


## BigDecimalInstantiation Rule

Checks for calls to the `java.math.BigDecimal` constructors that take a `double` value as
the first parameter. As described in the `BigDecimal` javadoc, the results from these constructors
can be somewhat unpredictable, and their use is generally not recommended. This is because some numbers,
such as 0.1, cannot be represented exactly as a `double`.

For instance, executing `println new BigDecimal(0.1)` prints out
`0.1000000000000000055511151231257827021181583404541015625`.

Here is an example of code that produces a violation:

```
    def b1 = new BigDecimal(0.1)               // violation
    def b2 = new java.math.BigDecimal(23.45d)  // violation
```


## BitwiseOperatorInConditional Rule

*Since CodeNarc 0.15*

Checks for bitwise operations in conditionals. For instance, the condition `if (a | b)` is almost
always a mistake and should be `if (a || b)`. If you need to do a bitwise operation then it is
best practice to extract a temp variable.

Example of violations:

```
    if (a | b) { }
    if (a & b) { }
```


## BooleanGetBoolean Rule

*Since CodeNarc 0.13*

This rule catches usages of java.lang.Boolean.getBoolean(String) which reads a boolean from the System properties. It is often mistakenly used to attempt to read user input or parse a String into a boolean. It is a poor piece of API to use; replace it with System.properties['prop̈́'].

Example of violations:

```
    // produces violation
    Boolean.getBoolean(value)

    // zero or two parameters is OK, must be different method
    Boolean.getBoolean(value, 1)
    Boolean.getBoolean()
```


## BrokenNullCheck Rule

*Since CodeNarc 0.17*

Looks for faulty checks for *null* that can cause a `NullPointerException`.

Examples:

```
    if (name != null || name.length > 0) { }            // violation
    if (name != null || name.length) { }                // violation
    while (record == null && record.id > 10) { }        // violation
    if (record == null && record.id && doStuff()) { }   // violation
    def isNotValid = record == null && record.id > 10   // violation
    return record == null && !record.id                 // violation

    if (name != null || name.size() > 0) { }            // violation
    if (string == null && string.equals("")) { }        // violation
    def isValid = name != null || name.size() * 0       // violation
    return name != null || !name.size()                 // violation
```


## BrokenOddnessCheck Rule

*Since CodeNarc 0.13*

The code uses `x % 2 == 1` to check to see if a value is odd, but this won't work for negative numbers
(e.g., `(-5) % 2 == -1)`. If this code is intending to check for oddness, consider using `x & 1 == 1`,
or ` x % 2 != 0`.

Examples:

```
    if (x % 2 == 1) { }             // violation
    if (method() % 2 == 1) { }      // violation

    if (x & 1 == 1) { }             // OK
    if (x % 2 != 0) { }             // OK
```


## ClassForName Rule

*Since CodeNarc 0.14*

Using `Class.forName(...)` is a common way to add dynamic behavior to a system. However, using this method can cause
resource leaks because the classes can be pinned in memory for long periods of time. If you're forced to do dynamic
class loading then use ClassLoader.loadClass instead. All variations of the `Class.forName(...)` method suffer from the
same problem.

For more information see these links:

  * <http://blog.bjhargrave.com/2007/09/classforname-caches-defined-class-in.html>

  * <http://www.osgi.org/blog/2011/05/what-you-should-know-about-class.html>

Example of violations:

```
    Class.forName('SomeClassName')
    Class.forName(aClassName, true, aClassLoader)
```


## ComparisonOfTwoConstants Rule

*Since CodeNarc 0.14*

Checks for expressions where a *comparison operator* or `equals()` or `compareTo()` is used to
compare two constants to each other or two literals that contain only constant values.

Here are examples of code that produces a violation:

```
    23 == 67                    // violation
    Boolean.FALSE != false      // violation
    23 < 88                     // violation
    0.17 >= 0.99                // violation
    "abc" > "ddd"               // violation
    [Boolean.FALSE] >= [27]     // violation
    [a:1] <=> [a:2]             // violation

    [1,2].equals([3,4])                                     // violation
    [a:123, b:true].equals(['a':222, b:Boolean.FALSE])      // violation

    [a:123, b:456].compareTo([a:222, b:567]                 // violation
    [a:false, b:true].compareTo(['a':34.5, b:Boolean.TRUE]  // violation
```


## ComparisonWithSelf Rule

*Since CodeNarc 0.14*

Checks for expressions where a *comparison operator* or `equals()` or `compareTo()` is used to compare a
variable to itself, e.g.: `x == x, x != x, x <=> x, x > x, x >= x, x.equals(x) or x.compareTo(x)`, where
`x` is a variable.

Here are examples of code that produces a violation:

```
    if (x == x) { }                 // violation
    if (x != x) { }                 // violation
    while (x > x) { }               // violation
    if (x >= x) { }                 // violation
    while (x > x) { }               // violation
    if (x <= x) { }                 // violation
    def c = (x <=> x) { }           // violation
    println isReady = x.equals(x)   // violation
    println x.compareTo(x)          // violation
```


## ConstantAssertExpression Rule

Checks for *assert* statements with a constant value for the *assert* boolean expression, such as
`true`, `false`, `null`, or a literal constant value. These *assert* statements
will always pass or always fail, depending on the constant/literal value. Examples of violations include:

```
    assert true
    assert false, "assertion message"
    assert Boolean.TRUE
    assert Boolean.FALSE
    assert null
    assert 0
    assert 99.7
    assert ""
    assert "abc"
    assert [:]
    assert [a:123, b:456]
    assert [a, b, c]
```


## ConstantIfExpression Rule

Checks for *if* statements with a constant value for the *if* boolean expression, such as
`true`, `false`, `null`, or a literal constant value. These *if* statements
can be simplified or avoided altogether. Examples of violations include:

```
    if (true) { .. }
    if (false) { .. }
    if (Boolean.TRUE) { .. }
    if (Boolean.FALSE) { .. }
    if (null) { .. }
    if (0) { .. }
    if (99.7) { .. }
    if ("") { .. }
    if ("abc") { .. }
    if ([:]) { .. }
    if ([a:123, b:456]) { .. }
    if ([a, b, c]) { .. }
```


## ConstantTernaryExpression Rule

Checks for ternary expressions with a constant value for the boolean expression, such as
`true`, `false`, `null`, or a literal constant value. Examples of violations include:

```
    true ? x : y
    false ? x : y
    Boolean.TRUE ? x : y
    Boolean.FALSE ? x : y
    null ? x : y
    0 ? x : y
    99.7 ? x : y
    "" ? x : y
    "abc" ? x : y
    [:] ? x : y
    [a:123, b:456] ? x : y
    [a, b, c] ? x : y
```

The rule also checks for the same types of constant values for the boolean expressions within the "short"
ternary expressions, also known as the "Elvis" operator, e.g.:

```
    true ?: y
    null ?: y
    99.7 ?: y
    "abc" ?: y
    [:] ?: y
    [a, b, c] ?: y
```

## DeadCode Rule

*Since CodeNarc 0.11*

Dead code appears after a `return` statement or an exception is thrown. If code appears after one of these
statements then it will never be executed and can be safely deleted.


## DoubleNegative Rule

*Since CodeNarc 0.11*

There is no point in using a double negative, it is always positive. For instance `!!x` can always be
simplified to `x`. And `!(!x)` can as well.


## DuplicateCaseStatement Rule

*Since CodeNarc 0.11*

Check for duplicate `case` statements in a `switch` block, such as two equal
integers or strings. Here are some examples of code that produces violations:

```
    switch( 0 ) {
        case 1: break;
        case 2: break;
        case 2: break;          // violation
    }

    switch( "test" ) {
        case "$a": break;
        case "$a": break;       // ok; only flags constant values (not GStrings)
        case "ab": break;
        case "ab": break;       // violation
        case "abc": break;
    }
```

## DuplicateMapKey Rule

*Since CodeNarc 0.14*

A *Map* literal is created with duplicated key. The map entry will be overwritten.

Example of violations:

```
    def var1 = [a:1, a:2, b:3]        //violation
    def var2 = [1:1, 1:2, 2:3]        //violation
    def var3 = ["a":1, "a":2, "b":3]  //violation

    // these are OK
    def var4 = [a:1, b:1, c:1]
    def var5 = [1:1, 2:1, 3:1]
    def var6 = ["a":1, "b":1, "c":1]
```


## DuplicateSetValue Rule

*Since CodeNarc 0.14*

A *Set* literal is created with duplicate constant value. A set cannot contain two elements with the same value.

Example of violations:

```
    def a = [1, 2, 2, 4] as Set
    def b = [1, 2, 2, 4] as HashSet
    def c = [1, 2, 2, 4] as SortedSet
    def d = [1, 2, 2, 4] as FooSet
    def e = ['1', '2', '2', '4'] as Set
    def f = ['1', '2', '2', '4'] as HashSet
    def g = ['1', '2', '2', '4'] as SortedSet
    def h = ['1', '2', '2', '4'] as FooSet

    // these are OK
    def a = [1, 2, 3, 4] as Set
    def b = ['1', '2', '3', '4'] as Set
    def c = [1, '1'] as Set
```


## EmptyCatchBlock Rule

Checks for empty *catch* blocks. In most cases, exceptions should not be caught and ignored (swallowed).

The rule has a property named `ignoreRegex` that defaults to the value 'ignore|ignored'. If the name of the exception
matches this regex then no violations are produced.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| ignoreRegex                 | Regular expression - exception parameter names matching this regular expression are ignored and no violations are produced. | 'ignore\|ignored' |

Here is an example of code that produces a violation:

```
    def myMethod() {
        try {
            doSomething
        } catch(MyException e) {                //violation
            // should do something here
        }
    }

    def myMethod() {
        try {
            doSomething
        } catch(MyException ignored) {
            //no violations because the parameter name is ignored
        }
    }
```

## EmptyClass Rule

*Since CodeNarc 0.19*

Reports classes without methods, fields or properties. Why would you need a class like this?

This rule ignores interfaces, abstract classes, enums, anonymous inner classes, subclasses (extends), and classes with annotations.


## EmptyElseBlock Rule


Checks for empty *else* blocks. Empty *else* blocks are confusing and serve no purpose.

Here is an example of code that produces a violation:

```
    def myMethod() {
        if (x==23) {
            println 'ok'
        } else {
            // empty
        }
    }
```


## EmptyFinallyBlock Rule


Checks for empty *finally* blocks. Empty *finally* blocks are confusing and serve no purpose.

Here is an example of code that produces a violation:

```
    def myMethod() {
        try {
            doSomething()
        } finally {
            // empty
        }
    }
```

## EmptyForStatement Rule

Checks for empty *for* blocks. Empty *for* statements are confusing and serve no purpose.

Here is an example of code that produces a violation:

```
    def myMethod() {
        for (int i=0; i * 23; i++) {
            // empty
        }
    }
```

## EmptyIfStatement Rule

Checks for empty *if* statements. Empty *if* statements are confusing and serve no purpose.

Here is an example of code that produces a violation:

```
    def myMethod() {
        if (x==23) {
            // empty
        }
    }
```

## EmptyInstanceInitializer Rule

*Since CodeNarc 0.13*

An empty class instance initializer was found. It is safe to remove it. Example:

```
    class MyClass {
        { }     // empty instance initializer, not a closure
    }
```

## EmptyMethod Rule

*Since CodeNarc 0.13*

A method was found without an implementation. If the method is overriding or implementing a parent method,
then mark it with the `@Override` annotation. This rule should not be used with Java 5 code because you cannot
put `@Override` on a method implementing an interface. Use with Java 6 and higher.

Example of violations:

```
    class MyClass {

        // violation, empty method
        public void method1() {}

        // violation, empty method
        def method2() {}

        // OK because of @Override
        @Override
        public void method3() {}
    }

    abstract class MyBaseClass {
        // OK, handled by EmptyMethodInAbstractClass Rule
        public void method() {}
    }
```


## EmptyStaticInitializer Rule

*Since CodeNarc 0.13*

An empty static initializer was found. It is safe to remove it. Example:

```
    class MyClass {
        static { }
    }
```


## EmptySwitchStatement Rule

Checks for empty *switch* statements. Empty *switch* statements are confusing and serve no purpose.

Here is an example of code that produces a violation:

```
    def myMethod() {
        switch(myVariable) {
            // empty
        }
    }
```


## EmptySynchronizedStatement Rule
~

Checks for empty *synchronized* statements. Empty *synchronized* statements are confusing and serve no purpose.

Here is an example of code that produces a violation:

```
    class MyClass {
        def myMethod() {
            synchronized(lock) {
            }
        }
    }
```

## EmptyTryBlock Rule

Checks for empty *try* blocks. Empty *try* blocks are confusing and serve no purpose. This rule ignores all try-with-resources statements.

Here is an example of code that produces a violation:

```
    def myMethod() {
        try {
            // empty
        } catch(MyException e) {
            e.printStackTrace()
        }
    }
```


## EmptyWhileStatement Rule

Checks for empty *while* statements. Empty *while* statements are confusing and serve no purpose.

Here is an example of code that produces a violation:

```
    def myMethod() {
        while (!stopped) {
            // empty
        }
    }
```


## EqualsAndHashCode Rule

Checks that if either the `boolean equals(Object)` or the `int hashCode()` methods
are overridden within a class, then both must be overridden.

Here is an example of code that produces a violation:

```
    class MyClass {
        boolean equals(Object object) {
            // do something
        }
    }
```

And so does this:

```
    class MyClass {
        int hashCode() {
            return 0
        }
    }
```


## EqualsOverloaded Rule

*Since CodeNarc 0.14*

The class has an `equals` method, but the parameter of the method is not of type `Object`.
It is not overriding `equals` but instead overloading it.

Example of violations:

```
    class Object1 {
        //parameter should be Object not String
        boolean equals(String other) { true }
    }

    class Object2 {
        // Overloading equals() with 2 parameters is just mean
        boolean equals(Object other, String other2) { true }
    }

    class Object3 {
        // a no-arg equals()? What is this supposed to do?
        boolean equals() { true }
    }


    // all of these are OK and do not cause violations
    class Object4 {
        boolean equals(Object other) { true }
    }

    @SuppressWarnings('EqualsOverloaded')
    class Object5 {
        boolean equals(String other) { true }
    }

    class Object6 {
        boolean equals(java.lang.Object other) { true }
    }

    class Object7 {
        boolean equals(other) { true }
    }
```


## ExplicitGarbageCollection Rule

*Since CodeNarc 0.12*

Calls to `System.gc()`, `Runtime.getRuntime().gc()`, and `System.runFinalization()` are not advised. Code should have
the same behavior whether the garbage collection is disabled using the option `-Xdisableexplicitgc` or not. Moreover,
"modern" JVMs do a very good job handling garbage collections. If memory usage issues unrelated to memory leaks develop
within an application, it should be dealt with JVM options rather than within the code itself.


## ForLoopShouldBeWhileLoop Rule

*Since CodeNarc 0.14*

A `for` loop without an init and update statement can be simplified to a `while` loop.

Example of violations:

```
    int i = 0;
    for(; i * 5;) {     // Violation
        println i++
    }

    // These are OK
    for(i in [1,2])         // OK
       println i

    for(int i = 0; i*5;)    // OK
        println i++

    int i = 0;
    for(; i * 5; i++)       // OK
        println i

    for (Plan p : plans) {  // OK
        println "Plan=$p"
    }
```


## HardCodedWindowsFileSeparator Rule

*Since CodeNarc 0.15*

This rule finds usages of a Windows file separator within the constructor call of a File object. It is better to use
the Unix file separator or use the File.separator constant.

Example of violations:

```
   new File('.\\foo\\')
   new File('c:\\dir')
   new File('../foo\\')
```

## HardCodedWindowsRootDirectory Rule

*Since CodeNarc 0.15*

This rule find cases where a File object is constructed with a windows-based path. This is not portable across operating systems
or different machines, and using  the File.listRoots() method is a better alternative.

Example of violations:

```
   new File('c:\\')
   new File('c:\\dir')
   new File('E:\\dir')
```

## IntegerGetInteger Rule

*Since CodeNarc 0.13*

This rule catches usages of java.lang.Integer.getInteger(String, ...) which reads an Integer from the System properties.
It is often mistakenly used to attempt to read user input or parse a String into an Integer.
It is a poor piece of API to use; replace it with System.properties['prop'].

Example of violations:

```
    // violations
    Integer.getInteger(value)
    Integer.getInteger(value, radix)

    // zero or more than 2 parameters is OK, must be different method
    Integer.getInteger()
    Integer.getInteger(value, radix, locale)
```


## MultipleUnaryOperators Rule

*Since CodeNarc 0.21*

Checks for multiple consecutive unary operators. These are confusing, and are likely typos and bugs.

Example of violations:

```
    int z = ~~2             // violation
    boolean b = !!true      // violation
    boolean c = !!!false    // 2 violations
    int j = -~7             // violation
    int k = +~8             // violation
```


## ParameterAssignmentInFilterClosure Rule

<Since CodeNarc 2.1.0>

An assignment operator was used on a parameter, or a property or subproperty of the parameter, in a filtering or searching closure. This is usually a typo, and the comparison operator (==) was intended.

This rule will check the following filter methods: `find`, `findAll`, `findIndexOf`, `every`, `any`, `filter`, `grep`, `dropWhile` and `takeWhile`.

Example of violations:

```
    List someList = [1,2,3]
    someList.find {it == 2}
    someList.find {it = 2}                  // violation, this actually finds 1 instead.
    someList.find { int integer ->
        integer == 2
    }
    someList.find { int integer ->
        integer = 2                         // violation, this actually finds 1 instead.
    }
    someList.takeWhile { it.name = 42 }     // violation
```


## RandomDoubleCoercedToZero Rule

*Since CodeNarc 0.15*

The Math.random() method returns a double result greater than or equal to 0.0 and less than 1.0. If you coerce this
result into an Integer, Long, int, or long then it is coerced to zero. Casting the result to int, or assigning it to an int
field is probably a bug.

Example of violations:

```
    (int) Math.random()
    (Integer) Math.random()
    int x = Math.random()
    Integer y = Math.random()
    int m() { Math.random() }
    Integer m() { Math.random() }
    (Math.random()) as int
    (Math.random()) as Integer
```

## RemoveAllOnSelf Rule

*Since CodeNarc 0.11*

Don't use `removeAll` to clear a collection. If you want to remove all elements from a
collection `c`, use `c.clear`, not `c.removeAll(c)`. Calling `c.removeAll(c)`
to clear a collection is less clear, susceptible to errors from typos, less efficient and
for some collections, might throw a `ConcurrentModificationException`.


## ReturnFromFinallyBlock Rule

Checks for a return from within a *finally* block. Returning from a *finally* block is confusing and
can hide the original exception.

Here is an example of code that produces a violation:

```
    int myMethod() {
        try {
            doSomething()
            return 0
        } catch(Exception e) {
            return -1
        } finally {
            return 99               // violation
        }
    }
```

## ThrowExceptionFromFinallyBlock

Checks for throwing an exception from within a *finally* block. Throwing an exception from a
*finally* block is confusing and can hide the original exception.

Here is an example of code that produces a violation:

```
    int myMethod() {
        try {
            doSomething()
            throw new Exception()
        } finally {
            println 'finally'
            throw new Exception()   // violation
        }
    }
```
