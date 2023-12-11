# Code of your exercise

Code section we added to quickstart.xml :
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
