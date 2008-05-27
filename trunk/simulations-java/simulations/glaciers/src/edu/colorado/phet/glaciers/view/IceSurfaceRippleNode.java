/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.IceSurfaceRipple;
import edu.colorado.phet.glaciers.model.Valley;
import edu.colorado.phet.glaciers.model.Glacier.GlacierAdapter;
import edu.colorado.phet.glaciers.model.Glacier.GlacierListener;
import edu.colorado.phet.glaciers.model.IceSurfaceRipple.IceSurfaceRippleAdapter;
import edu.colorado.phet.glaciers.model.IceSurfaceRipple.IceSurfaceRippleListener;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * IceSurfaceRippleNode is the visual representation of a "ripple" on the surface of the ice.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IceSurfaceRippleNode extends PComposite {
    
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color COLOR_ABOVE_ELA = new Color( 230, 230, 230 );
    private static final Color COLOR_BELOW_ELA = new Color( 200, 200, 200 );
    private static final double RIPPLE_WIDTH = 25; // meters
    private static final double MARGIN_PERCENTAGE = 0.2;
    
    private final Glacier _glacier;
    private final IceSurfaceRipple _ripple;
    private final GlacierListener _glacierListener;
    private final IceSurfaceRippleListener _rippleListener;
    private final ModelViewTransform _mvt;
    private final Point2D _pView;
    private final PPath _topArcNode, _bottomArcNode;
    
    public IceSurfaceRippleNode( IceSurfaceRipple ripple, Glacier glacier, ModelViewTransform mvt ) {
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
        
        _rippleListener = new IceSurfaceRippleAdapter() {
            public void positionChanged() {
                update();
            }
        };
        _ripple.addIceSurfaceRippleListener( _rippleListener );
        
        _pView = new Point2D.Double();
       
        Point2D pModel = new Point2D.Double( RIPPLE_WIDTH, 0 );
        mvt.modelToView( pModel, _pView );
        final double rippleWidth = Math.abs( _pView.getX() );
        final double arcWidth = 2 * rippleWidth;
        pModel = new Point2D.Double( 0, Valley.getPerspectiveHeight() );
        mvt.modelToView( pModel, _pView );
        final double surfaceHeight = Math.abs( _pView.getY() );
        final double margin = MARGIN_PERCENTAGE * surfaceHeight;
        final double rippleHeight = surfaceHeight - ( 2 * margin );
        Shape topArc = new Arc2D.Double( 0, 0, arcWidth, 0.35 * rippleHeight, 270, 180, Arc2D.OPEN ); // angles specified in degrees!
        Shape bottomArc = new Arc2D.Double( 0, 0, arcWidth, 0.65 * rippleHeight, 270, 180, Arc2D.OPEN );
        _topArcNode = new PPath( topArc );
        _topArcNode.setStroke( STROKE );
        _bottomArcNode = new PPath( bottomArc );
        _bottomArcNode.setStroke( STROKE );
        
        _bottomArcNode.setOffset( -arcWidth/2, -( _bottomArcNode.getFullBoundsReference().getHeight() + margin ) );
        _topArcNode.setOffset( -arcWidth/2, -( _topArcNode.getFullBoundsReference().getHeight() + _bottomArcNode.getFullBoundsReference().getHeight() + margin ) );
        addChild( _topArcNode );
        addChild( _bottomArcNode );
        
        update();
    }
    
    public void cleanup() {
        _glacier.removeGlacierListener( _glacierListener );
        _ripple.removeIceSurfaceRippleListener( _rippleListener );
    }
    
    private void update() {
        
        // use different colors above and below ELA
        final Point2D surfaceAtELA = _glacier.getSurfaceAtSteadyStateELAReference();
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
