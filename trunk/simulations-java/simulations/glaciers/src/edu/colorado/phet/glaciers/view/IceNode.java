/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.model.Glacier;
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
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color CROSS_SECTION_COLOR = new Color( 207, 255, 255 ); // ice blue
    private static final Color SURFACE_COLOR = Color.WHITE;
    private static final Color SURFACE_BELOW_ELA_COLOR = new Color( 230, 230, 230 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Glacier _glacier;
    private final GlacierListener _glacierListener;
    private final ModelViewTransform _mvt;
    
    private final GeneralPath _crossSectionPath;
    private final PPath _crossSectionNode;
    private final GeneralPath _surfacePath, _surfaceBelowELAPath;
    private final PPath _surfaceNode, _surfaceBelowELANode;
    private final Point2D _pModel, _pView; // reusable points
    
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
        _crossSectionNode.setPaint( CROSS_SECTION_COLOR );
        _crossSectionNode.setStroke( null );
        addChild( _crossSectionNode );
        
        _surfacePath = new GeneralPath();
        _surfaceNode = new PPath( _surfacePath );
        _surfaceNode.setPaint( SURFACE_COLOR );
        _surfaceNode.setStroke( null );
        addChild( _surfaceNode );
        
        _surfaceBelowELAPath = new GeneralPath();
        _surfaceBelowELANode = new PPath( _surfacePath );
        _surfaceBelowELANode.setPaint( SURFACE_BELOW_ELA_COLOR );
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
    
    private void update() {

        // reset the paths
        _crossSectionPath.reset();
        _surfacePath.reset();
        _surfaceBelowELAPath.reset();

        if (  _glacier.getLength() > 0 ) {

            // constants
            final double dx = Glacier.getDx();
            final double xHeadwall = _glacier.getHeadwallX();
            final double xTerminus = _glacier.getTerminusX();
            final Point2D surfaceAtELA = _glacier.getSurfaceAtSteadyStateELAReference();
            final double perspectiveHeight = MountainsAndValleyNode.getPerspectiveHeight();
            
            // variables
            double elevation = 0;
            
            // move downvalley, the ice-air boundary is shared by all paths
            boolean initialzedSurfaceBelowELA = false;
            for ( double x = xHeadwall; x <= xTerminus; x += dx ) {

                elevation = _glacier.getSurfaceElevation( x );
                _pModel.setLocation( x, elevation );
                _mvt.modelToView( _pModel, _pView );

                if ( x == xHeadwall ) {
                    _crossSectionPath.moveTo( (float) _pView.getX(), (float) _pView.getY() );
                    _surfacePath.moveTo( (float) _pView.getX(), (float) _pView.getY() );
                }
                else {
                    _crossSectionPath.lineTo( (float) _pView.getX(), (float) _pView.getY() );
                    _surfacePath.lineTo( (float) _pView.getX(), (float) _pView.getY() );
                }

                if ( surfaceAtELA != null && x >= surfaceAtELA.getX() ) {
                    if ( !initialzedSurfaceBelowELA ) {
                        _surfaceBelowELAPath.moveTo( (float) _pView.getX(), (float) _pView.getY() );
                        initialzedSurfaceBelowELA = true;
                    }
                    else {
                        _surfaceBelowELAPath.lineTo( (float) _pView.getX(), (float) _pView.getY() );
                    }
                }
            }

            // moving upvalley...
            for ( double x = xTerminus; x >= xHeadwall; x -= dx ) {

                // ice-rock boundary
                elevation = _glacier.getValley().getElevation( x );
                _pModel.setLocation( x, elevation );
                _mvt.modelToView( _pModel, _pView );
                _crossSectionPath.lineTo( (float) _pView.getX(), (float) _pView.getY() );

                // surface perspective
                elevation = _glacier.getSurfaceElevation( x ) + perspectiveHeight;
                _pModel.setLocation( x, elevation );
                _mvt.modelToView( _pModel, _pView );
                _surfacePath.lineTo( (float) _pView.getX(), (float) _pView.getY() );
                if ( surfaceAtELA != null && x >= surfaceAtELA.getX() ) {
                    _surfaceBelowELAPath.lineTo( (float) _pView.getX(), (float) _pView.getY() );
                }
            }

            // close the paths
            _crossSectionPath.closePath();
            _surfacePath.closePath();
            if ( surfaceAtELA != null && xTerminus >= surfaceAtELA.getX() ) {
                _surfaceBelowELAPath.closePath();
            }
        }

        // update the nodes
        _crossSectionNode.setPathTo( _crossSectionPath );
        _surfaceNode.setPathTo( _surfacePath );
        _surfaceBelowELANode.setPathTo( _surfaceBelowELAPath );
    }
}
