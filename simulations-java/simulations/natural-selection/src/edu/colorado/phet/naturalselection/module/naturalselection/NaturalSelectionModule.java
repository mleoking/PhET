package edu.colorado.phet.naturalselection.module.naturalselection;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.defaults.ExampleDefaults;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.model.NaturalSelectionClock;

public class NaturalSelectionModule extends PiccoloModule {

    private NaturalSelectionModel model;
    private NaturalSelectionCanvas canvas;
    //private ExampleControlPanel _controlPanel;
    private PiccoloClockControlPanel clockControlPanel;
    private NaturalSelectionControlPanel controlPanel;

    public NaturalSelectionModule( Frame parentFrame ) {
        super( NaturalSelectionStrings.TITLE_EXAMPLE_MODULE, new NaturalSelectionClock( NaturalSelectionDefaults.CLOCK_FRAME_RATE, NaturalSelectionDefaults.CLOCK_DT ) );

        // Model
        NaturalSelectionClock clock = (NaturalSelectionClock) getClock();
        model = new NaturalSelectionModel( clock );

        // Canvas
        canvas = new NaturalSelectionCanvas( model );
        setSimulationPanel( canvas );

        controlPanel = new NaturalSelectionControlPanel();
        getModulePanel().setClockControlPanelWithoutContainer( controlPanel );


        /*
        clockControlPanel = new PiccoloClockControlPanel( getClock() );
        //clockControlPanel.setRewindButtonVisible( true );
        //clockControlPanel.setTimeDisplayVisible( true );
        
        clockControlPanel.setUnits( NaturalSelectionStrings.UNITS_TIME );
        clockControlPanel.setTimeColumns( ExampleDefaults.CLOCK_TIME_COLUMNS );
        setClockControlPanel( clockControlPanel );
        */

        // Controller
        // TODO: add in controller
        //ExampleController controller = new ExampleController( model, canvas, _controlPanel );
    }

    public void reset() {

    }
}
