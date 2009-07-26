package edu.colorado.phet.wickettest.panels;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import edu.colorado.phet.wickettest.SimulationModel;
import edu.colorado.phet.wickettest.WebSimulation;
import edu.colorado.phet.wickettest.data.LocalizedSimulation;
import static edu.colorado.phet.wickettest.util.HtmlUtils.encode;
import edu.colorado.phet.wickettest.util.PhetLink;
import edu.colorado.phet.wickettest.util.PhetPage;
import edu.colorado.phet.wickettest.util.SqlUtils;
import edu.colorado.phet.wickettest.util.StaticImage;

public class SimulationMainPanel extends PhetPanel {

    public SimulationMainPanel( String id, LocalizedSimulation simulation, PhetPage page, final Locale myLocale ) {
        super( id, myLocale );

        add( new Label( "simulation-main-title", simulation.getTitle() ) );

        PhetLink link = new PhetLink( "simulation-main-link-run-main", simulation.getRunUrl() );
        // TODO: localize
        link.add( new StaticImage( "simulation-main-screenshot", simulation.getSimulation().getImageUrl(), MessageFormat.format( "Screenshot of the simulation {0}", encode( simulation.getTitle() ) ) ) );
        add( link );

        add( new Label( "simulation-main-description", simulation.getDescription() ) );

        // TODO: use hibernate for locale list
        List<WebSimulation> simulations = SqlUtils.getSimulationsMatching( getContext(), null, simulation.getSimulation().getName(), null );
        WebSimulation.orderSimulations( simulations, myLocale );

        List<SimulationModel> models = new LinkedList<SimulationModel>();

        for ( WebSimulation sim : simulations ) {
            if ( !sim.getLocale().equals( simulation.getLocale() ) ) {
                models.add( new SimulationModel( sim ) );
            }
        }

        ListView simulationList = new ListView( "simulation-main-translation-list", models ) {
            protected void populateItem( ListItem item ) {
                WebSimulation simulation = (WebSimulation) ( ( (SimulationModel) ( item.getModel().getObject() ) ).getObject() );
                Locale simLocale = simulation.getLocale();
                PhetLink link = new PhetLink( "simulation-main-translation-link", simulation.getRunUrl() );
                link.add( new Label( "simulation-main-translation-locale-name", simLocale.getDisplayName( myLocale ) ) );
                item.add( link );
                item.add( new Label( "simulation-main-translation-locale-translated-name", simLocale.getDisplayName( simLocale ) ) );
                item.add( new Label( "simulation-main-translation-title", simulation.getTitle() ) );
            }
        };
        add( simulationList );

        // so we don't emit an empty <table></table> that isn't XHTML Strict compatible
        if ( models.isEmpty() ) {
            simulationList.setVisible( false );
        }
    }

}