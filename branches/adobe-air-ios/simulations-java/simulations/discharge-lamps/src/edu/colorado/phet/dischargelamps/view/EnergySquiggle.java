// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;

/**
 * EnergySquiggle
 * <p/>
 * A sine wave with arrows at each end that is meant to show something about the wavelength
 * and color associated with an amount of energy
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class EnergySquiggle extends PhetImageGraphic {

    //-----------------------------------------------------------------
    // Class data and methods
    //-----------------------------------------------------------------
    static public final int VERTICAL = 1, HORIZONTAL = 2;
    static private int numArrowheads = 1;


    /**
     * @param component
     * @param wavelength
     * @param phaseAngle
     * @param length
     * @param height
     * @param orientation
     */
    public EnergySquiggle( Component component, double wavelength, double phaseAngle,
                           int length, int height, int orientation ) {
        super( component );
        setImage( computeSquiggleImage( wavelength, phaseAngle, length, height, orientation ) );
    }

    /**
     * Creates a buffered image for a squiggle
     */
    private BufferedImage computeSquiggleImage( double wavelength,
                                                double phaseAngle,
                                                int length,
                                                int height,
                                                int orientation ) {
        int arrowHeight = height;

        // So that the tip of the arrow will just touch an energy level line when it is supposed to match the line,
        // we need to subtract 1 from the length of the squiggle
        int actualLength = length - 1;

        // A buffered image for generating the image data
        if ( actualLength + numArrowheads * arrowHeight <= 0 || height <= 0 ) {
            System.out.println( "EnergySquiggle.computeSquiggleImage" );
        }
        BufferedImage img = new BufferedImage( actualLength + numArrowheads * arrowHeight,
                                               height,
                                               BufferedImage.TYPE_INT_ARGB_PRE );
        Graphics2D g2d = img.createGraphics();
        Color c = VisibleColor.wavelengthToColor( wavelength );
        if ( c.getRed() == 0 && c.getGreen() == 0 & c.getBlue() == 0 ) {
            c = Color.black;
        }
        g2d.setColor( c );

        int kPrev = height / 2;
        int iPrev = 0;
        int iOffset = arrowHeight * ( numArrowheads - 1 );
        double renderedWavelength = Math.max( wavelength, VisibleColor.MIN_WAVELENGTH / 2 );
        double freqFactor = 15 * renderedWavelength / 680;
        for ( int i = 0; i < actualLength - ( arrowHeight * numArrowheads ); i++ ) {
            int k = (int) ( Math.sin( phaseAngle + i * Math.PI * 2 / freqFactor ) * height / 2 + height / 2 );
            for ( int j = 0; j < height; j++ ) {
                if ( j == k ) {
                    g2d.drawLine( iPrev + iOffset, kPrev, i + iOffset, k );
                    iPrev = i;
                    kPrev = k;
                }
            }
        }
        if ( numArrowheads == 2 ) {
            Arrow head = new Arrow( new Point2D.Double( arrowHeight, height / 2 ),
                                    new Point2D.Double( 0, height / 2 ),
                                    arrowHeight, height * 1.2, 2 );
            g2d.fill( head.getShape() );
        }
        Arrow tail = new Arrow( new Point2D.Double( actualLength - arrowHeight, height / 2 ),
                                new Point2D.Double( actualLength, height / 2 ),
                                arrowHeight, height * 1.2, 2 );
        g2d.fill( tail.getShape() );
        g2d.dispose();
        if ( orientation == VERTICAL ) {
            img = BufferedImageUtils.getRotatedImage( img, Math.PI / 2 );
        }
        return img;
    }
}
