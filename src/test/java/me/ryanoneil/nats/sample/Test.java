package me.ryanoneil.nats.sample;

public class Test {

    private String example;

    public Test() {
    }

    public Test(String example) {
        this.example = example;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    @Override
    public String toString() {
        return "Test{" +
            "example='" + example + '\'' +
            '}';
    }
}
