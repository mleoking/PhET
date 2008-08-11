/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.alpharadiation;

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

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.view.AlphaParticleNode;
import edu.colorado.phet.nuclearphysics.view.LabeledNucleusNode;
import edu.colorado.phet.nuclearphysics.view.NeutronNode;
import edu.colorado.phet.nuclearphysics.view.ProtonNode;
import edu.umd.cs.piccolo.PNode;


/**
 * This class displays the legend for the Alpha Radiation tab.  It simply 
 * displays information and doesn't control anything, so it does not include
 * much in the way of interactive behavior.
 *
 * @author John Blanco
 */
public class AlphaRadiationLegendPanel extends JPanel {
        
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    // Amount to scale up the particle nodes to make them look reasonable.
    private static final double PARTICLE_SCALE_FACTOR = 8;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public AlphaRadiationLegendPanel() {
        
        // Add the border around the legend.
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                NuclearPhysicsStrings.LEGEND_BORDER_LABEL,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new PhetFont( Font.BOLD, 14 ),
                Color.GRAY );
        
        setBorder( titledBorder );
        
        // Set the layout.
        setLayout( new GridLayout(0, 2) );

        // Add the images and labels for the simple portion of the legend.
        
        PNode neutron = new NeutronNode();
        neutron.scale( PARTICLE_SCALE_FACTOR );
        addLegendItem( neutron.toImage(), NuclearPhysicsStrings.NEUTRON_LEGEND_LABEL ); 
        PNode proton = new ProtonNode();
        proton.scale( PARTICLE_SCALE_FACTOR );
        addLegendItem( proton.toImage(), NuclearPhysicsStrings.PROTON_LEGEND_LABEL ); 
        PNode alphaParticle = new AlphaParticleNode();
        alphaParticle.scale( PARTICLE_SCALE_FACTOR );
        addLegendItem( alphaParticle.toImage(), NuclearPhysicsStrings.ALPHA_PARTICLE_LEGEND_LABEL );
        
        // Add the Polonium nucleus to the legend.
        
        PNode labeledPoloniumNucleus = new LabeledNucleusNode("Polonium Nucleus Small.png",
                NuclearPhysicsStrings.POLONIUM_211_ISOTOPE_NUMBER, 
                NuclearPhysicsStrings.POLONIUM_211_CHEMICAL_SYMBOL, 
                NuclearPhysicsConstants.POLONIUM_LABEL_COLOR );
        
        Image poloniumImage = labeledPoloniumNucleus.toImage();
        ImageIcon icon = new ImageIcon(poloniumImage);
        add(new JLabel(icon));
        add(new JLabel( NuclearPhysicsStrings.POLONIUM_LEGEND_LABEL ) );
        
        // Add the Lead nucleus to the legend.
        
        PNode labeledLeadNucleus = new LabeledNucleusNode("Lead Nucleus Small.png",
                NuclearPhysicsStrings.LEAD_207_ISOTOPE_NUMBER, 
                NuclearPhysicsStrings.LEAD_207_CHEMICAL_SYMBOL, 
                NuclearPhysicsConstants.LEAD_LABEL_COLOR );
        
        Image leadImage = labeledLeadNucleus.toImage();
        icon = new ImageIcon(leadImage);
        add(new JLabel(icon));
        add(new JLabel( NuclearPhysicsStrings.LEAD_LEGEND_LABEL ) );
    }
    
    /**
     * This method adds simple legend items, i.e. those that only include an
     * image and a label, to the legend.
     */
    private void addLegendItem( Image im, String label ) {
        ImageIcon icon = new ImageIcon(im);
        add(new JLabel(icon));
        add(new JLabel( label ));
    }
}
