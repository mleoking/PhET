package edu.colorado.phet.glaciers.view;

import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.Valley;
import edu.colorado.phet.glaciers.model.Glacier.GlacierAdapter;
import edu.colorado.phet.glaciers.model.Glacier.GlacierListener;
import edu.colorado.phet.glaciers.util.UnitsConverter;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Patch of snow between the terminus and ELA, when the ELA is below the terminus.
 * When the ELA is above or at the terminus, this snow patch doesn't exist.
 * <p>
 * There is no model for the shape of this patch.
 * From the model's point of view this patch does not exist, and have zero thickness.
 * Tools will behave as if the patch doesn't exist.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SnowPatchNode extends PComposite {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double THICKNESS_AT_TERMINUS = UnitsConverter.feetToMeters( 50 ); // meters
    private static final double THICKNESS_AT_ELA = 0; // meters
    private static final double DX = 80; // x distance between sample points (meters)
    private static final Color CROSS_SECTION_COLOR = Color.LIGHT_GRAY;
    private static final Color SURFACE_COLOR = IceNode.SURFACE_COLOR;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Glacier _glacier;
    private final GlacierListener _glacierListener;
    private final ModelViewTransform _mvt;
    
    private final GeneralPath _crossSectionPath, _surfacePath;
    private final PPath _crossSectionNode, _surfaceNode;
    private final Point2D _pModel, _pView; // reusable points
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public SnowPatchNode( Glacier glacier, ModelViewTransform mvt ) {
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
        
        // generate geometry if the ELA is below terminus
        final double yELA = _glacier.getClimate().getELA();
        final double yTerminus = _glacier.getTerminusY();
        if ( yELA < yTerminus ) {
            
            final double xELA = _glacier.getValley().getX( yELA );
            final double xTerminus = _glacier.getTerminusX();
            final double patchLength = xELA - xTerminus;
            assert( patchLength > 0 );
            
            Valley valley = _glacier.getValley();
            
            // move downvalley, the ice-air boundary is shared by both paths
            assert( THICKNESS_AT_TERMINUS > THICKNESS_AT_ELA );
            final double dy = ( THICKNESS_AT_TERMINUS - THICKNESS_AT_ELA ) * DX / patchLength;
            double thickness = THICKNESS_AT_TERMINUS;
            for ( double x = xTerminus; x <= xELA; x += DX ) {
                
                _pModel.setLocation( x, valley.getElevation( x ) + thickness );
                _mvt.modelToView( _pModel, _pView );
                
                if ( x == xTerminus ) {
                    _crossSectionPath.moveTo( (float) _pView.getX(), (float) _pView.getY() );
                    _surfacePath.moveTo( (float) _pView.getX(), (float) _pView.getY() );
                }
                else {
                    _crossSectionPath.lineTo( (float) _pView.getX(), (float) _pView.getY() );
                    _surfacePath.lineTo( (float) _pView.getX(), (float) _pView.getY() );
                }
                
                // Ensure that our last sample is exactly at the ELA, 
                // in case the patch's length isn't an integer multiple of DX.
                if ( patchLength > DX ) {
                    double diff = xELA - x;
                    if ( diff > 0.1 && diff < DX ) {
                        x = xELA - DX;
                    }
                }
                 
                thickness = Math.max( thickness - dy, THICKNESS_AT_ELA );
            }
            
            // moving upvalley...
            for ( double x = xELA; x >= xTerminus; x -= DX ) {

                // ice-rock boundary
                _pModel.setLocation( x, _glacier.getValley().getElevation( x ) );
                _mvt.modelToView( _pModel, _pView );
                _crossSectionPath.lineTo( (float) _pView.getX(), (float) _pView.getY() );

                /*
                 * Surface perspective, placed directly on the valley floor.
                 * While the cross-section has some thickness, we're placing this directly 
                 * on the valley floor so that it lines up with the ice at the terminus.  
                 */
                final double surfaceX = x + GlaciersConstants.YAW_X_OFFSET;
                double elevation = valley.getElevation( x ) + GlaciersConstants.PITCH_Y_OFFSET;
                _pModel.setLocation( surfaceX, elevation );
                _mvt.modelToView( _pModel, _pView );
                _surfacePath.lineTo( (float) _pView.getX(), (float) _pView.getY() );
                
                // Ensure that our last sample is exactly at the ELA, 
                // in case the patch's length isn't an integer multiple of DX.
                if ( patchLength > DX ) {
                    double diff = x - xTerminus;
                    if ( diff > 0.1 && diff < DX ) {
                        x = xTerminus + DX;
                    }
                }
            }
            
            // close the paths
            _crossSectionPath.closePath();
            _surfacePath.closePath();
        }
        
        // update the nodes
        _crossSectionNode.setPathTo( _crossSectionPath );
        _surfaceNode.setPathTo( _surfacePath );
    }
}
