# Using PMD

Pick a Java project from Github (see the [instructions](../sujet.md) for suggestions). Run PMD on its source code using any ruleset. Describe below an issue found by PMD that you think should be solved (true positive) and include below the changes you would add to the source code. Describe below an issue found by PMD that is not worth solving (false positive). Explain why you would not solve this issue.

## Answer

We choose the project Apache Commons Math. We used pmd on the all project and got a file report.

Here we can sport an false positive just as the "UselessParentheses". Indeed, we can take the first occurence in folder "commons-math-master\commons-math-core\src\main\java\org\apache\commons\math4\core\jdkmath\AccurateMath.java" line 396 we have this line "return (0.5 * t) * t;" which can be remplaced by return 0.5 * t * t. This will not change the code however it's more readable for the user. This is why we should not change this line and modify the ruleset.

A true positiv could be for example in the folder "\commons-math-legacy\src\test\java\org\apache\commons\math4\legacy\distribution\EmpiricalDistributionTest.java" we code the following code 
```java
try {
    final BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
    String str = null;
    while ((str = in.readLine()) != null) {
        list.add(Double.valueOf(str));
    }
    in.close();
} catch (IOException ex) {
    Assert.fail("IOException " + ex);
}
```
Here, the buffer in opened in the try and closed after. The issue is that, if an error is encountered right after the oppening, it will go directly inside the catch and will not got through the "in.closed". So we have to fix that by adding an additional close in the catch just in case it go directly.
