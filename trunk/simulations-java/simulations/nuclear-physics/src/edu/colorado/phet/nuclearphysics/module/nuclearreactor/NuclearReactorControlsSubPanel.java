/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.nuclearreactor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;

/**
 * This class defines a subpanel that goes on the main control panel for the
 * nuclear reactor tab and allows the user to enable a graph, fire neutrons,
 * and reset the reaction.
 *
 * @author John Blanco
 */
public class NuclearReactorControlsSubPanel extends VerticalLayoutPanel {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    private ArrayList _listeners;
    private NuclearReactorModel _model;
    private final JCheckBox _energyGraphsCheckBox;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public NuclearReactorControlsSubPanel(Frame parentFrame, NuclearReactorModel model) {
        
        _model = model;
        
        // Perform local initialization.
        _listeners = new ArrayList();
        
        // Add the border around the sub panel.
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                NuclearPhysicsStrings.CONTROLS_BORDER,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new PhetFont( Font.BOLD, 14 ),
                Color.GRAY );
        
        setBorder( titledBorder );
        
        addSpace(5);
        
        // Add the check box for the energy graphs.
        _energyGraphsCheckBox = new JCheckBox( NuclearPhysicsStrings.ENERGY_GRAPHS_CHECK_BOX );
        _energyGraphsCheckBox.setSelected( true );
        _energyGraphsCheckBox.addActionListener( new ActionListener() {
            
            public void actionPerformed( ActionEvent e) {
                notifyParameterChanged();
            }
        } );
        _energyGraphsCheckBox.setBorder( BorderFactory.createEtchedBorder() );
        add(_energyGraphsCheckBox);
        
        // Add a little spacing in order to make the controls easier to spot.
        addSpace( 20 );
        
        // Turn off the fill so the buttons don't get stretched.
        setFillNone();
        
        // Add the button that allows users to fire neutrons into the reaction area.
        JButton fireNeutronsButton = new JButton(NuclearPhysicsStrings.FIRE_NEUTRONS_BUTTON_LABEL );
        fireNeutronsButton.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
                _model.fireNeutrons();
            }
        });
        add(fireNeutronsButton);
        
        // Add a bit more spacing.
        addSpace( 10 );
        
        // Add the button that allows the user to reset the reaction.
        JButton resetButton = new JButton(NuclearPhysicsStrings.RESET_BUTTON_LABEL);
        resetButton.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
                // Reset the sim time, which will in turn reset the model.
                _model.getClock().resetSimulationTime();
            }
        });
        add(resetButton);
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------

    public boolean getEnergyGraphCheckBoxState(){
        return _energyGraphsCheckBox.isSelected();
    }
    
    //------------------------------------------------------------------------
    // Other Public Methods
    //------------------------------------------------------------------------
    
    public void addListener(Listener listener){

        if (_listeners.contains( listener ))
        {
            // Don't bother re-adding.
            return;
        }
        
        _listeners.add( listener );
    }

    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
    /**
     * Add space to the panel, generally used to create space between other
     * components added to the panel.
     */
    private void addSpace(int space){
        JPanel spacePanel = new JPanel();
        spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.Y_AXIS ) );
        spacePanel.add( Box.createVerticalStrut( space ) );
        add( spacePanel );
    }
    
    /**
     * Notify listeners that a control parameter has changed.
     */
    private void notifyParameterChanged(){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get(i)).parameterChanged();
        }
    }
    
    //------------------------------------------------------------------------
    // Inner Interfaces
    //------------------------------------------------------------------------

    /**
     * This listener interface allows listeners to get notified when the user
     * changes something on this control panel.
     */
    public static interface Listener {
        /**
         * This informs the listener that the state of some potentially
         * important parameter has changed.
         */
        public void parameterChanged();
    }
}
