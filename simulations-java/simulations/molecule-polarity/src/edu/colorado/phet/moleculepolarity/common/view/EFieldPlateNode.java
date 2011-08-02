// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.common.model.EField;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for E-Field plates.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class EFieldPlateNode extends PComposite {

    public static class PositiveEFieldPlateNode extends EFieldPlateNode {
        public PositiveEFieldPlateNode( EField eField ) {
            super( eField, true );
        }
    }

    public static class NegativeEFieldPlateNode extends EFieldPlateNode {
        public NegativeEFieldPlateNode( EField eField ) {
            super( eField, false );
        }
    }

    private static final Dimension PLATE_SIZE = new Dimension( 50, 450 );
    private static final double INDICATOR_DIAMETER = 50;

    private final PPath plateNode;
    private final PNode indicatorNode;
    private final Color enabledColor;

    public EFieldPlateNode( EField eField, boolean positive ) {

        this.enabledColor = positive ? MPConstants.POSITIVE_COLOR : MPConstants.NEGATIVE_COLOR;

        indicatorNode = new PolarityIndicatorNode( positive, INDICATOR_DIAMETER );
        addChild( indicatorNode );

        plateNode = new PlateNode( positive, PLATE_SIZE );
        addChild( plateNode );

        double x = plateNode.getFullBoundsReference().getCenterX() - ( indicatorNode.getFullBoundsReference().getWidth() / 2 ) - PNodeLayoutUtils.getOriginXOffset( indicatorNode );
        double y = plateNode.getFullBoundsReference().getMinY() - indicatorNode.getFullBoundsReference().getHeight() - PNodeLayoutUtils.getOriginYOffset( indicatorNode );
        indicatorNode.setOffset( x, y );

        eField.enabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean enabled ) {
                setEnabled( enabled );
            }
        } );
    }

    private void setEnabled( boolean enabled ) {
        plateNode.setPaint( enabled ? enabledColor : MPConstants.PLATE_DISABLED_COLOR );
        indicatorNode.setVisible( enabled );
    }

    private static class PlateNode extends PPath {
        public PlateNode( boolean positive, Dimension size ) {
            //TODO add 3D perspective, based on positive parameter
            super( new Rectangle2D.Double( 0, 0, size.width, size.height ) );
        }
    }

    private static class PolarityIndicatorNode extends PComposite {
        public PolarityIndicatorNode( boolean positive, double diameter ) {
            final float strokeWidth = (float) ( 0.1 * diameter );
            // circle
            addChild( new PPath( new Ellipse2D.Double( 0, 0, diameter, diameter ) ) {{
                setStroke( new BasicStroke( strokeWidth ) );
            }} );
            // horizontal bar for plus or minus sign
            addChild( new PPath( new Line2D.Double( 0.25 * diameter, 0.5 * diameter, 0.75 * diameter, 0.5 * diameter ) ) {{
                setStroke( new BasicStroke( strokeWidth ) );
            }} );
            // vertical bar for plus sign
            if ( positive ) {
                addChild( new PPath( new Line2D.Double( 0.5 * diameter, 0.25 * diameter, 0.5 * diameter, 0.75 * diameter ) ) {{
                    setStroke( new BasicStroke( strokeWidth ) );
                }} );
            }
            // vertical connecting bar
            addChild( new PPath( new Line2D.Double( 0.5 * diameter, diameter, 0.5 * diameter, 1.25 * diameter ) ) {{
                setStroke( new BasicStroke( strokeWidth ) );
            }} );
        }
    }
}
