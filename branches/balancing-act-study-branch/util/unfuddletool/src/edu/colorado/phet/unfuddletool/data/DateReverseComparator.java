package edu.colorado.phet.unfuddletool.data;

import java.util.Comparator;
import java.util.Date;

public class DateReverseComparator implements Comparator<Date> {
    public int compare( Date a, Date b ) {
        if ( a.getTime() == b.getTime() ) {
            return 0;
        }
        if ( a.getTime() < b.getTime() ) {
            return 1;
        }
        else {
            return -1;
        }
    }
}
