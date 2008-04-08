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
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Strings;


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
        
        // Now we need to add the legend entries for the various atomic nuclei
        // that appear in this portion of the simulation.  This is a bit
        // tricky because we need to put the label on to the graphic and then
        // display it.

        // Get the image for the nucleus.
        BufferedImage im = NuclearPhysics2Resources.getImage( "Uranium Nucleus Small.png" );
        
        // Get a Graphics2D and set up the strings and fonts that we will need.
        Graphics2D g2 = im.createGraphics();
        String isotopeNumber = NuclearPhysics2Strings.URANIUM_235_ISOTOPE_NUMBER;
        String chemicalSymbol = NuclearPhysics2Strings.URANIUM_235_CHEMICAL_SYMBOL;
        Font superscriptFont = new PhetDefaultFont( Font.PLAIN, 72);
        Font symbolFont = new PhetDefaultFont( Font.PLAIN, 100);
        
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        // Draw the shadow for the superscript.  We use shadows to enhance
        // the readability.
        g2.setFont(superscriptFont);
        g2.setPaint( Color.BLACK );
        TextLayout superscriptTextLayout = 
            new TextLayout(isotopeNumber, superscriptFont, g2.getFontRenderContext());
        superscriptTextLayout.draw( g2, SHADOW_OFFSET, superscriptTextLayout.getAscent()+SHADOW_OFFSET);
        
        // Draw the main text of the superscript.
        g2.setPaint( NuclearPhysics2Constants.URANIUM_LABEL_COLOR );
        superscriptTextLayout.draw( g2, 0, superscriptTextLayout.getAscent());
        
        // Draw the shadow for the chemical symbol.
        g2.setPaint( Color.BLACK );
        TextLayout symbolTextLayout = 
            new TextLayout(chemicalSymbol, symbolFont, g2.getFontRenderContext());
        symbolTextLayout.draw( g2, (float)superscriptTextLayout.getBounds().getWidth()+SHADOW_OFFSET,
                symbolTextLayout.getAscent()+SHADOW_OFFSET);
        
        // Draw the main text of the chemical symbol.
        g2.setPaint( NuclearPhysics2Constants.URANIUM_LABEL_COLOR );
        symbolTextLayout.draw( g2, (float)superscriptTextLayout.getBounds().getWidth(), symbolTextLayout.getAscent());

        g2.dispose();

        // Add the new image and the associated label to the legend.
        ImageIcon icon = new ImageIcon(im.getScaledInstance( 50, -1, Image.SCALE_SMOOTH ));
        add(new JLabel(icon));
        add(new JLabel( NuclearPhysics2Strings.URANIUM_LEGEND_LABEL ) );
        
        // Now add the graphic and label for the Barium nucleus.
        
        // Get the image for the nucleus.
        im = NuclearPhysics2Resources.getImage( "Barium Nucleus Small.png" );
        
        // Get a Graphics2D and set up the strings and fonts that we will need.
        g2 = im.createGraphics();
        isotopeNumber = NuclearPhysics2Strings.BARIUM_141_ISOTOPE_NUMBER;
        chemicalSymbol = NuclearPhysics2Strings.BARIUM_141_CHEMICAL_SYMBOL;
        superscriptFont = new PhetDefaultFont( Font.PLAIN, 64);
        symbolFont = new PhetDefaultFont( Font.PLAIN, 95);
        
        // Draw the shadow for the superscript.  Again, We use shadows to
        // enhance the readability.
        g2.setFont(superscriptFont);
        g2.setPaint( Color.BLACK );
        superscriptTextLayout = 
            new TextLayout(isotopeNumber, superscriptFont, g2.getFontRenderContext());
        superscriptTextLayout.draw( g2, SHADOW_OFFSET, superscriptTextLayout.getAscent()+SHADOW_OFFSET);
        
        // Draw the main text of the superscript.
        g2.setPaint( NuclearPhysics2Constants.BARIUM_LABEL_COLOR );
        superscriptTextLayout.draw( g2, 0, superscriptTextLayout.getAscent());
        
        // Draw the shadow for the chemical symbol.
        g2.setPaint( Color.BLACK );
        symbolTextLayout = new TextLayout(chemicalSymbol, symbolFont, g2.getFontRenderContext());
        symbolTextLayout.draw( g2, (float)superscriptTextLayout.getBounds().getWidth()+SHADOW_OFFSET, 
                symbolTextLayout.getAscent()+SHADOW_OFFSET);
        
        // Draw the main text of the chemical symbol.
        g2.setPaint( NuclearPhysics2Constants.BARIUM_LABEL_COLOR );
        symbolTextLayout.draw( g2, (float)superscriptTextLayout.getBounds().getWidth(), symbolTextLayout.getAscent());

        g2.dispose();

        // Add the new image and the associated label to the legend.
        icon = new ImageIcon(im.getScaledInstance( 40, -1, Image.SCALE_SMOOTH ));
        add(new JLabel(icon));
        add(new JLabel( NuclearPhysics2Strings.BARIUM_LEGEND_LABEL ));

        // Now add the graphic and label for the Krypton nucleus.
        
        // Get the image for the nucleus.
        im = NuclearPhysics2Resources.getImage( "Krypton Nucleus Small.png" );
        
        // Get a Graphics2D and set up the strings and fonts that we will need.
        g2 = im.createGraphics();
        isotopeNumber = NuclearPhysics2Strings.KRYPTON_92_ISOTOPE_NUMBER;
        chemicalSymbol = NuclearPhysics2Strings.KRYPTON_92_CHEMICAL_SYMBOL;
        superscriptFont = new PhetDefaultFont( Font.PLAIN, 64);
        symbolFont = new PhetDefaultFont( Font.PLAIN, 95);
        
        // Draw the shadow for the superscript.  Again, We use shadows to
        // enhance the readability.
        g2.setFont(superscriptFont);
        g2.setPaint( Color.BLACK );
        superscriptTextLayout = 
            new TextLayout(isotopeNumber, superscriptFont, g2.getFontRenderContext());
        superscriptTextLayout.draw( g2, SHADOW_OFFSET, superscriptTextLayout.getAscent()+SHADOW_OFFSET);
        
        // Draw the main text of the superscript.
        g2.setPaint( NuclearPhysics2Constants.KRYPTON_LABEL_COLOR );
        superscriptTextLayout.draw( g2, 0, superscriptTextLayout.getAscent());
        
        // Draw the shadow for the chemical symbol.
        g2.setPaint( Color.BLACK );
        symbolTextLayout = new TextLayout(chemicalSymbol, symbolFont, g2.getFontRenderContext());
        symbolTextLayout.draw( g2, (float)superscriptTextLayout.getBounds().getWidth()+SHADOW_OFFSET, 
                symbolTextLayout.getAscent()+SHADOW_OFFSET);
        
        // Draw the main text of the chemical symbol.
        g2.setPaint( NuclearPhysics2Constants.KRYPTON_LABEL_COLOR );
        symbolTextLayout.draw( g2, (float)superscriptTextLayout.getBounds().getWidth(), symbolTextLayout.getAscent());

        g2.dispose();

        // Add the new image and the associated label to the legend.
        icon = new ImageIcon(im.getScaledInstance( 37, -1, Image.SCALE_SMOOTH ));
        add(new JLabel(icon));
        add(new JLabel( NuclearPhysics2Strings.KRYPTON_LEGEND_LABEL ));
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
