package models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Relation {
    private String name;
    private AssociatedColumn associatedcolumn;
}
