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

import edu.colorado.phet.mri.model.MriModel;
import edu.umd.cs.piccolo.PNode;

/**
 * BFieldGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BFieldGraphic extends PNode {
    private int numSegments = 7;
    private MriModel model;

    public BFieldGraphic( MriModel model ) {
        this.model = model;
    }

    /**
     * Creates the arrows that grow and shrink with the field the magnet is producing
     *
     * @param bounds
     * @param gradientMagnet
     */
//    private void createFieldStrengthArrows( Rectangle2D bounds, GradientElectromagnet gradientMagnet ) {
//        double baseDim = bounds.getWidth();
//        double spacing = baseDim / numSegments;
//        for( int i = 0; i < numSegments; i++ ) {
//            double xLoc = spacing * ( i + 0.5 );
//            // Use a big max length so that we'll see the arrow at small field strengths.
//            // todo: this should be done with a max-strength parameter to the constructor!!!
//            BFieldIndicator arrow = new BFieldIndicator( gradientMagnet, 500, null, xLoc );
////            BFieldIndicator arrow = new BFieldIndicator( gradientMagnet, 150, null, xLoc );
//            addChild( arrow );
//            Point2D.Double p = null;
////            if( magnet.getOrientation() == GradientElectromagnet.HORIZONTAL ) {
////                p = new Point2D.Double( xLoc, bounds.getHeight() / 2 );
////            }
////            else {
////                p = new Point2D.Double( bounds.getWidth() / 2, xLoc );
////            }
////            arrow.setOffset( p );
//        }
//    }

}
