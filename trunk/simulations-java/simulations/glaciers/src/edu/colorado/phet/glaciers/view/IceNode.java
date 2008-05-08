/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.glaciers.GlaciersImages;
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
    private GeneralPath _surfacePath, _surfaceBelowELAPath;
    private PPath _surfaceNode, _surfaceBelowELANode;
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
        _crossSectionNode.setPaint( createCrossSectionPaint() );
        _crossSectionNode.setStroke( null );
        addChild( _crossSectionNode );
        
        _surfacePath = new GeneralPath();
        _surfaceNode = new PPath( _surfacePath );
        _surfaceNode.setPaint( createSurfaceAboveELAPaint() );
        _surfaceNode.setStroke( null );
        addChild( _surfaceNode );
        
        _surfaceBelowELAPath = new GeneralPath();
        _surfaceBelowELANode = new PPath( _surfacePath );
        _surfaceBelowELANode.setPaint( createSurfaceBelowELAPaint() );
        _surfaceBelowELANode.setStroke( null );
        addChild( _surfaceBelowELANode );
        
        _pModel = new Point2D.Double();
        _pView = new Point2D.Double();
        
        // initialization
        update();
    }
    
    public void cleanup() {
        _glacier.removeGlacierListener( _glacierListener );
    }
    
    private static Paint createCrossSectionPaint() {
        final BufferedImage texture = GlaciersImages.ICE_CROSS_SECTION_TEXTURE;
        final Rectangle2D anchorRect = new Rectangle2D.Double( 0, 0, texture.getWidth(), texture.getHeight() );
        return new TexturePaint( texture, anchorRect );
    }
    
    private static Paint createSurfaceAboveELAPaint() {
        final BufferedImage texture = GlaciersImages.ICE_SURFACE_ABOVE_ELA_TEXTURE;
        final Rectangle2D anchorRect = new Rectangle2D.Double( 0, 0, texture.getWidth(), texture.getHeight() );
        return new TexturePaint( texture, anchorRect );
    }
    
    private static Paint createSurfaceBelowELAPaint() {
        final BufferedImage texture = GlaciersImages.ICE_SURFACE_BELOW_ELA_TEXTURE;
        final Rectangle2D anchorRect = new Rectangle2D.Double( 0, 0, texture.getWidth(), texture.getHeight() );
        return new TexturePaint( texture, anchorRect );
    }
    
    private void update() {

        // reset the reusable paths
        _crossSectionPath.reset();
        _surfacePath.reset();
        _surfaceBelowELAPath.reset();

        double[] iceThicknessSamples = _glacier.getIceThicknessSamples();
        if ( iceThicknessSamples != null && iceThicknessSamples.length > 0 ) {
            
            Valley valley = _glacier.getValley();
            double elevation = 0;
            
            // cross-section
            {
                double x = Glacier.getMinX();;
                final double dx = Glacier.getDx();
                
                // ice-air boundary, moving downvalley
                for ( int i = 0; i < iceThicknessSamples.length; i++ ) {
                    elevation = valley.getElevation( x ) + iceThicknessSamples[i];
                    _pModel.setLocation( x, elevation );
                    _mvt.modelToView( _pModel, _pView );
                    if ( i == 0 ) {
                        _crossSectionPath.moveTo( (float) _pView.getX(), (float) _pView.getY() );
                    }
                    else {
                        _crossSectionPath.lineTo( (float) _pView.getX(), (float) _pView.getY() );
                    }
                    x += dx;
                }

                // ice-rock boundary, moving upvalley
                x = x - dx;
                for ( int i = iceThicknessSamples.length - 1; i >= 0; i-- ) {
                    elevation = valley.getElevation( x );
                    _pModel.setLocation( x, elevation );
                    _mvt.modelToView( _pModel, _pView );
                    _crossSectionPath.lineTo( (float) _pView.getX(), (float) _pView.getY() );
                    x -= dx;
                }

                _crossSectionPath.closePath();
            }

            // complete surface (above and below ELA)
            {
                final double dx = Glacier.getDx();
                double x = Glacier.getMinX();;

                // ice-air boundary, moving downvalley
                for ( int i = 0; i < iceThicknessSamples.length; i++ ) {
                    elevation = valley.getElevation( x ) + iceThicknessSamples[i];
                    _pModel.setLocation( x, elevation );
                    _mvt.modelToView( _pModel, _pView );
                    if ( i == 0 ) {
                        _surfacePath.moveTo( (float) _pView.getX(), (float) _pView.getY() );
                    }
                    else {
                        _surfacePath.lineTo( (float) _pView.getX(), (float) _pView.getY() );
                    }
                    x += dx;
                }
                
                // surface perspective, moving upvalley
                x = x - dx;
                final double perspectiveHeight = MountainsAndValleyNode.getPerspectiveHeight();
                for ( int i = iceThicknessSamples.length - 1; i >= 0; i-- ) {
                    elevation = valley.getElevation( x ) + iceThicknessSamples[i] + perspectiveHeight;
                    _pModel.setLocation( x, elevation );
                    _mvt.modelToView( _pModel, _pView );
                    _surfacePath.lineTo( (float) _pView.getX(), (float) _pView.getY() );
                    x -= dx;
                }

                _surfacePath.closePath();
            }

            // surface below ELA 
            //XXX this uses a more precise (but costlier?) method of determining points, might not match shapes above
            final Point2D surfaceAtELA = _glacier.getSurfaceAtSteadyStateELAReference();
            if ( surfaceAtELA != null )
            {
                final double xSurface = surfaceAtELA.getX();
                final double xTerminus = _glacier.getTerminusReference().getX();
                final double dx = 1;
                
                // ice-air boundary, moving downvalley
                boolean first = true;
                for ( double x = xSurface; x <= xTerminus; x += dx ) {
                    elevation = _glacier.getSurfaceElevation( x );
                    _pModel.setLocation( x, elevation );
                    _mvt.modelToView( _pModel, _pView );
                    if ( first ) {
                        _surfaceBelowELAPath.moveTo( (float) _pView.getX(), (float) _pView.getY() );
                        first = false;
                    }
                    else {
                        _surfaceBelowELAPath.lineTo( (float) _pView.getX(), (float) _pView.getY() );
                    }
                }
                
                final double perspectiveHeight = MountainsAndValleyNode.getPerspectiveHeight();
                elevation = _glacier.getSurfaceElevation( xTerminus ) + perspectiveHeight;
                _pModel.setLocation( xTerminus, elevation );
                _mvt.modelToView( _pModel, _pView );
                _surfaceBelowELAPath.lineTo( (float) _pView.getX(), (float) _pView.getY() );
                
                // surface perspective, moving upvalley
                for ( double x = xTerminus; x >= xSurface; x -= dx ) {
                    elevation = _glacier.getSurfaceElevation( x ) + perspectiveHeight;
                    _pModel.setLocation( x, elevation );
                    _mvt.modelToView( _pModel, _pView );
                    _surfaceBelowELAPath.lineTo( (float) _pView.getX(), (float) _pView.getY() );
                }

                _surfaceBelowELAPath.closePath();
            }
        }
        
        _crossSectionNode.setPathTo( _crossSectionPath );
        _surfaceNode.setPathTo( _surfacePath );
        _surfaceBelowELANode.setPathTo( _surfaceBelowELAPath );
    }
}
