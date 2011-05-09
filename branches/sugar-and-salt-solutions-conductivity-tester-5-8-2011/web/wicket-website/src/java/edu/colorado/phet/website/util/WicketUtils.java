package edu.colorado.phet.website.util;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.model.Model;

public class WicketUtils {

    /**
     * Highlight every other row of a list view. Call within the populateItem( ListItem item ) loop.
     *
     * @param item The list item to possibly highlight
     */
    public static void highlightListItem( ListItem item ) {
        if ( item.getIndex() % 2 == 0 ) {
            item.add( new AttributeAppender( "class", new Model<String>( "list-highlight-background" ), " " ) );
        }
    }
}
