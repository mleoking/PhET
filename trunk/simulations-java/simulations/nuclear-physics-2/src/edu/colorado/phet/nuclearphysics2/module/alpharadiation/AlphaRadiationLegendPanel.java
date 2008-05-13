/* Copyright 2008, University of Colorado */

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

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Strings;
import edu.colorado.phet.nuclearphysics2.view.LabeledNucleusNode;
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
                new PhetFont( Font.BOLD, 14 ),
                Color.GRAY );
        
        setBorder( titledBorder );
        
        // Set the layout.
        setLayout( new GridLayout(0, 2) );

        // Add the images and labels for the simple portion of the legend.
        
        addLegendItem( "Neutron.png", NuclearPhysics2Strings.NEUTRON_LEGEND_LABEL, 12 );
        addLegendItem( "Proton.png", NuclearPhysics2Strings.PROTON_LEGEND_LABEL, 12 );
        addLegendItem( "Alpha Particle 001.png", NuclearPhysics2Strings.ALPHA_PARTICLE_LEGEND_LABEL, 20 );
        
        // Add the Polonium nucleus to the legend.
        
        PNode labeledPoloniumNucleus = new LabeledNucleusNode("Polonium Nucleus Small.png",
                NuclearPhysics2Strings.POLONIUM_211_ISOTOPE_NUMBER, 
                NuclearPhysics2Strings.POLONIUM_211_CHEMICAL_SYMBOL, 
                NuclearPhysics2Constants.POLONIUM_LABEL_COLOR );
        
        Image poloniumImage = labeledPoloniumNucleus.toImage();
        ImageIcon icon = new ImageIcon(poloniumImage);
        add(new JLabel(icon));
        add(new JLabel( NuclearPhysics2Strings.POLONIUM_LEGEND_LABEL ) );
        
        // Add the Lead nucleus to the legend.
        
        PNode labeledLeadNucleus = new LabeledNucleusNode("Lead Nucleus Small.png",
                NuclearPhysics2Strings.LEAD_207_ISOTOPE_NUMBER, 
                NuclearPhysics2Strings.LEAD_207_CHEMICAL_SYMBOL, 
                NuclearPhysics2Constants.LEAD_LABEL_COLOR );
        
        Image leadImage = labeledLeadNucleus.toImage();
        icon = new ImageIcon(leadImage);
        add(new JLabel(icon));
        add(new JLabel( NuclearPhysics2Strings.LEAD_LEGEND_LABEL ) );
    }
    
    /**
     * This method adds simple legend items, i.e. those that only include an
     * image and a label, to the legend.
     * 
     * @param imageName
     * @param labelName
     * @param width
     */
    private void addLegendItem( String imageName, String label, int width ) {
        Image im = NuclearPhysics2Resources.getImage( imageName );
        ImageIcon icon = new ImageIcon(im.getScaledInstance( width, -1, Image.SCALE_SMOOTH ));
        add(new JLabel(icon));
        add(new JLabel( label ));
    }
}
