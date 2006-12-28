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

import edu.colorado.phet.mri.util.RoundGradientPaint;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * DetectorPaint
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DetectorPaint extends RoundGradientPaint {

    static Color[] grayScale = new Color[256];

    static {
        for( int i = 0; i < grayScale.length; i++ ) {
            grayScale[i] = new Color( i, i, i );
        }
    }

    public DetectorPaint( Point2D location, double width, double density, Color backgroundColor ) {
        super( location.getX(),
               location.getY(),
               grayScale[(int)( ( 1 - density ) * ( grayScale.length - 1 ) )],
               new Point2D.Double( 0, Math.max( 1, width * density / 2 ) ),
//               new Point2D.Double( 0, width * density / 2 ),
backgroundColor );
    }
}
