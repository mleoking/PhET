package edu.colorado.phet.website.panels.lists;

import java.util.Locale;

/**
 * An OrderListItem that can also be automatically sorted with a given locale
 */
public interface SortableListItem extends OrderListItem {

    /**
     * @param item   Other item to compare to
     * @param locale Locale to use for sorting
     * @return -1, 0, or 1 as the classic compareTo functions return
     */
    public int compareTo( SortableListItem item, Locale locale );
}
