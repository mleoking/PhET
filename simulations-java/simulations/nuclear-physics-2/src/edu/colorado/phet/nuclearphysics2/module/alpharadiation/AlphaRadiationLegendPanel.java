/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.alpharadiation;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;


/**
 * This class displays the legend for the Alpha Radiation tab.  It simply 
 * displays information and doesn't control anything, so it does not include
 * much in the way of interactive behavior.
 *
 * @author John Blanco
 */
public class AlphaRadiationLegendPanel extends JPanel {
    
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public AlphaRadiationLegendPanel() {
        
        // Add the border around the legend.
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                NuclearPhysics2Resources.getString( "NuclearPhysicsControlPanel.LegendBorder" ),
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new PhetDefaultFont( Font.BOLD, 14 ),
                Color.BLUE );
        
        setBorder( titledBorder );
        
        // Set the layout.
        setLayout( new GridLayout(0, 2) );

        // Add the images and labels that make up the legend.
        
        Image neutronImage = NuclearPhysics2Resources.getImage( "Neutron 001.png");
        ImageIcon neutronImageImageIcon = new ImageIcon(neutronImage.getScaledInstance( 15, -1, Image.SCALE_SMOOTH ));
        add(new JLabel(neutronImageImageIcon));

        add(new JLabel("Neutron"));

        Image protonImage = NuclearPhysics2Resources.getImage( "Proton 001.png");
        ImageIcon protonImageIcon = new ImageIcon(protonImage.getScaledInstance( 15, -1, Image.SCALE_SMOOTH ));
        add(new JLabel(protonImageIcon));

        add(new JLabel("Proton"));

        Image alphaParticleImage = NuclearPhysics2Resources.getImage( "Alpha Particle 001.png");
        ImageIcon alphaParticleImageIcon = 
            new ImageIcon(alphaParticleImage.getScaledInstance( 26, -1, Image.SCALE_SMOOTH ));
        add(new JLabel(alphaParticleImageIcon));

        add(new JLabel("Alpha Particle"));
    }

}
