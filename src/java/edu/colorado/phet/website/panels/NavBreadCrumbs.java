package edu.colorado.phet.website.panels;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.website.components.RawLabel;
import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.menu.NavLocation;
import edu.colorado.phet.website.util.PageContext;

public class NavBreadCrumbs extends PhetPanel {
    public NavBreadCrumbs( String id, final PageContext context, NavLocation location ) {
        super( id, context );

        NavLocation base = null;
        List<NavLocation> locations = new LinkedList<NavLocation>();

        for ( NavLocation loc = location; loc != null; loc = loc.getParent() ) {
            if ( loc.getParent() != null ) {
                locations.add( 0, loc );
            }
            else {
                base = loc;
            }
        }

        if ( base == null ) {
            throw new RuntimeException( "BreadCrumbs failure!" );
        }
        else {
            Link baseLink = base.getLink( "base-link", context, getPhetCycle() );
            baseLink.add( new RawLabel( "base-label", new ResourceModel( base.getLocalizationKey() ) ) );
            add( baseLink );

            ListView listView = new ListView( "more-crumbs", locations ) {

                protected void populateItem( ListItem item ) {
                    NavLocation location = (NavLocation) item.getModel().getObject();
                    Link link = location.getLink( "crumb-link", context, getPhetCycle() );
                    link.add( new RawLabel( "crumb-label", new ResourceModel( location.getLocalizationKey() ) ) );
                    item.add( link );
                }
            };
            add( listView );

            add( HeaderContributor.forCss( CSS.NAV_BREADCRUMBS ) );
        }
    }
}
