package edu.colorado.phet.glaciers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.model.Borehole;
import edu.colorado.phet.glaciers.model.Borehole.BoreholeAdapter;
import edu.colorado.phet.glaciers.model.Borehole.BoreholeListener;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;


public class BoreholeNode extends PComposite {
    
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color STROKE_COLOR = Color.BLACK;

    private Borehole _borehole;
    private ModelViewTransform _mvt;
    private BoreholeListener _boreholeListener;
    private GeneralPath _path;
    private PPath _pathNode;
    private Point2D _pView;
    
    public BoreholeNode( Borehole borehole, ModelViewTransform mvt ) {
        super();
        
        _borehole = borehole;
        _mvt = mvt;
        
        _boreholeListener = new BoreholeAdapter() {
            public void evolved() {
                update();
            }
        };
        _borehole.addBoreholeListener( _boreholeListener );
        
        _path = new GeneralPath();
        _pathNode = new PPath();
        _pathNode.setStroke( STROKE );
        _pathNode.setStrokePaint( STROKE_COLOR );
        addChild( _pathNode );
        
        _pView = new Point2D.Double();
        
        update();
    }
    
    public void cleanup() {
        _borehole.removeBoreholeListener( _boreholeListener );
    }
    
    private void update() {
        _path.reset();
        Point2D[] points = _borehole.getPoints();
        if ( points != null && points.length > 1 ) {
            for ( int i = 0; i < points.length; i++ ) {
                _mvt.modelToView( points[i], _pView );
                if ( i == 0 ) {
                    _path.moveTo( (float) _pView.getX(), (float) _pView.getY() );
                }
                else {
                    _path.lineTo( (float) _pView.getX(), (float) _pView.getY() ); 
                }
            }
        }
        _pathNode.setPathTo( _path );
    }
}
