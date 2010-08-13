/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.audio.AudioResourcePlayer;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.defaults.RadiometricDecayDefaults;

/**
 * This class is where the model and view classes are created and connected
 * for the portion of the sim that allows the user to date multiple items and
 * guess their ages.
 *
 * @author John Blanco
 */
public class RadioactiveDatingGameModule extends PiccoloModule {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private RadioactiveDatingGameModel _model;
    private RadioactiveDatingGameCanvas _canvas;
    private AudioResourcePlayer audioResourcePlayer = new AudioResourcePlayer(NuclearPhysicsResources.getResourceLoader(), true);
    private PiccoloClockControlPanel _clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public RadioactiveDatingGameModule( Frame parentFrame ) {
        super( NuclearPhysicsStrings.TITLE_RADIOACTIVE_DATING_GAME,
               new NuclearPhysicsClock( RadiometricDecayDefaults.CLOCK_FRAME_RATE, RadiometricDecayDefaults.CLOCK_DT ));
        
        // Add a check box for sound to the clock control panel.
        audioResourcePlayer.setEnabled(true);  // Set sound to be on by default.
        final JCheckBox soundControlCheckBox = new JCheckBox(NuclearPhysicsStrings.SOUND_ENABLED,
        		audioResourcePlayer.isEnabled());
        soundControlCheckBox.setOpaque(false);
        _clockControlPanel.addToLeft(soundControlCheckBox);
        soundControlCheckBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				audioResourcePlayer.setEnabled(soundControlCheckBox.isSelected());
			}
        });
        
        // Physical model
        _model = new RadioactiveDatingGameModel();

        // Canvas
        _canvas = new RadioactiveDatingGameCanvas( _model, audioResourcePlayer );
        setSimulationPanel( _canvas );
        
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
    
	@Override
    protected JComponent createClockControlPanel( IClock clock ) {
		_clockControlPanel = new PiccoloClockControlPanel( clock );
        return _clockControlPanel;
    }
    
    

}
