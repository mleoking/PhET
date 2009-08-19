package edu.colorado.phet.wickettest.content;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.hibernate.Session;

import edu.colorado.phet.wickettest.components.PhetLink;
import edu.colorado.phet.wickettest.data.LocalizedSimulation;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.HibernateTask;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import edu.colorado.phet.wickettest.util.Linkable;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.content.troubleshooting.TroubleshootingMainPanel;

public class OneAtATimePanel extends PhetPanel {
    public OneAtATimePanel( String id, final PageContext context ) {
        super( id, context );

        final List<LocalizedSimulation> simulations = new LinkedList<LocalizedSimulation>();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                List<LocalizedSimulation> sims = HibernateUtils.getAllSimulationsWithLocale( session, context.getLocale() );
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
                Link link = new PhetLink( "download-link", simulation.getDownloadUrl() );
                link.add( new Label( "simulation-title", simulation.getTitle() ) );
                item.add( link );
            }
        } );

        add( TroubleshootingMainPanel.getLinker().getLink( "tech-support-link", context ) );

    }

    public static String getKey() {
        return "get-phet.one-at-a-time";
    }

    public static String getUrl() {
        return "get-phet/one-at-a-time";
    }

    public static Linkable getLinker() {
        return new Linkable() {
            public Link getLink( String id, PageContext context ) {
                return new PhetLink( id, context.getPrefix() + getUrl() );
            }
        };
    }
}