package ru.job4j.cars;

import lombok.Getter;

@Getter
public enum GlobalExceptionMessage {
    GLOBAL_EXCEPTION_MESSAGE("Произошла непредвиденная ошибка");

    private final String message;

    GlobalExceptionMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "GlobalExceptionMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
