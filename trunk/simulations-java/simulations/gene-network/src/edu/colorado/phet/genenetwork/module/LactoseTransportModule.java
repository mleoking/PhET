// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.module;

import java.awt.Frame;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.common.phetcommon.view.clock.TimeSpeedSlider;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.genenetwork.GeneNetworkStrings;
import edu.colorado.phet.genenetwork.model.GeneNetworkClock;
import edu.colorado.phet.genenetwork.model.LacOperonModelWithLacY;
import edu.colorado.phet.genenetwork.view.GeneNetworkCanvas;
import edu.colorado.phet.genenetwork.view.LactoseTransportCanvas;

/**
 * Module template.
 */
public class LactoseTransportModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private LacOperonModelWithLacY model;
    private GeneNetworkCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public LactoseTransportModule( Frame parentFrame ) {
        super( GeneNetworkStrings.TITLE_LACTOSE_TRANSPORT, new GeneNetworkClock( LacOperonDefaults.CLOCK_FRAME_RATE,
        		LacOperonDefaults.CLOCK_DT ) );

        // Model
        GeneNetworkClock clock = (GeneNetworkClock) getClock();
        model = new LacOperonModelWithLacY( clock, true );

        // Canvas
        canvas = new LactoseTransportCanvas( model );
        setSimulationPanel( canvas );

        // Turn off the logo panel so that it doesn't take up space.
        setLogoPanel(null);

        // Clock controls
    	PiccoloClockControlPanel clockControlPanel = new PiccoloClockControlPanel( getClock() );
    	final TimeSpeedSlider timeSpeedSlider = new TimeSpeedSlider(LacOperonDefaults.CLOCK_DT / 5,
    			LacOperonDefaults.CLOCK_DT * 2, (ConstantDtClock)getClock(), null);
        timeSpeedSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                ((ConstantDtClock)getClock()).setDt( timeSpeedSlider.getValue() );
            }
        } );
    	clockControlPanel.addBetweenTimeDisplayAndButtons(timeSpeedSlider);
        JPanel clockPanelWithResetButton = new JPanel();
        clockPanelWithResetButton.add(clockControlPanel);
        JPanel spacePanel = new JPanel();
        spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.X_AXIS ) );
        spacePanel.add( Box.createHorizontalStrut( 30 ) );
        clockPanelWithResetButton.add(spacePanel);
        clockPanelWithResetButton.add(new ResetAllButton(this, clockPanelWithResetButton));

        setClockControlPanel( clockPanelWithResetButton );

        // Help
        if ( hasHelp() ) {
            //XXX add help items
        }
    }

    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {

        // Reset the clock.
        GeneNetworkClock clock = model.getClock();
        clock.resetSimulationTime();

        // Reset the model.
        model.reset();
    }
}
