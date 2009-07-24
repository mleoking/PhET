package edu.colorado.phet.wickettest.panels;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import edu.colorado.phet.wickettest.SimulationModel;
import edu.colorado.phet.wickettest.WebSimulation;
import edu.colorado.phet.wickettest.content.SimulationPage;
import static edu.colorado.phet.wickettest.util.HtmlUtils.encode;
import edu.colorado.phet.wickettest.util.PhetLink;
import edu.colorado.phet.wickettest.util.SqlUtils;
import edu.colorado.phet.wickettest.util.StaticImage;

public class SimulationDisplayPanel extends PhetPanel {

    public SimulationDisplayPanel( String id, final Locale myLocale ) {
        super( id, myLocale );

        List<SimulationModel> models = SqlUtils.getOrderedSimulationModels( getContext(), getMyLocale() );

        ListView simulationList = new ListView( "simulation-list", models ) {
            protected void populateItem( ListItem item ) {
                WebSimulation simulation = (WebSimulation) ( ( (SimulationModel) ( item.getModel().getObject() ) ).getObject() );
                PhetLink link = SimulationPage.createLink( "simulation-link", getMyLocale(), simulation );
                link.add( new Label( "title", simulation.getTitle() ) );
                link.add( new StaticImage( "thumbnail", simulation.getThumbnailUrl(), MessageFormat.format( "Screenshot of the simulation {0}", encode( simulation.getTitle() ) ) ) );
                item.add( link );
            }
        };
        add( simulationList );

    }

}