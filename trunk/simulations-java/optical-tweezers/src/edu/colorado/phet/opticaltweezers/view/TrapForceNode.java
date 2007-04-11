/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.util.Vector2D;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * TrapForceNode displays the optical trap force acting on a bead.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TrapForceNode extends PComposite implements Observer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean SHOW_VALUES = true;
    private static final boolean SHOW_XY_COMPONENTS = true;

    // properties of the vectors
    private static final double VECTOR_HEAD_HEIGHT = 20;
    private static final double VECTOR_HEAD_WIDTH = 20;
    private static final double VECTOR_TAIL_WIDTH = 5;
    private static final double VECTOR_MAX_TAIL_LENGTH = 125;
    private static final Stroke VECTOR_STROKE = new BasicStroke( 1f );
    private static final Paint VECTOR_STROKE_PAINT = Color.BLACK;
    private static final Paint SUM_VECTOR_FILL_PAINT = Color.WHITE;
    private static final Paint COMPONENT_VECTOR_FILL_PAINT = Color.LIGHT_GRAY;
    private static final double VALUE_SPACING = 2; // space between value and arrow head
    
    private static final DecimalFormat VALUE_FORMAT = new DecimalFormat( "0.##E0" );
    
    private static final String UNITS_STRING = OTResources.getString( "units.trapForce" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Laser _laser;
    private Bead _bead;
    private ModelViewTransform _modelViewTransform;
    private Vector2DNode _vectorNode, _xComponentNode, _yComponentNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public TrapForceNode( Laser laser, Bead bead, ModelViewTransform modelViewTransform ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _laser = laser;
        _laser.addObserver( this );
        
        _bead = bead;
        _bead.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        double x = _laser.getX() + ( _laser.getDiameterAtWaist() / 4 ); // halfway between center and edge of waist
        double y = _laser.getY();
        double maxPower = _laser.getPowerRange().getMax();
        Vector2D maxTrapForce = _laser.getTrapForce( x, y, maxPower );

        _xComponentNode = createVectorNode( COMPONENT_VECTOR_FILL_PAINT, maxTrapForce.getMagnitude(), VECTOR_MAX_TAIL_LENGTH );
        _xComponentNode.setValueVisible( false );
        
        _yComponentNode = createVectorNode( COMPONENT_VECTOR_FILL_PAINT, maxTrapForce.getMagnitude(), VECTOR_MAX_TAIL_LENGTH );
        _yComponentNode.setValueVisible( false );
        
        _vectorNode = createVectorNode( SUM_VECTOR_FILL_PAINT, maxTrapForce.getMagnitude(), VECTOR_MAX_TAIL_LENGTH );
        
        if ( SHOW_XY_COMPONENTS ) {
            addChild( _xComponentNode );
            addChild( _yComponentNode );
        }
        addChild( _vectorNode );
        
        updatePosition();
        updateVectors();
    }
    
    public void cleanup() {
        _laser.deleteObserver( this );
        _bead.deleteObserver( this );
    }
    
    /*
     * Creates a Vector2DNode with common property values.
     */
    private static Vector2DNode createVectorNode( Paint fillPaint, double referenceMagnitude, double referenceLength ) {
        Vector2DNode vectorNode = new Vector2DNode( new Vector2D(), referenceMagnitude, referenceLength );
        vectorNode.setUpdateEnabled( false );
        vectorNode.setHeadSize( VECTOR_HEAD_WIDTH, VECTOR_HEAD_HEIGHT );
        vectorNode.setTailWidth( VECTOR_TAIL_WIDTH );
        vectorNode.setArrowFillPaint( fillPaint );
        vectorNode.setArrowStroke( VECTOR_STROKE );
        vectorNode.setArrowStrokePaint( VECTOR_STROKE_PAINT );
        vectorNode.setValueSpacing( VALUE_SPACING );
        vectorNode.setValueVisible( SHOW_VALUES );
        vectorNode.setValueFormat( VALUE_FORMAT );
        vectorNode.setUnits( UNITS_STRING );
        vectorNode.setUpdateEnabled( true );
        return vectorNode;
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _laser ) {
            if ( arg == Laser.PROPERTY_POSITION || arg == Laser.PROPERTY_POWER || arg == Laser.PROPERTY_RUNNING ) {
                updateVectors();
            }
        }
        else if ( o == _bead ) {
            if ( arg == Bead.PROPERTY_POSITION ) {
                updatePosition();
                updateVectors();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void updatePosition() {
        Point2D position = _modelViewTransform.modelToView( _bead.getPositionRef() );
        setOffset( position.getX(), position.getY() );
    }
    
    private void updateVectors() {

        _xComponentNode.setVisible( _laser.isRunning() );
        _yComponentNode.setVisible( _laser.isRunning() );

        if ( _laser.isRunning() ) {

            // calcuate the trap force vector at the bead's position
            Point2D beadPosition = _bead.getPositionRef();
            Vector2D f = _laser.getTrapForce( beadPosition );

            // update the x & y component vectors
            _vectorNode.setVector( f );
            if ( SHOW_XY_COMPONENTS ) {
                _xComponentNode.setVectorXY( f.getX(), 0 );
                _yComponentNode.setVectorXY( 0, f.getY() );
            }
        }
    }
}
