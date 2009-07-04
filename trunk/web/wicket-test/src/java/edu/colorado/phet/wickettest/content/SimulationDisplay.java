package edu.colorado.phet.wickettest.content;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.wickettest.panels.SimulationDisplayPanel;
import edu.colorado.phet.wickettest.util.PhetPage;

public class SimulationDisplay extends PhetPage {
    public SimulationDisplay( PageParameters parameters ) {
        super( parameters );

        Label title = new Label( "page-title", new ResourceModel( "simulationDisplay.simulations" ) );
        add( title );

        add( new SimulationDisplayPanel( "simulation-display-panel", getMyLocale() ) );

    }
}