/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.Uranium235;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.SimStrings;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Random;

public class Lead206Graphic extends NucleusGraphic {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static Font isotopeFont = new Font( "SansSerif", Font.BOLD, 16 );
    private static Font elementFont = new Font( "SansSerif", Font.BOLD, 34 );
    private static Color color = Color.orange;
    private static AffineTransform nucleusTx = new AffineTransform();
    private static Stroke fontOutlineStroke = new BasicStroke( 1f );
    private static Random random = new Random();
    private static int numImagesToUse = 15;
    // An array of differently randomized images of U235 nuclei, that we will choose randomly between at runtime
    private static BufferedImage[] imagesToUse = new BufferedImage[Lead206Graphic.numImagesToUse];
    static {
        Nucleus nucleus = new Nucleus( new Point2D.Double( ), Uranium235.NUM_PROTONS, Uranium235.NUM_NEUTRONS );
        for( int i = 0; i < Lead206Graphic.imagesToUse.length; i++ ) {
            Lead206Graphic.imagesToUse[i] = computeImage( nucleus );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private boolean displayLabel = true;

    public Lead206Graphic( Nucleus nucleus ) {
        super( nucleus, Lead206Graphic.imagesToUse[ Lead206Graphic.random.nextInt( Lead206Graphic.numImagesToUse )] );
    }

    public void setDisplayLabel( boolean displayLabel ) {
        this.displayLabel = displayLabel;
    }

    public void paint( Graphics2D g ) {
        super.paint( g );

        if( displayLabel ) {
            Lead206Graphic.nucleusTx.setToTranslation( getNucleus().getPosition().getX(), getNucleus().getPosition().getY() );
            AffineTransform orgTx = g.getTransform();
            g.transform( Lead206Graphic.nucleusTx );

            GraphicsUtil.setAntiAliasingOn( g );

            g.setColor( Lead206Graphic.color );
            g.setFont( Lead206Graphic.isotopeFont );
            FontMetrics fm = g.getFontMetrics();
            g.drawString( SimStrings.get( "Lead206Graphic.Number" ), -fm.stringWidth( SimStrings.get( "Lead206Graphic.Number" ) ), 0 );

            int dy = fm.getHeight() * 3 / 4;
            g.setColor( Lead206Graphic.color );
            g.setFont( Lead206Graphic.elementFont );
            g.drawString( SimStrings.get( "Lead206Graphic.Symbol" ), 0, dy );
            g.setTransform( orgTx );
        }
    }
}
