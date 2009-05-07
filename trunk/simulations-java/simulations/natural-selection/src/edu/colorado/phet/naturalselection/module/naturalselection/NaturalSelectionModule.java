/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.module.naturalselection;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.control.NaturalSelectionControlPanel;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.dialog.BunnyStatsDialog;
import edu.colorado.phet.naturalselection.model.NaturalSelectionClock;

/**
 * The natural selection module
 *
 * @author Jonathan Olson
 */
public class NaturalSelectionModule extends PiccoloModule {

    private NaturalSelectionModel model;
    private NaturalSelectionCanvas canvas;
    private NaturalSelectionControlPanel controlPanel;
    private BunnyStatsDialog bunnyStatsDialog;

    private Frame parentFrame;

    public NaturalSelectionModule( Frame parentFrame ) {
        super( NaturalSelectionStrings.TITLE_NATURAL_SELECTION_MODULE, new NaturalSelectionClock( NaturalSelectionDefaults.CLOCK_FRAME_RATE, NaturalSelectionDefaults.CLOCK_DT ) );

        this.parentFrame = parentFrame;

        // Model
        NaturalSelectionClock clock = (NaturalSelectionClock) getClock();
        model = new NaturalSelectionModel( clock );

        // Canvas
        canvas = new NaturalSelectionCanvas( model );
        setSimulationPanel( canvas );

        // control panel
        controlPanel = new NaturalSelectionControlPanel( this, model );
        getModulePanel().setClockControlPanelWithoutContainer( controlPanel );

        // controller
        NaturalSelectionController controller = new NaturalSelectionController( model, canvas, controlPanel, this );

        model.initialize();

        //canvas.showFrenzy( 4000 );
    }

    /**
     * Reset the entire module
     */
    public void reset() {
        controlPanel.reset();
        canvas.reset();
        model.reset();
    }

    /**
     * Shows the generation chart dialog
     */
    public void showGenerationChart() {
        System.out.println( "Showing bunny stats" );
        if ( bunnyStatsDialog == null ) {
            bunnyStatsDialog = new BunnyStatsDialog( parentFrame, model );
            SwingUtils.centerDialogInParent( bunnyStatsDialog );
            bunnyStatsDialog.addWindowListener( new WindowAdapter() {
                // called when the close button in the dialog's window dressing is clicked
                public void windowClosing( WindowEvent e ) {
                    bunnyStatsDialog.dispose();
                }

                // called by JDialog.dispose
                public void windowClosed( WindowEvent e ) {
                    bunnyStatsDialog = null;
                }
            } );
            bunnyStatsDialog.setVisible( true );
        }
    }

    public NaturalSelectionModel getMyModel() {
        return model;
    }

    public NaturalSelectionCanvas getMyCanvas() {
        return canvas;
    }

    public NaturalSelectionControlPanel getMyControlPanel() {
        return controlPanel;
    }

    public BunnyStatsDialog getMyBunnyStatsDialog() {
        return bunnyStatsDialog;
    }
}
