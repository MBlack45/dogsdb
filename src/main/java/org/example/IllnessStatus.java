package org.example;

public enum IllnessStatus {
    ILL("Хворіє"),
    CURED("Вилікувана");

    private final String label;

    IllnessStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}