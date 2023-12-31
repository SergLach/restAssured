package api;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ColorsData {
    private int id;
    private String name;
    private int year;
    private String color;
    private String pantone_value;

    public ColorsData(int id, String name, int year, String color, String pantone_value) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.color = color;
        this.pantone_value = pantone_value;
    }
}
