/**
 * Class: Uranium235Graphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Mar 19, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.Uranium235;
import edu.colorado.phet.nuclearphysics.model.Uranium239;
import edu.colorado.phet.common.view.util.SimStrings;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Random;

public class Uranium239Graphic extends NucleusGraphic {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------
    
    private static Font isotopeFont = new Font( "SansSerif", Font.BOLD, 12 );
    private static Font elementFont = new Font( "SansSerif", Font.BOLD, 30 );
    private static Font font = new Font( "Serif", Font.BOLD, 18 );
    private static Color color = Color.white;
    private static AffineTransform nucleusTx = new AffineTransform();

    private static Random random = new Random();
    private static int numImagesToUse = 15;
    // An array of differently randomized images of U239 nuclei, that we will choose randomly between at runtime
    private static BufferedImage[] imagesToUse = new BufferedImage[numImagesToUse];
    static {
        Nucleus nucleus = new Nucleus( new Point2D.Double( ), Uranium239.NUM_PROTONS, Uranium239.NUM_NEUTRONS );
        for( int i = 0; i < imagesToUse.length; i++ ) {
            imagesToUse[i] = computeImage( nucleus );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    public Uranium239Graphic( Nucleus nucleus ) {
        super( nucleus, imagesToUse[ random.nextInt( numImagesToUse )] );
    }

    public void paint( Graphics2D g ) {
        nucleusTx.setToTranslation( getNucleus().getPosition().getX(), getNucleus().getPosition().getY() );
        super.paint( g );

        AffineTransform orgTx = g.getTransform();
        g.transform( nucleusTx );

        g.setColor( color );
        g.setFont( isotopeFont );
        FontMetrics fm = g.getFontMetrics();
        g.drawString( SimStrings.get( "Uranium239Graphic.Number" ), -fm.stringWidth( SimStrings.get( "Uranium239Graphic.Number" ) ), 0 );
        int dy = fm.getHeight() * 3 / 4;
        g.setFont( elementFont );
        g.drawString( SimStrings.get( "Uranium239Graphic.Symbol" ), 0, dy );

        g.setTransform( orgTx );
    }
}
