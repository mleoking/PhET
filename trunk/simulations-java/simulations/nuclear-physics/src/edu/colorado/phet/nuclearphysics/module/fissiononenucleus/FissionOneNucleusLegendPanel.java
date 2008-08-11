/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.fissiononenucleus;

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
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.view.LabeledNucleusNode;
import edu.colorado.phet.nuclearphysics.view.NeutronNode;
import edu.colorado.phet.nuclearphysics.view.ProtonNode;
import edu.umd.cs.piccolo.PNode;


/**
 * This class displays the legend for the Fission: One Nucleus tab.  It simply 
 * displays information and doesn't control anything, so it does not include
 * much in the way of interactive behavior.
 * 
 * @author John Blanco
 */
public class FissionOneNucleusLegendPanel extends JPanel {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    // Amount to scale up the particle nodes to make them look reasonable.
    private static final double PARTICLE_SCALE_FACTOR = 8;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public FissionOneNucleusLegendPanel() {
        
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
        
        // Add the Uranium nucleus to the legend.
        
        PNode labeledUraniumNucleus = new LabeledNucleusNode("Uranium Nucleus Small.png",
                NuclearPhysicsStrings.URANIUM_235_ISOTOPE_NUMBER, 
                NuclearPhysicsStrings.URANIUM_235_CHEMICAL_SYMBOL, 
                NuclearPhysicsConstants.URANIUM_235_LABEL_COLOR );
        
        Image uraniumImage = labeledUraniumNucleus.toImage();
        ImageIcon icon = new ImageIcon(uraniumImage);
        add(new JLabel(icon));
        add(new JLabel( NuclearPhysicsStrings.URANIUM_235_LEGEND_LABEL ) );
        
        // Add the daughter nuclei to the legend.  These are not specifically
        // labeled with chemical symbols because the products from a fission
        // of U-235 can vary.
        
        addLegendItem( "Daughter Nuclei Small.png", NuclearPhysicsStrings.DAUGHTER_NUCLEI_LABEL, 75 );
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
        Image im = NuclearPhysicsResources.getImage( imageName );
        ImageIcon icon = new ImageIcon(im.getScaledInstance( width, -1, Image.SCALE_SMOOTH ));
        add(new JLabel(icon));
        add(new JLabel( label ));
    }
    
    /**
     * An alternative way to add a legend item if the image is already available.
     */
    private void addLegendItem( Image im, String label ) {
        ImageIcon icon = new ImageIcon(im);
        add(new JLabel(icon));
        add(new JLabel( label ));
    }

}
