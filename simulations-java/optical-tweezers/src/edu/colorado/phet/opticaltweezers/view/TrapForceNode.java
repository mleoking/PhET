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


public class TrapForceNode extends PComposite implements Observer {
    
    private static final boolean SHOW_VALUES = true;

    // properties of the vectors
    private static final double VECTOR_HEAD_HEIGHT = 20;
    private static final double VECTOR_HEAD_WIDTH = 20;
    private static final double VECTOR_TAIL_WIDTH = 5;
    private static final double VECTOR_MIN_TAIL_LENGTH = 2;
    private static final double VECTOR_MAX_TAIL_LENGTH = 125;
    private static final Stroke VECTOR_STROKE = new BasicStroke( 1f );
    private static final Paint VECTOR_STROKE_PAINT = Color.BLACK;
    private static final Paint VECTOR_FILL_PAINT = Color.GREEN;
    private static final double VECTOR_VALUE_SPACING = 2;
    
    private static final DecimalFormat VALUE_FORMAT = new DecimalFormat( "0.##E0" );
    
    private static final String UNITS_STRING = OTResources.getString( "units.trapForce" );
    
    private Laser _laser;
    private Bead _bead;
    private ModelViewTransform _modelViewTransform;
    private PPath _xComponentNode, _yComponentNode;
    private PText _xTextNode, _yTextNode;
    private double _fMax;
    
    public TrapForceNode( Laser laser, Bead bead, ModelViewTransform modelViewTransform ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _laser = laser;
        _laser.addObserver( this );
        
        _bead = bead;
        _bead.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        _xComponentNode = new PPath();
        _xComponentNode.setStroke( VECTOR_STROKE );
        _xComponentNode.setStrokePaint( VECTOR_STROKE_PAINT );
        _xComponentNode.setPaint( VECTOR_FILL_PAINT );
        
        _yComponentNode = new PPath();
        _yComponentNode.setStroke( VECTOR_STROKE );
        _yComponentNode.setStrokePaint( VECTOR_STROKE_PAINT );
        _yComponentNode.setPaint( VECTOR_FILL_PAINT );
        
        _xTextNode = new PText();
        _xTextNode.setTextPaint( Color.BLACK );
        
        _yTextNode = new PText();
        _yTextNode.setTextPaint( Color.BLACK );
        
        double x = _laser.getX() + ( _laser.getDiameterAtWaist() / 4 ); // halfway between center and edge of waist
        double y = _laser.getY();
        double maxPower = _laser.getPowerRange().getMax();
        Vector2D fMax = _laser.getTrapForce( x, y, maxPower );
        _fMax = Math.max( Math.abs( fMax.getX() ), Math.abs( fMax.getY() ) );
//        System.out.println( "TrapForceNode.init fMax=" + fMax );//XXX
        
        updatePosition();
        updateVectors();
    }
    
    public void cleanup() {
        _laser.deleteObserver( this );
        _bead.deleteObserver( this );
    }
    
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
    
    private void updatePosition() {
        Point2D position = _modelViewTransform.modelToView( _bead.getPositionRef() );
        setOffset( position.getX(), position.getY() );
    }
    
    private void updateVectors() {
        
        removeAllChildren();

        if ( _laser.isRunning() ) {
            
            // calcuate the trap force vector at the bead's position
            Point2D beadPosition = _bead.getPositionRef();
            Vector2D f = _laser.getTrapForce( beadPosition );
            double fx = f.getX();
            double fy = f.getY();
//            System.out.println( "TrapForceNode.updateVectors beadPosition=" + beadPosition + " laserPosition=" + _laser.getPositionRef() + " f=" + f );//XXX
            assert ( Math.abs( fx ) <= _fMax );
            assert ( Math.abs( fy ) <= _fMax );

            // x component of the trap force
            if ( fx != 0 ) {
                
                double length = vectorMagnitudeToArrowLength( fx, _fMax );
                Point2D tail = new Point2D.Double( 0, 0 );
                Point2D tip = new Point2D.Double( length, 0 );
                Arrow arrow = new Arrow( tail, tip, VECTOR_HEAD_HEIGHT, VECTOR_HEAD_WIDTH, VECTOR_TAIL_WIDTH );
                _xComponentNode.setPathTo( arrow.getShape() );
                addChild( _xComponentNode );
                
                if ( SHOW_VALUES ) {
                    String xText = VALUE_FORMAT.format( fx ) + " " + UNITS_STRING;
                    _xTextNode.setText( xText );
                    addChild( _xTextNode );
                    
                    double x = 0;
                    double y = -_yTextNode.getFullBounds().getHeight() / 2;
                    if ( fx > 0 ) {
                        // text to the right of the arrow
                        x = _xComponentNode.getFullBounds().getMaxX() + VECTOR_VALUE_SPACING;
                    }
                    else {
                        // text to the left of the arrow
                        x = _xComponentNode.getFullBounds().getX() - VECTOR_VALUE_SPACING - _xTextNode.getFullBounds().getWidth();

                    }
                    _xTextNode.setOffset( x, y );
                }
            }

            // y component of the trap force
            if ( fy != 0 ) {
                double length = vectorMagnitudeToArrowLength( fy, _fMax );
                Point2D tail = new Point2D.Double( 0, 0 );
                Point2D tip = new Point2D.Double( 0, length );
                Arrow arrow = new Arrow( tail, tip, VECTOR_HEAD_HEIGHT, VECTOR_HEAD_WIDTH, VECTOR_TAIL_WIDTH );
                _yComponentNode.setPathTo( arrow.getShape() );
                addChild( _yComponentNode );
                
                if ( SHOW_VALUES ) {
                    String yText = VALUE_FORMAT.format( fy ) + " " + UNITS_STRING;
                    _yTextNode.setText( yText );
                    addChild( _yTextNode );
                    
                    double x = -_yTextNode.getFullBounds().getWidth() / 2;
                    double y = 0;
                    if ( fy > 0 ) {
                        // text centered below arrow
                        y = _yComponentNode.getFullBounds().getMaxY() + VECTOR_VALUE_SPACING;
                    }
                    else {
                        // text centered above arrow
                        y = _yComponentNode.getFullBounds().getY() - VECTOR_VALUE_SPACING - _yTextNode.getFullBounds().getHeight();

                    }
                    _yTextNode.setOffset( x, y );
                }
            }
        }
    }
    
    private static double vectorMagnitudeToArrowLength( double magnitude, double maxMagnitude ) {
        double length = ( magnitude / maxMagnitude ) * ( VECTOR_MAX_TAIL_LENGTH - VECTOR_MIN_TAIL_LENGTH );
        if ( length > 0 ) {
            length = length + VECTOR_HEAD_HEIGHT + VECTOR_MIN_TAIL_LENGTH;
        }
        else {
            length = length - VECTOR_HEAD_HEIGHT - VECTOR_MIN_TAIL_LENGTH;
        }
        return length;
    }
}
