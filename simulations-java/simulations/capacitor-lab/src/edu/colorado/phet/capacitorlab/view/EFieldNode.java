// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeListener;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of the effective E-field (E_effective) between the capacitor plates.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EFieldNode extends PhetPNode {

    public static enum Direction {UP, DOWN}

    private final Capacitor capacitor;
    private final CLModelViewTransform3D mvt;
    private final PNode parentNode; // parent for all the field lines
    private final double maxEffectiveEField;

    public EFieldNode( Capacitor capacitor, CLModelViewTransform3D mvt, double maxEffectiveEField ) {
        this.capacitor = capacitor;
        this.mvt = mvt;
        this.maxEffectiveEField = maxEffectiveEField;

        capacitor.addCapacitorChangeListener( new CapacitorChangeListener() {
            public void capacitorChanged() {
                if ( isVisible() ) {
                    update();
                }
            }
        } );

        parentNode = new PComposite();
        addChild( parentNode );

        update();
    }

    /**
     * Update the node when it becomes visible.
     */
    @Override
    public void setVisible( boolean visible ) {
        if ( visible != isVisible() ) {
            super.setVisible( visible );
            if ( visible ) {
                update();
            }
        }
    }

    private void update() {

        // clear existing field lines
        parentNode.removeAllChildren();

        // compute density (spacing) of field lines
        double effectiveEField = capacitor.getEffectiveEField();
        double lineSpacing = getLineSpacing( effectiveEField );

        if ( lineSpacing > 0 ) {

            // relevant model values
            final double plateWidth = capacitor.getPlateWidth();
            final double plateDepth = plateWidth;
            final double plateSeparation = capacitor.getPlateSeparation();

            /*
             * Create field lines, working from the center outwards so that
             * lines appear/disappear at edges of plate as E_effective changes.
             */
            double length = mvt.modelToViewDelta( 0, plateSeparation, 0 ).getY();
            Direction direction = ( effectiveEField >= 0 ) ? Direction.DOWN : Direction.UP;
            double x = lineSpacing / 2;
            while ( x <= plateWidth / 2 ) {
                double z = lineSpacing / 2;
                while ( z <= plateDepth / 2 ) {

                    // add 4 lines, one for each quadrant
                    PNode lineNode0 = new EFieldLineNode( length, direction );
                    PNode lineNode1 = new EFieldLineNode( length, direction );
                    PNode lineNode2 = new EFieldLineNode( length, direction );
                    PNode lineNode3 = new EFieldLineNode( length, direction );
                    parentNode.addChild( lineNode0 );
                    parentNode.addChild( lineNode1 );
                    parentNode.addChild( lineNode2 );
                    parentNode.addChild( lineNode3 );

                    // position the lines
                    double y = 0;
                    lineNode0.setOffset( mvt.modelToView( x, y, z ) );
                    lineNode1.setOffset( mvt.modelToView( -x, y, z ) );
                    lineNode2.setOffset( mvt.modelToView( x, y, -z ) );
                    lineNode3.setOffset( mvt.modelToView( -x, y, -z ) );

                    z += lineSpacing;
                }
                x += lineSpacing;
            }
        }
    }

    /*
     * Gets the spacing of E-field lines.
     * Higher E-field results in higher density, therefore lower spacing.
     * Density is computed for the minimum plate size.
     *
     * @param effectiveEField
     * @return spacing, in model coordinates
     */
    private double getLineSpacing( double effectiveEField ) {
        if ( effectiveEField == 0 ) {
            return 0;
        }
        else {
            final int numberOfLines = getNumberOfLines( effectiveEField );
            return CLConstants.PLATE_WIDTH_RANGE.getMin() / Math.sqrt( numberOfLines ); // assumes a square plate!;
        }
    }

    /*
     * Computes number of lines to put on the smallest plate, linearly proportional to plate charge.
     */
    private int getNumberOfLines( double effectiveEField ) {
        double absEField = Math.abs( effectiveEField );
        int numberOfLines = (int) ( CLConstants.NUMBER_OF_EFIELD_LINES.getMax() * absEField / maxEffectiveEField );
        if ( absEField > 0 && numberOfLines < CLConstants.NUMBER_OF_EFIELD_LINES.getMin() ) {
            numberOfLines = CLConstants.NUMBER_OF_EFIELD_LINES.getMin();
        }
        return numberOfLines;
    }

    /**
     * An E-field line. Origin is at the center.
     */
    private static class EFieldLineNode extends PComposite {

        public static final PDimension ARROW_SIZE = new PDimension( 10, 15 );
        public static final Stroke LINE_STROKE = new BasicStroke( 2f );

        /**
         * @param length    length of the line in view coordinates
         * @param direction
         */
        public EFieldLineNode( double length, Direction direction ) {

            // line, origin at center
            PPath lineNode = new PPath( new Line2D.Double( 0, -length / 2, 0, length / 2 ) );
            lineNode.setStroke( LINE_STROKE );
            lineNode.setStrokePaint( CLPaints.EFIELD );
            addChild( lineNode );

            // arrow, shape points "up", origin at center
            GeneralPath path = new GeneralPath();
            float w = (float) ARROW_SIZE.getWidth();
            float h = (float) ARROW_SIZE.getHeight();
            path.moveTo( 0, -h / 2 ); // tip
            path.lineTo( w / 2, h / 2 ); // clockwise
            path.lineTo( -w / 2, h / 2 );
            path.closePath();
            PPath arrowNode = new PPath( path );
            arrowNode.setPaint( CLPaints.EFIELD );
            arrowNode.setStroke( null );
            addChild( arrowNode );
            if ( direction == Direction.DOWN ) {
                arrowNode.rotate( Math.PI );
            }

            // no additional layout needed, handled above by geometry specification
        }
    }
}
