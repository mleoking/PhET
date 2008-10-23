/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.alphadecay;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.view.AlphaParticleNode;
import edu.colorado.phet.nuclearphysics.view.LabeledNucleusNode;
import edu.colorado.phet.nuclearphysics.view.NeutronNode;
import edu.colorado.phet.nuclearphysics.view.ProtonNode;
import edu.umd.cs.piccolo.PNode;


/**
 * This class displays a panel that allows the user to select between
 * different types of atomic nuclei for demonstrating Alpha Decay.
 *
 * @author John Blanco
 */
public class AlphaDecayNucleusSelectionPanel extends JPanel {
        
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    // Amount to scale up the particle nodes to make them look reasonable.
    private static final double PARTICLE_SCALE_FACTOR = 8;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private JRadioButton m_poloniumRadioButton;
    private JRadioButton m_customNucleusRadioButton;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public AlphaDecayNucleusSelectionPanel() {
        
        // Add the border around the legend.
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                NuclearPhysicsStrings.NUCLEUS_SELECTION_BORDER_LABEL,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new PhetFont( Font.BOLD, 14 ),
                Color.GRAY );
        
        setBorder( titledBorder );
        
        // Set the layout.
        setLayout( new GridLayout(2, 3) );

        // JPB TBD - Experiement - Create an icon.
        PNode labeledPoloniumNucleus = new LabeledNucleusNode("Polonium Nucleus Small.png",
                NuclearPhysicsStrings.POLONIUM_211_ISOTOPE_NUMBER, 
                NuclearPhysicsStrings.POLONIUM_211_CHEMICAL_SYMBOL, 
                NuclearPhysicsConstants.POLONIUM_LABEL_COLOR );
        
        Image poloniumImage = labeledPoloniumNucleus.toImage();
        ImageIcon poloniumIconImage = new ImageIcon(poloniumImage);

        
        // Create the radio buttons.
        m_poloniumRadioButton = new JRadioButton();
        m_customNucleusRadioButton = new JRadioButton("Custom");
        
        // Group the buttons together and set initial state.
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( m_poloniumRadioButton );
        buttonGroup.add( m_customNucleusRadioButton );
        m_poloniumRadioButton.setSelected( true );
        
        // Add the buttons to the panel.
        add( m_poloniumRadioButton );
        add(new JLabel(poloniumIconImage));
        add(new JLabel( NuclearPhysicsStrings.POLONIUM_LEGEND_LABEL ) );
        add( m_customNucleusRadioButton );
    }
}
