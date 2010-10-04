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
        
        // compute density (spacing) of field lines
        double effectiveEField = circuit.getEffectiveEfield();
        double lineSpacing = getLineSpacing( effectiveEField );
        
        // clear existing field lines
        parentNode.removeAllChildren();
        
        if ( lineSpacing > 0 ) {
            
            final double plateWidth = circuit.getCapacitor().getPlateSideLength();
            final double plateDepth = plateWidth;
            final double plateSeparation = circuit.getCapacitor().getPlateSeparation();
            
            // grid dimensions
            Dimension gridSize = getGridSize( lineSpacing, plateWidth, plateDepth );
            final int rows = gridSize.height;
            final int columns = gridSize.width;
            
            // margin
            double xMargin = ( plateWidth - ( columns * lineSpacing ) ) / 2;
            double zMargin = ( plateDepth - ( rows * lineSpacing ) ) / 2;
            
            // distance between cells
            final double dx = lineSpacing;
            final double dz = lineSpacing;
            
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
                    double x = -( plateWidth /2 ) + xMargin + xOffset + ( column * dx );
                    double y = 0;
                    double z = -( plateDepth / 2 ) + zMargin + zOffset + ( row * dz );
                    Point2D offset = mvt.modelToView( x, y, z );
                    lineNode.setOffset( offset );
                }
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
        final int numberOfLines = getNumberOfLines( effectiveEField );
        return BatteryCapacitorCircuit.getMinPlateSideLength() / Math.sqrt( numberOfLines ); // assumes a square plate!;
    }
    
    /*
     * Computes number of lines to put on the smallest plate, linearly proportional to plate charge.
     */
    private int getNumberOfLines( double effectiveEField ) {
        
        double absEField = Math.abs( effectiveEField );
        double maxEField = BatteryCapacitorCircuit.getMaxEffectiveEfield();
        
        int numberOfLines = (int)( CLConstants.NUMBER_OF_EFIELD_LINES.getMax() * absEField / maxEField );
        if ( absEField > 0 && numberOfLines < CLConstants.NUMBER_OF_EFIELD_LINES.getMin() ) {
            numberOfLines = CLConstants.NUMBER_OF_EFIELD_LINES.getMin();
        }
        return numberOfLines;
    }
    
    private Dimension getGridSize( double cellSpacing, double width, double height ) {
        // casting here may result in some lines being thrown out, but that's OK
        int columns = (int)( width / cellSpacing );
        int rows = (int)( height / cellSpacing );
        return new Dimension( columns, rows );
    }
    
    /**
     * An E-field line. Origin is at the center.
     */
    private static class EFieldLineNode extends PComposite {
        
        public static final PDimension ARROW_SIZE = new PDimension( 10, 15 );
        public static final Stroke LINE_STROKE = new BasicStroke( 2f );
        
        /**
         * @param length length of the line in view coordinates
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
