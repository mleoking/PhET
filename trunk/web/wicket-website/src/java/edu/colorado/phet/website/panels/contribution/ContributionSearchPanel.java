package edu.colorado.phet.website.panels.contribution;

import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.hibernate.Session;

import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;

public class ContributionSearchPanel extends PhetPanel {

    private static Logger logger = Logger.getLogger( ContributionSearchPanel.class.getName() );

    public ContributionSearchPanel( String id, final PageContext context, final PageParameters params ) {
        super( id, context );

        final String[] simStrings = params.getStringArray( "simulations" );

        final LinkedList<LocalizedSimulation> simulations = new LinkedList<LocalizedSimulation>();

        add( HeaderContributor.forCss( "/css/contribution-browse-v1.css" ) );

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                HibernateUtils.addPreferredFullSimulationList( simulations, getHibernateSession(), context.getLocale() );
                HibernateUtils.orderSimulations( simulations, context.getLocale() );
                return true;
            }
        } );

        simulations.add( 0, null ); // will indicate all simulations in the selection panel

        add( new ListView( "simulations", simulations ) {
            protected void populateItem( ListItem item ) {
                LocalizedSimulation lsim = (LocalizedSimulation) item.getModel().getObject();
                String value;
                boolean selected = false;
                if ( lsim == null ) {
                    item.add( new LocalizedText( "simulation-title", "contribution.search.allSimulations" ) );
                    value = "all";
                    if ( simStrings == null || simStrings.length == 0 ) {
                        selected = true;
                    }
                }
                else {
                    item.add( new Label( "simulation-title", lsim.getTitle() ) );
                    item.add( new AttributeModifier( "title", true, new Model( lsim.getTitle() ) ) );
                    value = Integer.toString( lsim.getSimulation().getId() );
                }

                item.add( new AttributeModifier( "value", true, new Model( value ) ) );

                if ( simStrings != null ) {
                    for ( String simString : simStrings ) {
                        if ( simString.equals( value ) ) {
                            selected = true;
                        }
                    }
                }
                if ( selected ) {
                    item.add( new AttributeModifier( "selected", true, new Model( "selected" ) ) );
                }
            }
        } );

    }

}