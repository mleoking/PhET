package edu.colorado.phet.wickettest.panels;

import java.util.List;
import java.util.Locale;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import edu.colorado.phet.wickettest.SimulationModel;
import edu.colorado.phet.wickettest.WebSimulation;
import edu.colorado.phet.wickettest.content.SimulationPage;
import edu.colorado.phet.wickettest.util.PhetLink;
import edu.colorado.phet.wickettest.util.SqlUtils;

public class SimulationListPanel extends PhetPanel {

    public SimulationListPanel( String id, final Locale myLocale ) {
        super( id, myLocale );

        List<SimulationModel> models = SqlUtils.getOrderedSimulationModels( getContext(), myLocale );

        ListView simulationList = new ListView( "simulation-list", models ) {
            protected void populateItem( ListItem item ) {
                WebSimulation simulation = (WebSimulation) ( ( (SimulationModel) ( item.getModel().getObject() ) ).getObject() );

                Label title = new Label( "title", simulation.getTitle() );
                PhetLink link = SimulationPage.createLink( "simulation-link", getMyLocale(), simulation );
                link.add( title );
                item.add( link );
            }
        };
        add( simulationList );

    }

}