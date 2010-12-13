package edu.colorado.phet.website.panels.lists;

import org.apache.wicket.Component;

/**
 * Represents an abstract selection item that can be displayed in lists and added
 */
public interface OrderListItem {

    /**
     * @return What string should we display for this item
     */
    public String getDisplayValue();

    /**
     * @param id Component ID
     * @return A component suitable to display this item, with the particular wicket id specified by 'id'
     */
    public Component getDisplayComponent( String id );

    /**
     * @return Some understandable ID that can be used later (and will be used to identify this item)
     */
    public int getId();
}
