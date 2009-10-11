package edu.colorado.phet.naturalselection.module;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.control.NaturalSelectionControlPanel;
import edu.colorado.phet.naturalselection.dialog.BunniesTakeOverDialog;
import edu.colorado.phet.naturalselection.dialog.GameOverDialog;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.model.NaturalSelectionClock;
import edu.colorado.phet.naturalselection.model.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.persistence.NaturalSelectionConfig;
import edu.colorado.phet.naturalselection.view.NaturalSelectionCanvas;

/**
 * The natural selection module
 *
 * @author Jonathan Olson
 */
public class NaturalSelectionModule extends PiccoloModule {

    private NaturalSelectionModel model;
    private NaturalSelectionCanvas canvas;
    private NaturalSelectionControlPanel controlPanel;
    private GameOverDialog gameOverDialog;
    private BunniesTakeOverDialog bunniesTakeOverDialog;

    private Frame parentFrame;

    private static NaturalSelectionModule module;

    public NaturalSelectionModule( Frame parentFrame ) {
        super( NaturalSelectionStrings.TITLE_NATURAL_SELECTION_MODULE, new NaturalSelectionClock( NaturalSelectionConstants.getSettings().getClockFrameRate(), NaturalSelectionConstants.getSettings().getClockDT() ) );

        module = this;
        this.parentFrame = parentFrame;

        // Model
        NaturalSelectionClock clock = (NaturalSelectionClock) getClock();
        model = new NaturalSelectionModel( clock );

        // Canvas
        canvas = new NaturalSelectionCanvas( model );
        setSimulationPanel( canvas );

        // control panel
        controlPanel = new NaturalSelectionControlPanel( this );
        getModulePanel().setClockControlPanelWithoutContainer( controlPanel );

        // controller
        NaturalSelectionController controller = new NaturalSelectionController( model, canvas, controlPanel, this );

        model.initialize();
    }

    /**
     * Reset the entire module
     */
    public void reset() {
        Bunny.bunnyCount = 0;
        controlPanel.reset();
        canvas.reset();
        model.reset();
        gameOverDialog = null;
    }

    public void save( NaturalSelectionConfig config ) {
        model.save( config );
        controlPanel.save( config );
    }

    public void load( NaturalSelectionConfig config ) {
        reset();
        model.load( config );
        controlPanel.load( config );
        canvas.load( config );
    }

    //----------------------------------------------------------------------------
    // Dialogs
    //----------------------------------------------------------------------------

    public void showGameOver() {
        if ( gameOverDialog == null ) {
            gameOverDialog = new GameOverDialog( parentFrame, this );
            SwingUtils.centerDialogInParent( gameOverDialog );
            gameOverDialog.addWindowListener( new WindowAdapter() {
                // called when the close button in the dialog's window dressing is clicked
                public void windowClosing( WindowEvent e ) {
                    gameOverDialog.dispose();
                }

                // called by JDialog.dispose
                public void windowClosed( WindowEvent e ) {
                    gameOverDialog = null;
                }
            } );
            gameOverDialog.setVisible( true );
        }
    }

    public void showBunniesTakeOver() {
        if ( bunniesTakeOverDialog == null ) {
            bunniesTakeOverDialog = new BunniesTakeOverDialog( parentFrame, this );
            SwingUtils.centerDialogInParent( bunniesTakeOverDialog );
            bunniesTakeOverDialog.addWindowListener( new WindowAdapter() {
                // called when the close button in the dialog's window dressing is clicked
                public void windowClosing( WindowEvent e ) {
                    bunniesTakeOverDialog.dispose();
                }

                // called by JDialog.dispose
                public void windowClosed( WindowEvent e ) {
                    bunniesTakeOverDialog = null;
                }
            } );
            bunniesTakeOverDialog.setVisible( true );
        }
    }

    //----------------------------------------------------------------------------
    // Getters
    //----------------------------------------------------------------------------

    public NaturalSelectionModel getMyModel() {
        return model;
    }

    public NaturalSelectionCanvas getMyCanvas() {
        return canvas;
    }

    public NaturalSelectionControlPanel getMyControlPanel() {
        return controlPanel;
    }

    public Frame getParentFrame() {
        return parentFrame;
    }

    public static NaturalSelectionModule getModule() {
        return module;
    }
}
