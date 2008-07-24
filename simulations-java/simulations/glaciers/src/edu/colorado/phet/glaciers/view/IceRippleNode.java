/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.IceRipple;
import edu.colorado.phet.glaciers.model.Glacier.GlacierAdapter;
import edu.colorado.phet.glaciers.model.Glacier.GlacierListener;
import edu.colorado.phet.glaciers.model.IceRipple.IceRippleAdapter;
import edu.colorado.phet.glaciers.model.IceRipple.IceRippleListener;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * IceRippleNode is the visual representation of a "ripple" on the surface of the ice.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IceRippleNode extends PComposite {
    
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color COLOR_ABOVE_ELA = new Color( 222, 222, 222 );
    private static final Color COLOR_BELOW_ELA = new Color( 200, 200, 200 );
    
    private final Glacier _glacier;
    private final IceRipple _ripple;
    private final GlacierListener _glacierListener;
    private final IceRippleListener _rippleListener;
    private final ModelViewTransform _mvt;
    private final Point2D _pView;
    private final PPath _topArcNode, _bottomArcNode;
    
    public IceRippleNode( IceRipple ripple, Glacier glacier, ModelViewTransform mvt ) {
        super();
        setPickable( false );
        setChildrenPickable( false );

        _glacier = glacier;
        _ripple = ripple;
        _mvt = mvt;
        
        _glacierListener = new GlacierAdapter() {
            public void iceThicknessChanged() {
                update();
            }
        };
        _glacier.addGlacierListener( _glacierListener );
        
        _rippleListener = new IceRippleAdapter() {
            public void positionChanged() {
                update();
            }
        };
        _ripple.addIceRippleListener( _rippleListener );
        
        Point2D pModel = new Point2D.Double();
        _pView = new Point2D.Double();
        
        Dimension rippleSize = _ripple.getSize();
        pModel.setLocation( rippleSize.getWidth(), rippleSize.getHeight() );
        mvt.modelToView( pModel, _pView );
        final double rippleWidth = _pView.getX();
        final double rippleHeight = Math.abs( _pView.getY() );
        
        pModel.setLocation( 0, _ripple.getZOffset() );
        mvt.modelToView( pModel, _pView );
        final double rippleZOffset = Math.abs( _pView.getY() );
        
        final double arcWidth = 2 * rippleWidth;
        final double startAngle = 270; // degrees
        final double extent = 180; // degrees
        Shape topArc = new Arc2D.Double( 0, 0, arcWidth, 0.35 * rippleHeight, startAngle, extent, Arc2D.OPEN );
        Shape bottomArc = new Arc2D.Double( 0, 0, arcWidth, 0.65 * rippleHeight, startAngle, extent, Arc2D.OPEN );
        _topArcNode = new PPath( topArc );
        _topArcNode.setStroke( STROKE );
        _bottomArcNode = new PPath( bottomArc );
        _bottomArcNode.setStroke( STROKE );
        
        _bottomArcNode.setOffset( -arcWidth/2, -( _bottomArcNode.getFullBoundsReference().getHeight() + rippleZOffset ) );
        _topArcNode.setOffset( -arcWidth/2, -( _topArcNode.getFullBoundsReference().getHeight() + _bottomArcNode.getFullBoundsReference().getHeight() + rippleZOffset ) );
        addChild( _topArcNode );
        addChild( _bottomArcNode );
        
        rotate( GlaciersConstants.YAW_ROTATION );
        
        update();
    }
    
    public void cleanup() {
        _glacier.removeGlacierListener( _glacierListener );
        _ripple.removeIceRippleListener( _rippleListener );
    }
    
    private void update() {
        
        // use different colors above and below ELA
        final Point2D surfaceAtELA = _glacier.getSurfaceAtELAReference();
        if ( surfaceAtELA != null && _ripple.getX() >= surfaceAtELA.getX() ) {
            _topArcNode.setStrokePaint( COLOR_BELOW_ELA );
            _bottomArcNode.setStrokePaint( COLOR_BELOW_ELA );
        }
        else {
            _topArcNode.setStrokePaint( COLOR_ABOVE_ELA );
            _bottomArcNode.setStrokePaint( COLOR_ABOVE_ELA );
        }
        
        // position
        _mvt.modelToView( _ripple.getPositionReference(), _pView );
        setOffset( _pView.getX(), _pView.getY() );
    }
}
