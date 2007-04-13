/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.view;

import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * EnergyDifferenceSquiggle
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class EnergySquiggle extends PNode {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    public static class Orientation {
        private Orientation() {
        }
    }

    public static final Orientation VERTICAL = new Orientation();
    public static final Orientation HORIZONTAL = new Orientation();

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    // Used to get the wavelength to look right graphically for the range of frequencies the graphic
    // is intended to represent
    private double frequencyFactor = 1E-9;
    private PImage squiggleGraphic;
    private Orientation orientation;

    /**
     * Constructor
     *
     * @param orientation
     */
    public EnergySquiggle( Orientation orientation ) {
        this.orientation = orientation;
    }

    public void update( double wavelength, double phaseAngle, int length, int height ) {
        if( squiggleGraphic != null ) {
            removeChild( squiggleGraphic );
        }
        squiggleGraphic = new PImage( computeSquiggleImage( wavelength, phaseAngle, length, height ) );
        addChild( squiggleGraphic );
    }

    /**
     * Creates a buffered image for a squiggle
     */
    private BufferedImage computeSquiggleImage( double wavelength, double phaseAngle, int length, int height ) {

        int arrowHeight = height;

        // So that the tip of the arrow will just touch an energy level line when it is supposed to match the line,
        // we need to subtract 1 from the length of the squiggle
        int actualLength = length - 1;

        // A buffered image for generating the image data
        BufferedImage img = new BufferedImage( actualLength + 2 * arrowHeight,
                                               height,
                                               BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2d = img.createGraphics();
        int kPrev = height / 2;
        int iPrev = 0;
        double freqFactor = wavelength * frequencyFactor;
        g2d.setColor( Color.black );
        for( int i = 0; i < actualLength - arrowHeight * 2; i++ ) {
            int k = (int)( Math.sin( phaseAngle + i * Math.PI * 2 / freqFactor ) * height / 2 + height / 2 );
            for( int j = 0; j < height; j++ ) {
                if( j == k ) {
                    g2d.drawLine( iPrev + arrowHeight, kPrev, i + arrowHeight, k );
                    iPrev = i;
                    kPrev = k;
                }
            }
        }
        Arrow head = new Arrow( new Point2D.Double( arrowHeight, height / 2 ),
                                new Point2D.Double( 0, height / 2 ),
                                arrowHeight, height * 1.2, 2 );
        Arrow tail = new Arrow( new Point2D.Double( actualLength - arrowHeight, height / 2 ),
                                new Point2D.Double( actualLength, height / 2 ),
                                arrowHeight, height * 1.2, 2 );
        g2d.fill( head.getShape() );
        g2d.fill( tail.getShape() );
        g2d.dispose();

        // If the orientation in vertical, rotate the image
        if( orientation == VERTICAL ) {
            img = BufferedImageUtils.getRotatedImage( img, Math.PI / 2 );
        }
        return img;
    }

}
