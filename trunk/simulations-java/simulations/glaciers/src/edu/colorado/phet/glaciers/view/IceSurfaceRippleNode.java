/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.IceSurfaceRipple;
import edu.colorado.phet.glaciers.model.Glacier.GlacierAdapter;
import edu.colorado.phet.glaciers.model.Glacier.GlacierListener;
import edu.colorado.phet.glaciers.model.IceSurfaceRipple.IceSurfaceRippleAdapter;
import edu.colorado.phet.glaciers.model.IceSurfaceRipple.IceSurfaceRippleListener;
import edu.umd.cs.piccolo.nodes.PPath;


public class IceSurfaceRippleNode extends PPath {
    
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color COLOR_ABOVE_ELA = Color.RED;//XXX
    private static final Color COLOR_BELOW_ELA = Color.BLUE;//XXX
    
    private final Glacier _glacier;
    private final IceSurfaceRipple _ripple;
    private final GlacierListener _glacierListener;
    private final IceSurfaceRippleListener _rippleListener;
    private final ModelViewTransform _mvt;
    private final Point2D _pView;
    
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
        
        Point2D pModel = new Point2D.Double( 0, MountainsAndValleyNode.getPerspectiveHeight() );
        mvt.modelToView( pModel, _pView );
        final double height = _pView.getY();
        setPathTo( createPath( height ) );
        setStroke( STROKE );
        
        update();
    }
    
    public void cleanup() {
        _glacier.removeGlacierListener( _glacierListener );
        _ripple.removeIceSurfaceRippleListener( _rippleListener );
    }
    
    private static Shape createPath( double height ) {
        return new Line2D.Double( 0, 0, 0, height );
    }
    
    private void update() {
        
        System.out.println( "IceSurfaceRippleNode.update " + _ripple.getPositionReference() );//XXX
        
        // use different colors above and below ELA
        final Point2D surfaceAtELA = _glacier.getSurfaceAtSteadyStateELAReference();
        if ( surfaceAtELA != null && _ripple.getX() >= surfaceAtELA.getX() ) {
            setStrokePaint( COLOR_BELOW_ELA );
        }
        else {
            setStrokePaint( COLOR_ABOVE_ELA );
        }
        
        // position
        _mvt.modelToView( _ripple.getPositionReference(), _pView );
        setOffset( _pView.getX(), _pView.getY() );
    }
}
