/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeAdapter;
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
    
    public static enum Direction { UP, DOWN };
    
    private final BatteryCapacitorCircuit circuit; 
    private final ModelViewTransform mvt;
    private final PNode parentNode; // parent for all the field lines

    public EFieldNode( BatteryCapacitorCircuit circuit, ModelViewTransform mvt ) {
        this.circuit = circuit;
        this.mvt = mvt;
        
        circuit.addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeAdapter() {
            @Override
            public void efieldChanged() {
                update();
            }
        } );
        
        parentNode = new PComposite();
        addChild( parentNode );
        
        update();
    }
    
    private void update() {
        
        // compute number of field lines
        double effectiveEField = circuit.getEffectiveEfield();
        int numberOfLines = getNumberOfLines( effectiveEField );
        
        // clear existing field lines
        parentNode.removeAllChildren();
        
        if ( numberOfLines > 0 ) {
            
            final double plateWidth = circuit.getCapacitor().getPlateSideLength();
            final double plateDepth = plateWidth;
            final double plateSeparation = circuit.getCapacitor().getPlateSeparation();
            
            // grid dimensions
            Dimension gridSize = getGridSize( numberOfLines, plateWidth, plateDepth );
            final int rows = gridSize.height;
            final int columns = gridSize.width;
            
            // distance between cells
            final double dx = plateWidth / columns;
            final double dz = plateDepth / rows;
            
            // offset to move us to the center of cells
            final double xOffset = dx / 2;
            final double zOffset = dz / 2;
            
            // populate the grid
            for ( int row = 0; row < rows; row++ ) {
                for ( int column = 0; column < columns; column++ ) {
                    
                    // add a line
                    Direction direction = ( effectiveEField >= 0 ) ? Direction.DOWN : Direction.UP;
                    double length = mvt.modelToView( plateSeparation );
                    PNode lineNode = new EFieldLineNode( length, direction );
                    parentNode.addChild( lineNode );

                    // position the line in the grid cell
                    double x = -( plateWidth /2 ) + xOffset + ( column * dx );
                    double y = -( plateSeparation / 2 );
                    double z = -( plateDepth / 2 ) + zOffset + ( row * dz );
                    Point2D offset = mvt.modelToView( x, y, z );
                    lineNode.setOffset( offset );
                }
            }
        }
    }
    
    // same algorithm used in CCK CapacitorNode for plate charges
    private Dimension getGridSize( int numberOfLines, double width, double height ) {
        double alpha = Math.sqrt( numberOfLines / width / height );
        // casting here may result in some charges being thrown out, but that's OK
        int columns = (int)( width * alpha );
        int rows = (int)( height * alpha );
        return new Dimension( columns, rows );
    }
    
    /*
     * Computes number of field lines, linearly proportional to effective E-field (E_effective).
     * All non-zero values below some minimum are mapped to 1 charge.
     */
    private int getNumberOfLines( double effectiveEField ) {
        
        double absoluteEffectiveEField = Math.abs( effectiveEField );
        double minEffectiveEField = CLConstants.MIN_NONZERO_EFFECTIVE_EFIELD;
        double maxEffectiveEField = BatteryCapacitorCircuit.getMaxEffectiveEfield();
        
        int numberOfLines = 0;
        if ( absoluteEffectiveEField == 0 ) {
            numberOfLines = 0;
        }
        else if ( absoluteEffectiveEField <= minEffectiveEField ) {
            numberOfLines = 1;
        }
        else {
            numberOfLines = (int) ( CLConstants.MAX_NUMBER_OF_EFIELD_LINES * ( absoluteEffectiveEField - minEffectiveEField ) / ( maxEffectiveEField - minEffectiveEField ) );
        }
        return numberOfLines;
    }
    
    /**
     * An E-field line.  Origin is at the top center.
     */
    private static class EFieldLineNode extends PComposite {
        
        public static final PDimension ARROW_SIZE = new PDimension( 20, 20 );
        public static final Stroke LINE_STROKE = new BasicStroke( 2f );
        
        /**
         * @param length length of the line in view coordinates
         * @param direction
         */
        public EFieldLineNode( double length, Direction direction ) {
            
            PPath lineNode = new PPath( new Line2D.Double( 0, 0, 0, length ) );
            lineNode.setStroke( LINE_STROKE );
            lineNode.setStrokePaint( CLPaints.EFIELD );
            addChild( lineNode );
            
            // arrow shape points up, with origin at center
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
            
            // layout
            double x = 0;
            double y = 0;
            lineNode.setOffset( x, y );
            x = lineNode.getFullBoundsReference().getCenterX();
            y = lineNode.getFullBoundsReference().getCenterY();
            arrowNode.setOffset( x, y );
        }
    }
}
