/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.neuron.controlpanel;

import java.awt.Color;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.neuron.NeuronResources;
import edu.colorado.phet.neuron.model.AxonModel;
import edu.colorado.phet.neuron.model.SodiumLeakageChannel;
import edu.colorado.phet.neuron.module.MembraneDiffusionModule;
import edu.colorado.phet.neuron.view.MembraneChannelNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Control panel for the neuron sim.
 *
 * @author John Blanco
 */
public class NeuronControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     * @param parentFrame parent frame, for creating dialogs
     */
    public NeuronControlPanel( MembraneDiffusionModule module, Frame parentFrame, AxonModel model ) {
        super();
        
        // Set the control panel's minimum width.
        int minimumWidth = NeuronResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
//        LeakChannelSlider sodiumLeakChannelControl = new LeakChannelSlider("Sodium Leak Channels", 
//        		new MembraneChannelNode(new SodiumLeakageChannel(), new ModelViewTransform2D()));
//        addControlFullWidth(sodiumLeakChannelControl);
        
        // Layout
        {
            addResetAllButton( module );
        }
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void closeAllDialogs() {
        //XXX close any dialogs created via the control panel
    }
    
    private static class LeakChannelSlider extends LinearValueControl{
    	
        public LeakChannelSlider(String title, PNode icon) {
            super( 0, 5, title, "0", "");
            setUpDownArrowDelta( 1 );
            setTextFieldEditable( false );
            setTickPattern( "0" );
            setMajorTickSpacing( 1 );
            setMinorTicksVisible(false);
            setBorder( BorderFactory.createEtchedBorder() );
            setValueLabelIcon(new ImageIcon(icon.toImage(50, 50, new Color(0,0,0,0))));
//            setValue( _model.getNumU235Nuclei() );
//            addChangeListener( new ChangeListener() {
//
//    			public void stateChanged( ChangeEvent e ) {
//                    if (_ignoreStateChanges == false){
//                    	// Set the new value for U235 nuclei.
//                        _model.setNumU235Nuclei( (int)Math.round(getValue()) );
//                    }
//                }
//            });
		}
        
    }
}
