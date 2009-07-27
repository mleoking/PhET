package edu.colorado.phet.wickettest.menu;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.wickettest.util.PageContext;

public class NavMenuList extends Panel {

    public NavMenuList( String id, final PageContext context, List<NavLocation> locations ) {
        super( id );

        ListView listView = new ListView( "items", locations ) {
            protected void populateItem( ListItem item ) {
                NavLocation location = (NavLocation) item.getModel().getObject();
                Link link = location.getLink( "link", context );

                Label label = new Label( "link-label", new ResourceModel( "nav." + location.getKey() ) );
                link.add( label );

                item.add( link );

                if ( location.getChildren().isEmpty() ) {
                    Label placeholder = new Label( "children", "BOO" );
                    placeholder.setVisible( false );
                    item.add( placeholder );
                }
                else {
                    item.add( new NavMenuList( "children", context, location.getChildren() ) );
                }

            }
        };
        add( listView );
    }
}
