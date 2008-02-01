/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.Valley;
import edu.colorado.phet.glaciers.model.Glacier.GlacierListener;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * GlacierNode is the visual representation of a glacier.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlacierNode extends PComposite {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Glacier _glacier;
    private GlacierListener _glacierListener;
    private ModelViewTransform _mvt;
    
    private GeneralPath _icePath;
    private PPath _icePathNode;
    private Point2D _pModel, _pView; // reusable points
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GlacierNode( Glacier glacier, ModelViewTransform mvt ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        _glacier = glacier;
        _glacierListener = new GlacierListener() {

            public void iceThicknessChanged() {
                updateIceThickness();
                
            }

            public void iceVelocityChanged() {
                //XXX
            }
        };
        _glacier.addGlacierListener( _glacierListener );
        
        _mvt = mvt;
        
        _icePath = new GeneralPath();
        _icePathNode = new PPath( _icePath );
        _icePathNode.setStroke( null );
        _icePathNode.setPaint( GlaciersConstants.ICE_COLOR );
        addChild( _icePathNode );
        
        _pModel = new Point2D.Double();
        _pView = new Point2D.Double();
        
        // initialization
        updateIceThickness();
    }
    
    public void cleanup() {
        _glacier.removeGlacierListener( _glacierListener );
    }
    
    private void updateIceThickness() {
        
        //XXX disabled, prohibitively expensive, model needs redesign
        if ( true ) return;
        
        final double dx = Glacier.getDx();
        final double x0 = Glacier.getX0();
        final double xTerminus = _glacier.getTerminusX();
        Valley valley = _glacier.getValley();
        double elevation = 0;
        System.out.println( "GlacierNode.updateIce x0=" + x0 + " xTerminus=" + xTerminus + " dx=" + dx );//XXX
        
        // reset the reusable path
        _icePath.reset();
        
        // move downvalley, draw ice-rock boundary
        for ( double x = x0; x <= xTerminus; x += dx ) {
            elevation = valley.getElevation( x );
            _pModel.setLocation( x, elevation );
            _mvt.modelToView( _pModel, _pView );
            if ( x == x0 ) {
                _icePath.moveTo( (float) _pView.getX(), (float) _pView.getY() );
            }
            else {
                _icePath.lineTo( (float) _pView.getX(), (float) _pView.getY() );
            }
        }
        System.out.println( "GlacierNode.updateIce create ice-rock boundary" );//XXX
        
        // move upvalley, draw ice-air boundary
        double iceSurface = 0;
        for ( double x = xTerminus; x >= 0; x -= dx ) {
            System.out.println( "GlacierNode.updateIce calculating iceSurface at x=" + x );//XXX
            iceSurface = valley.getElevation( x ) + _glacier.getIceThickness( x );
            _pModel.setLocation( x, iceSurface );
            _mvt.modelToView( _pModel, _pView );
            _icePath.lineTo( (float) _pView.getX(), (float) _pView.getY() );
        }
        System.out.println( "GlacierNode.updateIce create ice-air boundary" );//XXX
        
        // close the path
        _icePath.closePath();
        
        _icePathNode.setPathTo( _icePath );
    }
}
