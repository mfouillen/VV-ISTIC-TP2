package fr.istic.vv.Exercise3;

class Person {
    private int age;
    private String name;
    public String lastname;
    
    public String getName() { return name; }

    public boolean isAdult() {
        return age > 17;
    }
}
