/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.GeneralPath;

/**
 * EnergyView
 * <p>
 * A view of the MRModel that shows the potential energy of two individual molecules, or their compound
 * molecule. This is a fairly abstract view.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class EnergyView extends PNode {
    private Dimension size = new Dimension( 300, 300 );
    private Color backgroundColor = Color.black;
    private Color curveColor = Color.cyan;

    private PNode moleculeLayer = new PNode();
    private PNode curveLayer = new PNode();
    private PNode cursorLayer = new PNode();

    public EnergyView() {
        PPath background = new PPath( new Rectangle2D.Double(0,0,
                                                             size.getWidth(),
                                                             size.getHeight()));
        background.setPaint( backgroundColor );

        addChild( background );
        addChild( moleculeLayer );
        addChild( curveLayer );
        addChild( cursorLayer );

        EnergyCurve energyCurve = new EnergyCurve( size.getWidth(), curveColor );
        energyCurve.setLeftLevel( 250 );
        energyCurve.setRightLevel( 200 );
        energyCurve.setPeakLevel( 100 );

        curveLayer.addChild( energyCurve );
    }

    private class EnergyCurve extends PPath {
        private double leftLevel;
        private double peakLevel;
        private double rightLevel;
        private double width;
        private Color color;

        public EnergyCurve( double width, Color color ) {
            this.width = width;
            this.color = color;
            update();
        }

        private void update() {
            DoubleGeneralPath curve = new DoubleGeneralPath( );

            double x1 = width * 0.4;
            double x2 = width * 0.5;
            double x3 = width * 0.6;
            curve.moveTo( 0,leftLevel);
            curve.lineTo( x1, leftLevel );

            curve.curveTo( x1 + (x2 - x1) * 0.33, leftLevel,
                           x1 + (x2 - x1) * 0.66, peakLevel,
                           x2, peakLevel );
            curve.curveTo( x2 + (x3 - x2) * 0.33, peakLevel,
                           x2 + (x3 - x2) * 0.66, rightLevel,
                           x3, rightLevel );
            curve.lineTo( width, rightLevel );

            setPathTo( curve.getGeneralPath() );
            setStrokePaint( color );
            setStroke( new BasicStroke( 3 ) );
        }

        public void setLeftLevel( double leftLevel ) {
            this.leftLevel = leftLevel;
            update();
        }

        public void setPeakLevel( double peakLevel ) {
            this.peakLevel = peakLevel;
            update();
        }

        public void setRightLevel( double rightLevel ) {
            this.rightLevel = rightLevel;
            update();
        }
    }
}
