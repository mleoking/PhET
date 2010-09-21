package edu.colorado.phet.website.menu;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.components.RawLabel;
import edu.colorado.phet.website.components.VisListView;
import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class NavMenuList extends PhetPanel {

    public NavMenuList( String id, final PageContext context, List<NavLocation> locations, final Collection<NavLocation> currentLocations, final int level ) {
        super( id, context );

        add( new VisListView<NavLocation>( "items", locations ) {
            protected void populateItem( ListItem item ) {
                NavLocation location = (NavLocation) item.getModel().getObject();
                Link link = location.getLink( "link", context, (PhetRequestCycle) getRequestCycle() );

                RawLabel label = new RawLabel( "link-label", new ResourceModel( location.getLocalizationKey() ) );

                boolean open = false;

                if ( currentLocations != null ) {
                    for ( NavLocation currentLocation : currentLocations ) {
                        if ( !open ) {
                            open = currentLocation.isUnderLocation( location );
                        }
                        if ( currentLocation.getBaseKey().equals( location.getKey() ) || currentLocation.getKey().equals( location.getKey() ) ) {
                            label.add( new AttributeAppender( "class", new Model<String>( "selected" ), " " ) );
                        }
                    }
                }

                // adds class so we can remove these for things like the installer and whatnot in CSS
                if ( location.isUnderLocationKey( "get-phet" ) ) {
                    label.add( new AttributeAppender( "class", new Model<String>( "get-phet-item" ), " " ) );
                }
                if ( location.isUnderLocationKey( "teacherIdeas" ) ) {
                    label.add( new AttributeAppender( "class", new Model<String>( "teacher-ideas-item" ), " " ) );
                    if ( !DistributionHandler.displayContributions( getPhetCycle() ) ) {
                        item.setVisible( false );
                    }
                }
                if ( location.isUnderLocationKey( "forTranslators.website" ) ) {
                    if ( DistributionHandler.hideWebsiteTranslations( getPhetCycle() ) ) {
                        item.setVisible( false );
                    }
                }

                link.add( label );

                item.add( link );

                if ( location.getChildren().isEmpty() || !open ) {
                    Label placeholder = new Label( "children", "BOO" );
                    placeholder.setVisible( false );
                    item.add( placeholder );
                }
                else {
                    NavMenuList children = new NavMenuList( "children", context, location.getVisibleChildren( currentLocations ), currentLocations, level + 1 );
                    children.setRenderBodyOnly( true );
                    item.add( children );
                }

                link.add( new AttributeAppender( "class", new Model<String>( "nav" + level ), " " ) );

            }
        } );

        add( HeaderContributor.forCss( CSS.NAV_MENU ) );
    }
}
