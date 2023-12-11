# Extending PMD

Use XPath to define a new rule for PMD to prevent complex code. The rule should detect the use of three or more nested `if` statements in Java programs so it can detect patterns like the following:

```Java
if (...) {
    ...
    if (...) {
        ...
        if (...) {
            ....
        }
    }

}
```
Notice that the nested `if`s may not be direct children of the outer `if`s. They may be written, for example, inside a `for` loop or any other statement.
Write below the XML definition of your rule.

You can find more information on extending PMD in the following link: https://pmd.github.io/latest/pmd_userdocs_extending_writing_rules_intro.html, as well as help for using `pmd-designer` [here](https://github.com/selabs-ur1/VV-ISTIC-TP2/blob/master/exercises/designer-help.md).

Use your rule with different projects and describe you findings below. See the [instructions](../sujet.md) for suggestions on the projects to use.

## Answer


We are adding a new rule to our file quickstart.xml :
```xml
<rule name="AvoidNestedIf" language="java" message="If nested detected" class="net.sourceforge.pmd.lang.rule.XPathRule">
    <description>Avoid nested IF</description>
    <priority>3</priority>
    <properties>
        <property name="xpath">
            <value>
                <![CDATA[
                //IfStatement//IfStatement//IfStatement
                ]]>
            </value>
        </property>
    </properties>
</rule>
```

We start with the command : 
pmd check -f html --rulesets=D:\Downloads\commons-math-master\commons-math-master\rulesets\java\quickstart.xml -d D:\Downloads\commons-math-master -r result.html


Now we are able to stop nested if. For example, in the folder : \commons-math-master\commons-math-master\commons-math-core\src\main\java\org\apache\commons\math4\core\jdkmath\AccurateMath.java

At line 861  we have nested if : 
```java
if (x < 0.0) {

// We don't check against intVal here as conversion of large negative double values
// may be affected by a JIT bug. Subsequent comparisons can safely use intVal
if (x < -746d) {
    if (hiPrec != null) {
        hiPrec[0] = 0.0;
        hiPrec[1] = 0.0;
    }
    return 0.0;
}
```
So the rule is working properly

