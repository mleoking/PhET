/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.module.naturalselection;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.control.NaturalSelectionControlPanel;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.dialog.generationchart.GenerationChartDialog;
import edu.colorado.phet.naturalselection.dialog.generationchart.GenerationChartCanvas;
import edu.colorado.phet.naturalselection.model.NaturalSelectionClock;

public class NaturalSelectionModule extends PiccoloModule {

    private NaturalSelectionModel model;
    private NaturalSelectionCanvas canvas;
    //private ExampleControlPanel _controlPanel;
    private PiccoloClockControlPanel clockControlPanel;
    private NaturalSelectionControlPanel controlPanel;

    private GenerationChartDialog generationChartDialog;

    private Frame parentFrame;

    public NaturalSelectionModule( Frame parentFrame ) {
        super( NaturalSelectionStrings.TITLE_EXAMPLE_MODULE, new NaturalSelectionClock( NaturalSelectionDefaults.CLOCK_FRAME_RATE, NaturalSelectionDefaults.CLOCK_DT ) );

        this.parentFrame = parentFrame;

        // Model
        NaturalSelectionClock clock = (NaturalSelectionClock) getClock();
        model = new NaturalSelectionModel( clock );

        // Canvas
        canvas = new NaturalSelectionCanvas( model );
        setSimulationPanel( canvas );

        controlPanel = new NaturalSelectionControlPanel( this, model );
        getModulePanel().setClockControlPanelWithoutContainer( controlPanel );


        /*
        clockControlPanel = new PiccoloClockControlPanel( getClock() );
        //clockControlPanel.setRewindButtonVisible( true );
        //clockControlPanel.setTimeDisplayVisible( true );
        
        clockControlPanel.setUnits( NaturalSelectionStrings.UNITS_TIME );
        clockControlPanel.setTimeColumns( ExampleDefaults.CLOCK_TIME_COLUMNS );
        setClockControlPanel( clockControlPanel );
        */

        NaturalSelectionController controller = new NaturalSelectionController( model, canvas, controlPanel, this );

        model.initialize();
    }

    public void reset() {
        controlPanel.reset();
        canvas.reset();
        model.reset();
        if ( generationChartDialog != null ) {
            generationChartDialog.reset();
        } else {
            GenerationChartCanvas.resetType();
        }
    }

    public void showGenerationChart() {
        System.out.println( "Showing generation chart" );
        if ( generationChartDialog == null ) {
            generationChartDialog = new GenerationChartDialog( parentFrame, model );
            generationChartDialog.addWindowListener( new WindowAdapter() {
                // called when the close button in the dialog's window dressing is clicked
                public void windowClosing( WindowEvent e ) {
                    generationChartDialog.dispose();
                }

                // called by JDialog.dispose
                public void windowClosed( WindowEvent e ) {
                    generationChartDialog = null;
                }
            } );
            generationChartDialog.setVisible( true );
        }
    }
}
