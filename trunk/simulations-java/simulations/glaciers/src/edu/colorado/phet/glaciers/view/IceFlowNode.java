// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.glaciers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.piccolophet.nodes.Vector2DNode;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.Glacier.GlacierAdapter;
import edu.colorado.phet.glaciers.model.Glacier.GlacierListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * IceFlowNode is the visual representation of ice flow in the glacier.
 * Ice flow is represented as a grid of vectors in the ice.
 * Each vector represents the speed and direction of ice flow at that point.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IceFlowNode extends PComposite {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final double DX = 400; // horizontal distance between velocity samples (meters)
    private static final double DZ = 50; // elevation between velocity samples (meters)
    private static final double ICE_ROCK_MARGIN = 20; // meters
    private static final double ICE_AIR_MARGIN = 0; // meters

    private static final double VELOCITY_VECTOR_SCALE = 5; // scale vectors by this much
    private static final double VELOCITY_VECTOR_HEAD_HEIGHT = 4;
    private static final double VELOCITY_VECTOR_HEAD_WIDTH = 4;
    private static final double VELOCITY_VECTOR_TAIL_WIDTH = 1;
    private static final Stroke VELOCITY_VECTOR_STROKE = new BasicStroke( 1f );
    private static final Paint VELOCITY_VECTOR_STROKE_PAINT = null;
    private static final Paint VELOCITY_VECTOR_FILL_PAINT = Color.BLACK;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final Glacier _glacier;
    private final GlacierListener _glacierListener;
    private final GlaciersModelViewTransform _mvt;
    private final PNode _parentNode;
    private boolean _isDirty;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public IceFlowNode( Glacier glacier, GlaciersModelViewTransform mvt ) {
        super();

        setPickable( false );
        setChildrenPickable( false );

        _glacier = glacier;
        _glacierListener = new GlacierAdapter() {
            public void iceThicknessChanged() {
                updateVectors();
            }
        };
        _glacier.addGlacierListener( _glacierListener );

        _mvt = mvt;

        _parentNode = new PComposite();
        _parentNode.setOffset( 0, 0 );
        addChild( _parentNode );

        _isDirty = true;

        updateVectors();
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( _isDirty ) {
            updateVectors();
        }
    }

    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------

    private void updateVectors() {

        if ( !getVisible() ) {
            _isDirty = true;
        }
        else {
            MutableVector2D outputVector = new MutableVector2D();
            Point2D outputPoint = new Point2D.Double();

            _parentNode.removeAllChildren();
            final double xTerminus = _glacier.getTerminusX();
            double x = _glacier.getHeadwallX();
            while ( x <= xTerminus ) {
                double valleyFloorElevation = _glacier.getValley().getElevation( x );
                double iceSurfaceElevation = valleyFloorElevation + _glacier.getIceThickness( x );
                double z = valleyFloorElevation + ICE_ROCK_MARGIN;
                while ( z <= iceSurfaceElevation - ICE_AIR_MARGIN ) {
                    MutableVector2D vModel = _glacier.getIceVelocity( x, z, outputVector );
                    Point2D vView = _mvt.modelToView( vModel.getX(), vModel.getY(), outputPoint );
                    PNode velocityNode = new VelocityVectorNode( vView.getX(), vView.getY(), VELOCITY_VECTOR_SCALE );
                    _parentNode.addChild( velocityNode );
                    velocityNode.setOffset( _mvt.modelToView( x, z, outputPoint ) );
                    z += DZ;
                }
                x += DX;
            }
            _isDirty = false;
        }
    }

    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------

    /*
    * VelocityVectorNode encapsulates the "look" of an ice velocity vector.
    */
    private static class VelocityVectorNode extends Vector2DNode {
        public VelocityVectorNode( double x, double y, double scale ) {
            super( x, y, 1, scale );
            setHeadSize( VELOCITY_VECTOR_HEAD_WIDTH, VELOCITY_VECTOR_HEAD_HEIGHT );
            setTailWidth( VELOCITY_VECTOR_TAIL_WIDTH );
            setArrowFillPaint( VELOCITY_VECTOR_FILL_PAINT );
            setArrowStroke( VELOCITY_VECTOR_STROKE );
            setArrowStrokePaint( VELOCITY_VECTOR_STROKE_PAINT );
            setFractionalHeadHeight( 0.5 );
            setValueVisible( false );
        }
    }

    //----------------------------------------------------------------------------
    // Icon
    //----------------------------------------------------------------------------

    /**
     * Creates an icon to represent this node.
     * The icon consists of a column of 3 vectors, left aligned.
     * Vectors near the top are longer than vectors near the bottom.
     *
     * @return Icon
     */
    public static Icon createIcon() {
        PNode parentNode = new PNode();
        VelocityVectorNode vectorNode;
        final double ySpacing = 4;

        vectorNode = new VelocityVectorNode( 30, 0, 1 );
        vectorNode.setOffset( 0, 0 );
        parentNode.addChild( vectorNode );

        vectorNode = new VelocityVectorNode( 20, 0, 1 );
        vectorNode.setOffset( 0, ySpacing );
        parentNode.addChild( vectorNode );

        vectorNode = new VelocityVectorNode( 10, 0, 1 );
        vectorNode.setOffset( 0, 2 * ySpacing );
        parentNode.addChild( vectorNode );

        Image image = parentNode.toImage();
        return new ImageIcon( image );
    }
}
