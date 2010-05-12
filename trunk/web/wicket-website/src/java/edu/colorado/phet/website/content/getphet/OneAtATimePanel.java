package edu.colorado.phet.website.content.getphet;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.hibernate.Session;

import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.components.RawLink;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingMainPanel;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class OneAtATimePanel extends PhetPanel {
    public OneAtATimePanel( String id, final PageContext context ) {
        super( id, context );

        final List<LocalizedSimulation> simulations = new LinkedList<LocalizedSimulation>();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                List<LocalizedSimulation> sims = HibernateUtils.preferredFullSimulationList( session, context.getLocale() );
                HibernateUtils.orderSimulations( sims, context.getLocale() );
                for ( LocalizedSimulation sim : sims ) {
                    simulations.add( sim );
                }
                return true;
            }
        } );

        add( new ListView( "simulation-list", simulations ) {
            protected void populateItem( ListItem item ) {
                LocalizedSimulation simulation = (LocalizedSimulation) item.getModel().getObject();
                Link link = new RawLink( "download-link", simulation.getDownloadUrl() );
                link.add( new Label( "simulation-title", simulation.getTitle() ) );
                item.add( link );
            }
        } );

        //add( TroubleshootingMainPanel.getLinker().getLink( "tech-support-link", context ) );

        add( new LocalizedText( "running-sims", "get-phet.one-at-a-time.runningSims", new Object[]{
                TroubleshootingMainPanel.getLinker().getHref( context, getPhetCycle() )
        } ) );

    }

    public static String getKey() {
        return "get-phet.one-at-a-time";
    }

    public static String getUrl() {
        return "get-phet/one-at-a-time";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}