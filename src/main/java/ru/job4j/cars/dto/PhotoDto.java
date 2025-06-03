package ru.job4j.cars.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PhotoDto {

    private String name;

    private byte[] content;

    public boolean isEmpty() {
        return content == null || content.length == 0;
    }
}
