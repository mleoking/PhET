// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.common.piccolophet.nodes.Vector2DNode;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;

/**
 * ElectricFieldVectorNode displays the electric field at a point.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ElectricFieldVectorNode extends Vector2DNode {
    
    private static final String UNITS = OTResources.getString( "units.electricField");
    private static final Stroke VECTOR_STROKE = new BasicStroke( 2f );
    
    private final Point2D _offsetFromLaser; // offset from laser origin, nm (model coordinates)
    
    public ElectricFieldVectorNode( double xOffsetFromLaser, double yOffsetFromLaser, double referenceMagnitude, double referenceLength ) {
        super( 0, 0, referenceMagnitude, referenceLength );
        _offsetFromLaser = new Point2D.Double( xOffsetFromLaser, yOffsetFromLaser );
        setArrowStroke( VECTOR_STROKE );
        setArrowFillPaint( null );
        setUnits( UNITS );
    }
    
    public Point2D getOffsetFromLaserReference() {
        return _offsetFromLaser;
    }
    
    public static Icon createIcon() {
        final double length = OTConstants.VECTOR_ICON_LENGTH;
        Vector2DNode vectorNode = new Vector2DNode( -length, 0, length, length );
        vectorNode.setUpdateEnabled( false ); // disable updates before changing properties
        vectorNode.setArrowStroke( VECTOR_STROKE );
        vectorNode.setArrowStrokePaint( OTConstants.ELECTRIC_FIELD_COLOR );
        vectorNode.setArrowFillPaint( null );
        vectorNode.setTailWidth( OTConstants.VECTOR_ICON_TAIL_WIDTH );
        vectorNode.setHeadSize( OTConstants.VECTOR_ICON_HEAD_SIZE.width, OTConstants.VECTOR_ICON_HEAD_SIZE.height );
        vectorNode.setUpdateEnabled( true ); // update
        Image image = vectorNode.toImage();
        return new ImageIcon( image );
    }
}
