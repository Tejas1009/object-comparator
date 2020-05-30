package models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssociatedColumn implements Comparable<AssociatedColumn> {
    private String src;
    private String dest;

    @Override
    public int compareTo(AssociatedColumn o) {
        int retVal = getSrc().compareTo(o.getSrc());
        if (retVal != 0) {
            return retVal;
        }
        return getDest().compareTo(o.getDest());
    }
}