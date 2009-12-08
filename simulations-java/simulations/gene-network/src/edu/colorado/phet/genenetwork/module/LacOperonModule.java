/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.genenetwork.module;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.clock.TimeSpeedSlider;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.genenetwork.GeneNetworkStrings;
import edu.colorado.phet.genenetwork.model.GeneNetworkClock;
import edu.colorado.phet.genenetwork.model.LacOperonModel;
import edu.colorado.phet.genenetwork.view.GeneNetworkCanvas;

/**
 * Module template.
 */
public class LacOperonModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private LacOperonModel model;
    private GeneNetworkCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public LacOperonModule( Frame parentFrame ) {
        super( GeneNetworkStrings.TITLE_LACTOSE_REGULATION, new GeneNetworkClock( LacOperonDefaults.CLOCK_FRAME_RATE, 
        		LacOperonDefaults.CLOCK_DT ) );

        // Model
        GeneNetworkClock clock = (GeneNetworkClock) getClock();
        model = new LacOperonModel( clock );

        // Canvas
        canvas = new GeneNetworkCanvas( model );
        setSimulationPanel( canvas );

        // Turn off the logo panel so that it doesn't take up space.
        setLogoPanel(null);

        // Clock controls
    	PiccoloClockControlPanel clockControlPanel = new PiccoloClockControlPanel( getClock() );
    	final TimeSpeedSlider timeSpeedSlider = new TimeSpeedSlider(LacOperonDefaults.CLOCK_DT / 5, 
    			LacOperonDefaults.CLOCK_DT * 2, "0.00", (ConstantDtClock)getClock(),
    			GeneNetworkStrings.CLOCK_SPEED_CONTROL_CAPTION);
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
        JButton resetButton = new JButton("Reset All");
        resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.reset();
			}
		});
        clockPanelWithResetButton.add(resetButton);
        setClockControlPanel( clockPanelWithResetButton );
        
        // Add the Reset All button.  This is a little bit of a departure from
        // most PhET sims in that it is on the bottom panel rather than on the
        // control panel.
        /*
        JPanel panel = new JPanel();
        panel.add(new JButton("Reset All"));
        setHelpPanel(panel);
        */

        // Help
        if ( hasHelp() ) {
            //XXX add help items
        }

        // Set initial state
        reset();
    }

    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {

        // reset the clock
        GeneNetworkClock clock = model.getClock();
        clock.resetSimulationTime();
    }    
}
