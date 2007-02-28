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

public class Polonium211Graphic extends NucleusGraphic {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static Font isotopeFont = new Font( "SansSerif", Font.BOLD, 16 );
    private static Font elementFont = new Font( "SansSerif", Font.BOLD, 34 );
    private static AffineTransform nucleusTx = new AffineTransform();
    private static Stroke fontOutlineStroke = new BasicStroke( 1f );
    private static Random random = new Random();
    private static int numImagesToUse = 15;
    // An array of differently randomized images of U235 nuclei, that we will choose randomly between at runtime
    private static BufferedImage[] imagesToUse = new BufferedImage[Polonium211Graphic.numImagesToUse];
    static {
        Nucleus nucleus = new Nucleus( new Point2D.Double( ), Uranium235.NUM_PROTONS, Uranium235.NUM_NEUTRONS );
        for( int i = 0; i < Polonium211Graphic.imagesToUse.length; i++ ) {
            Polonium211Graphic.imagesToUse[i] = computeImage( nucleus );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private boolean displayLabel = true;

    public Polonium211Graphic( Nucleus nucleus ) {
        super( nucleus, Polonium211Graphic.imagesToUse[ Polonium211Graphic.random.nextInt( Polonium211Graphic.numImagesToUse )] );
    }

    public void setDisplayLabel( boolean displayLabel ) {
        this.displayLabel = displayLabel;
    }

    public void paint( Graphics2D g ) {
        super.paint( g );

        if( displayLabel ) {
            Polonium211Graphic.nucleusTx.setToTranslation( getNucleus().getPosition().getX(), getNucleus().getPosition().getY() );
            AffineTransform orgTx = g.getTransform();
            g.transform( Polonium211Graphic.nucleusTx );

            GraphicsUtil.setAntiAliasingOn( g );

            g.setColor( NucleusLabelColors.getColor( this.getClass() ));
            g.setFont( Polonium211Graphic.isotopeFont );
            FontMetrics fm = g.getFontMetrics();
            g.drawString( SimStrings.get( "Polonium211Graphic.Number" ), -fm.stringWidth( SimStrings.get( "Polonium211Graphic.Number" ) ), 0 );

            int dy = fm.getHeight() * 3 / 4;
            g.setFont( Polonium211Graphic.elementFont );
            g.drawString( SimStrings.get( "Polonium211Graphic.Symbol" ), 0, dy );
            g.setTransform( orgTx );
        }
    }
}
