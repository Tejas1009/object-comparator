package models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TableAccess {
    private String schemaname;
    private String user;
    private List<String> accesstyperead;
    private List<String> accesstypewrite;
}
