# TCC *vs* LCC

Explain under which circumstances *Tight Class Cohesion* (TCC) and *Loose Class Cohesion* (LCC) metrics produce the same value for a given Java class. Build an example of such as class and include the code below or find one example in an open-source project from Github and include the link to the class below. Could LCC be lower than TCC for any given class? Explain.

## Answer

They produce same value when all methods uses same attributes (all methods are connected).
```java
public class Square{
    private int size;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int area() {
        return size * size;
    }

    public int perimeter() {
        return 4*size;
    }
}
```


When we represent method connection as graph, using methods as vertex and attributes as edges. LCC is the transitive closure of TCC. This means that TCC graph is included in LCC graph.
TCC and LCC value is equals to (number of edges) / (number of vertexes).
The number of vertexes is the same in the two graphs (because it's representes the methods), but the number of edges is greater or equals in LCC than TCC. 
So LCC is always greater or equals than TCC.