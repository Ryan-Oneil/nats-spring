package me.ryanoneil.nats.sample;

public class SamplePojo {

    private String example;

    public SamplePojo() {
    }

    public SamplePojo(String example) {
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
