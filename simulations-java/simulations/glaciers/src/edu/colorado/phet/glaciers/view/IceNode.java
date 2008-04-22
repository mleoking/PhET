/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.Valley;
import edu.colorado.phet.glaciers.model.Glacier.GlacierAdapter;
import edu.colorado.phet.glaciers.model.Glacier.GlacierListener;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * IceNode is the visual representation of the glacier ice.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IceNode extends PComposite {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Glacier _glacier;
    private GlacierListener _glacierListener;
    private ModelViewTransform _mvt;
    
    private GeneralPath _crossSectionPath;
    private PPath _crossSectionNode;
    private GeneralPath _surfacePath;
    private PPath _surfaceNode;
    private Point2D _pModel, _pView; // reusable points
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public IceNode( Glacier glacier, ModelViewTransform mvt ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        _glacier = glacier;
        _glacierListener = new GlacierAdapter() {
            public void iceThicknessChanged() {
                update();
            }
        };
        _glacier.addGlacierListener( _glacierListener );
        
        _mvt = mvt;
        
        _crossSectionPath = new GeneralPath();
        _crossSectionNode = new PPath( _crossSectionPath );
        _crossSectionNode.setStroke( null );
        _crossSectionNode.setPaint( GlaciersConstants.ICE_CROSS_SECTION_COLOR );
        addChild( _crossSectionNode );
        
        _surfacePath = new GeneralPath();
        _surfaceNode = new PPath( _surfacePath );
        _surfaceNode.setStroke( null );
        _surfaceNode.setPaint( GlaciersConstants.ICE_SURFACE_COLOR );
        addChild( _surfaceNode );
        
        _pModel = new Point2D.Double();
        _pView = new Point2D.Double();
        
        // initialization
        update();
    }
    
    public void cleanup() {
        _glacier.removeGlacierListener( _glacierListener );
    }
    
    private void update() {
        
        final double x0 = Glacier.getMinX();
        final double dx = Glacier.getDx();
        Valley valley = _glacier.getValley();
        
        double x = x0;
        double elevation = 0;
        
        // reset the reusable paths
        _crossSectionPath.reset();
        _surfacePath.reset();

        double[] iceThicknessSamples = _glacier.getIceThicknessSamples();
        if ( iceThicknessSamples != null && iceThicknessSamples.length > 0 ) {

            // move downvalley, draw ice-air boundary
            for ( int i = 0; i < iceThicknessSamples.length; i++ ) {
                elevation = valley.getElevation( x ) + iceThicknessSamples[i];
                _pModel.setLocation( x, elevation );
                _mvt.modelToView( _pModel, _pView );
                if ( i == 0 ) {
                    _crossSectionPath.moveTo( (float) _pView.getX(), (float) _pView.getY() );
                    _surfacePath.moveTo( (float) _pView.getX(), (float) _pView.getY() );
                }
                else {
                    _crossSectionPath.lineTo( (float) _pView.getX(), (float) _pView.getY() );
                    _surfacePath.lineTo( (float) _pView.getX(), (float) _pView.getY() );
                }
                x += dx;
            }
            final double xTerminus = x - dx;
            
            // move upvalley, draw surface perspective
            x = xTerminus;
            final double perspectiveHeight = MountainsAndValleyNode.getPerspectiveHeight();
            for ( int i = 0; i < iceThicknessSamples.length; i++ ) {
                elevation = valley.getElevation( x ) + iceThicknessSamples[i] + perspectiveHeight;
                _pModel.setLocation( x, elevation );
                _mvt.modelToView( _pModel, _pView );
                _surfacePath.lineTo( (float) _pView.getX(), (float) _pView.getY() );
                x -= dx;
            }

            // move upvalley, draw cross-section ice-rock boundary
            x = xTerminus;
            for ( int i = 0; i < iceThicknessSamples.length; i++ ) {
                elevation = valley.getElevation( x );
                _pModel.setLocation( x, elevation );
                _mvt.modelToView( _pModel, _pView );
                _crossSectionPath.lineTo( (float) _pView.getX(), (float) _pView.getY() );
                x -= dx;
            }

            _crossSectionPath.closePath();
            _surfacePath.closePath();
        }
        
        _crossSectionNode.setPathTo( _crossSectionPath );
        _surfaceNode.setPathTo( _surfacePath );
    }
}
