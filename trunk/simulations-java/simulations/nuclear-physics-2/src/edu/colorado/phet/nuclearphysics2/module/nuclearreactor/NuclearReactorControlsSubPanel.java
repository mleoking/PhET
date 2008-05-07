/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.nuclearreactor;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Strings;

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
    private NuclearReactorModel _model;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public NuclearReactorControlsSubPanel(NuclearReactorModel model) {
        
        _model = model;
        
        // Add the border around the sub panel.
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                NuclearPhysics2Strings.CONTROLS_BORDER,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new PhetDefaultFont( Font.BOLD, 14 ),
                Color.GRAY );
        
        setBorder( titledBorder );
        
        addSpace(5);
        
        // Add the check box for the energy graphs.
        final JCheckBox energyGraphsCheckBox = new JCheckBox( NuclearPhysics2Strings.ENERGY_GRAPHS_CHECK_BOX );
        energyGraphsCheckBox.setSelected( false );
        energyGraphsCheckBox.addChangeListener( new ChangeListener() {
            
            public void stateChanged( ChangeEvent e ) {
                // TODO: JPB TBD - Need to fill this in.
            }
        } );
        energyGraphsCheckBox.setBorder( BorderFactory.createEtchedBorder() );
        add(energyGraphsCheckBox);
        
        // Add a little spacing in order to make the controls easier to spot.
        addSpace( 20 );
        
        // Turn off the fill so the buttons don't get stretched.
        setFillNone();
        
        // Add the button that allows users to fire neutrons into the reaction area.
        JButton fireNeutronsButton = new JButton(NuclearPhysics2Strings.FIRE_NEUTRONS_BUTTON_LABEL );
        fireNeutronsButton.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
                _model.fireNeutrons();
            }
        });
        add(fireNeutronsButton);
        
        // Add a bit more spacing.
        addSpace( 10 );
        
        // Add the button that allows the user to reset the reaction.
        JButton resetButton = new JButton(NuclearPhysics2Strings.RESET_BUTTON_LABEL);
        resetButton.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
                // Reset the sim time, which will in turn reset the model.
                _model.getClock().resetSimulationTime();
            }
        });
        add(resetButton);
    }
    
    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
    private void addSpace(int space){
        JPanel spacePanel = new JPanel();
        spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.Y_AXIS ) );
        spacePanel.add( Box.createVerticalStrut( space ) );
        add( spacePanel );
    }
}
