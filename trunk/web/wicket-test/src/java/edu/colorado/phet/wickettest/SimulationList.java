package edu.colorado.phet.wickettest;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.protocol.http.WebApplication;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.tomcattest.WebSimulation;
import edu.colorado.phet.tomcattest.util.SqlUtils;

public class SimulationList extends WebPage {
    public SimulationList( PageParameters parameters ) {
        ServletContext context = ( (WebApplication) getApplication() ).getServletContext();

        Locale myLocale = LocaleUtils.stringToLocale( (String) parameters.get( "localeString" ) );

        System.out.println( "Loading SimulationList with Locale: " + LocaleUtils.localeToString( myLocale ) );

        List<WebSimulation> simulations = SqlUtils.getSimulationsMatching( context, null, null, myLocale );
        WebSimulation.orderSimulations( simulations, myLocale );

        List<SimulationModel> models = new LinkedList<SimulationModel>();

        for ( WebSimulation simulation : simulations ) {
            models.add( new SimulationModel( simulation ) );
        }

        ListView simulationList = new ListView( "simulation-list", models ) {
            protected void populateItem( ListItem item ) {
                WebSimulation simulation = (WebSimulation) ( ( (SimulationModel) ( item.getModel().getObject() ) ).getObject() );
                item.add( new Label( "title", simulation.getTitle() ) );
            }
        };
        add( simulationList );

    }
}