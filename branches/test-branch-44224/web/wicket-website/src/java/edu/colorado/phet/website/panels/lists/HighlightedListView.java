package edu.colorado.phet.website.panels.lists;

import java.util.List;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class HighlightedListView extends ListView {
    public HighlightedListView( String id ) {
        super( id );
    }

    public HighlightedListView( String id, IModel model ) {
        super( id, model );
    }

    public HighlightedListView( String id, List list ) {
        super( id, list );
    }

    protected void populateItem( ListItem item ) {
        if ( item.getIndex() % 2 == 0 ) {
            item.add( new AttributeAppender( "class", new Model( "list-highlight-background" ), " " ) );
        }
    }
}
