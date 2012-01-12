// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.view;

import java.awt.*;
import java.text.DecimalFormat;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.Vector2DNode;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.util.OTVector2D;

/**
 * AbstractForceNode is the base class for all nodes that display a force vector.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractForceNode extends PhetPNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean SHOW_VALUES = false;
    private static final boolean SHOW_COMPONENT_VECTORS = false;

    // properties of the vectors
    private static final double VECTOR_HEAD_HEIGHT = 20;
    private static final double VECTOR_HEAD_WIDTH = 20;
    private static final double VECTOR_TAIL_WIDTH = 5;
    private static final Stroke VECTOR_STROKE = new BasicStroke( 1f );
    private static final Paint VECTOR_STROKE_PAINT = Color.BLACK;
    private static final double VALUE_SPACING = 2; // space between value and arrow head
    private static final Paint VALUE_PAINT = Color.BLACK;
    
    private static final DecimalFormat VALUE_FORMAT = new DecimalFormat( "0.##E0" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Vector2DNode _sumNode, _xComponentNode, _yComponentNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AbstractForceNode( double modelReferenceMagnitude, double viewReferenceMagnitude, String units, Color color ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _sumNode = createVectorNode( color, modelReferenceMagnitude, viewReferenceMagnitude, units );
        _sumNode.setValuePaint( VALUE_PAINT );
        _sumNode.setValueVisible( SHOW_VALUES );
        
        Color componentColor = new Color( color.getRed(), color.getGreen(), color.getBlue(), 100 );
        
        _xComponentNode = createVectorNode( componentColor, modelReferenceMagnitude, viewReferenceMagnitude, units  );
        _xComponentNode.setValuePaint( VALUE_PAINT );
        _xComponentNode.setValueVisible( SHOW_VALUES );
        
        _yComponentNode = createVectorNode( componentColor, modelReferenceMagnitude, viewReferenceMagnitude, units );
        _yComponentNode.setValuePaint( VALUE_PAINT );
        _yComponentNode.setValueVisible( SHOW_VALUES );

        addChild( _xComponentNode );
        addChild( _yComponentNode );
        addChild( _sumNode );
        
        setValuesVisible( SHOW_VALUES );
        setComponentVectorsVisible( SHOW_COMPONENT_VECTORS );
    }
    
    /*
     * Creates a Vector2DNode with common property values.
     */
    private static Vector2DNode createVectorNode( Paint fillPaint, double modelReferenceMagnitude, double viewReferenceMagnitude, String units ) {
        Vector2DNode vectorNode = new Vector2DNode( 0, 0, modelReferenceMagnitude, viewReferenceMagnitude );
        vectorNode.setUpdateEnabled( false );
        vectorNode.setHeadSize( VECTOR_HEAD_WIDTH, VECTOR_HEAD_HEIGHT );
        vectorNode.setTailWidth( VECTOR_TAIL_WIDTH );
        vectorNode.setArrowFillPaint( fillPaint );
        vectorNode.setArrowStroke( VECTOR_STROKE );
        vectorNode.setArrowStrokePaint( VECTOR_STROKE_PAINT );
        vectorNode.setValueSpacing( VALUE_SPACING );
        vectorNode.setValueVisible( SHOW_VALUES );
        vectorNode.setValueFormat( VALUE_FORMAT );
        vectorNode.setUnits( units );
        vectorNode.setUpdateEnabled( true );
        return vectorNode;
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setComponentVectorsVisible( boolean visible ) {
        _xComponentNode.setVisible( visible );
        _yComponentNode.setVisible( visible );
    }
    
    public void setValuesVisible( boolean visible ) {
        _sumNode.setValueVisible( visible );
        _xComponentNode.setValueVisible( visible );
        _yComponentNode.setValueVisible( visible );
    }
    
    protected void setForce( OTVector2D force ) {
        _sumNode.setXY( force.getX(), force.getY() );
        _xComponentNode.setXY( force.getX(), 0 );
        _yComponentNode.setXY( 0, force.getY() );
    }
    
    public void setArrowFillPaint( Paint paint ) {
        _sumNode.setArrowFillPaint( paint );
        _xComponentNode.setArrowFillPaint( paint );
        _yComponentNode.setArrowFillPaint( paint );
    }
    
    public Paint getArrowFillPaint() {
        return _sumNode.getArrowFillPaint();
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    protected static Icon createIcon( Color color ) {
        final double length = OTConstants.VECTOR_ICON_LENGTH;
        Vector2DNode vectorNode = new Vector2DNode( -length, 0, length, length );
        vectorNode.setArrowFillPaint( color );
        vectorNode.setTailWidth( OTConstants.VECTOR_ICON_TAIL_WIDTH );
        vectorNode.setHeadSize( OTConstants.VECTOR_ICON_HEAD_SIZE.width, OTConstants.VECTOR_ICON_HEAD_SIZE.height );
        Image image = vectorNode.toImage();
        return new ImageIcon( image );
    }
}
