/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.nuclearreactor;

import java.awt.Color;
import java.awt.Font;

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
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
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
        JPanel spacePanel1 = new JPanel();
        spacePanel1.setLayout( new BoxLayout( spacePanel1, BoxLayout.Y_AXIS ) );
        spacePanel1.add( Box.createVerticalStrut( 20 ) );
        add( spacePanel1 );
        
        // Add the button that allows users to fire neutrons into the reaction area.
        JButton fireNeutronsButton = new JButton(NuclearPhysics2Strings.FIRE_NEUTRONS_BUTTON_LABEL );
        // TODO: JPB TBD - Add listener and action.
        add(fireNeutronsButton);
        
        // Add a bit more spacing.
        JPanel spacePanel2 = new JPanel();
        spacePanel2.setLayout( new BoxLayout( spacePanel2, BoxLayout.Y_AXIS ) );
        spacePanel2.add( Box.createVerticalStrut( 20 ) );
        add( spacePanel2 );
        
        // Add the button that allows the user to reset the reaction.
        JButton resetButton = new JButton(NuclearPhysics2Strings.RESET_BUTTON_LABEL);
        // TODO: JPB TBD - Add listener and action.
        add(resetButton);
    }
}
