package models;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CustomTable implements Cloneable {
    private String name;
    private String primarycolumn;
    private List<CustomColumn> columns;
    private List<Relation> relations;
    private List<TableAccess> access;

    public Object clone() throws
            CloneNotSupportedException {
        CustomTable t = (CustomTable) super.clone();
        return t;
    }
}
