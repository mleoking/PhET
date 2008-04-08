/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.fissiononenucleus;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Strings;
import edu.colorado.phet.nuclearphysics2.view.LabeledNucleusNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;


/**
 * This class displays the legend for the Fission: One Nucleus tab.  It simply 
 * displays information and doesn't control anything, so it does not include
 * much in the way of interactive behavior.
 * 
 * @author John Blanco
 */
public class FissionOneNucleusLegendPanel extends JPanel {
    
    private static final float SHADOW_OFFSET = 4.0f;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public FissionOneNucleusLegendPanel() {
        
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

        // Add the images and labels for the simple portion of the legend.
        
        addLegendItem( "Neutron.png", "NuclearPhysicsControlPanel.NeutronLabel", 12 );
        addLegendItem( "Proton.png", "NuclearPhysicsControlPanel.ProtonLabel", 12 );
        
        // Add the Uranium nucleus to the legend.
        
        PNode labeledUraniumNucleus = new LabeledNucleusNode("Uranium Nucleus Small.png",
                NuclearPhysics2Strings.URANIUM_235_ISOTOPE_NUMBER, 
                NuclearPhysics2Strings.URANIUM_235_CHEMICAL_SYMBOL, 
                NuclearPhysics2Constants.URANIUM_LABEL_COLOR );
        
        Image uraniumImage = labeledUraniumNucleus.toImage();
        ImageIcon icon = new ImageIcon(uraniumImage);
        add(new JLabel(icon));
        add(new JLabel( NuclearPhysics2Strings.URANIUM_LEGEND_LABEL ) );
        
        // Add the Barium nucleus to the legend.
        
        PNode labeledBariumNucleus = new LabeledNucleusNode("Barium Nucleus Small.png",
                NuclearPhysics2Strings.BARIUM_141_ISOTOPE_NUMBER, 
                NuclearPhysics2Strings.BARIUM_141_CHEMICAL_SYMBOL, 
                NuclearPhysics2Constants.BARIUM_LABEL_COLOR );
        
        Image bariumImage = labeledBariumNucleus.toImage();
        icon = new ImageIcon(bariumImage);
        add(new JLabel(icon));
        add(new JLabel( NuclearPhysics2Strings.BARIUM_LEGEND_LABEL ) );
        
        // Add the Krypton nucleus to the legend.
        
        PNode labeledKryptonNucleus = new LabeledNucleusNode("Krypton Nucleus Small.png",
                NuclearPhysics2Strings.KRYPTON_92_ISOTOPE_NUMBER, 
                NuclearPhysics2Strings.KRYPTON_92_CHEMICAL_SYMBOL, 
                NuclearPhysics2Constants.KRYPTON_LABEL_COLOR );
        
        Image kryptonImage = labeledKryptonNucleus.toImage();
        icon = new ImageIcon(kryptonImage);
        add(new JLabel(icon));
        add(new JLabel( NuclearPhysics2Strings.KRYPTON_LEGEND_LABEL ) );
        
    }
    
    /**
     * This method adds simple legend items, i.e. those that only include an
     * image and a label, to the legend.
     * 
     * @param imageName
     * @param labelName
     * @param width
     */
    private void addLegendItem( String imageName, String labelName, int width ) {
        Image im = NuclearPhysics2Resources.getImage( imageName );
        ImageIcon icon = new ImageIcon(im.getScaledInstance( width, -1, Image.SCALE_SMOOTH ));
        add(new JLabel(icon));
        add(new JLabel( NuclearPhysics2Resources.getString( labelName ) ));
    }
}
