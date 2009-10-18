package edu.colorado.phet.website.admin;

import org.apache.wicket.Component;

public interface OrderListItem {
    public String getDisplayValue();

    public Component getDisplayComponent( String id );

    public int getId();
}
