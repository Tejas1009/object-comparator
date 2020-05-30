package models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomColumn {
    private String name;
    private String type;
    private int length;
}