/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.Glacier.GlacierAdapter;
import edu.colorado.phet.glaciers.model.Glacier.GlacierListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;


public class IceVelocitiesNode extends PComposite {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double DX = 100;
    private static final double DZ = 10;
    private static final double UNIT_VECTOR_LENGTH = 1; // length (in meters) of a 1 meter/year velocity
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Glacier _glacier;
    private GlacierListener _glacierListener;
    private ModelViewTransform _mvt;
    private PNode _parentNode;
    private boolean _isDirty;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public IceVelocitiesNode( Glacier glacier, ModelViewTransform mvt ) {
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
        _isDirty = true;
        
        updateVectors();
    }
    
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( _isDirty ) {
            updateVectors();
        }
    }
    
    private void updateVectors() {
        
        if ( !getVisible() ) {
            _isDirty = true;
        }
        else {
//            _parentNode.removeAllChildren();
//            final double xTerminus = _glacier.getTerminusX();
//            double x = Glacier.getX0();
//            while ( x <= xTerminus ) {
//                double valleyFloorElevation = _glacier.getValley().getElevation( x );
//                double iceSurfaceElevation = valleyFloorElevation + _glacier.getIceThickness( x );
//                double z = valleyFloorElevation;
//                while ( z <= iceSurfaceElevation ) {
//                    Vector2D velocity = _glacier.getIceVelocity( x, z );
//                    PNode velocityNode = createVector( x, z, velocity, _mvt );
//                    _parentNode.addChild( velocityNode );
//                    z += DZ;
//                }
//                x += DX;
//            }
            _isDirty = false;
        }
    }
    
    private PNode createVector( double x, double z, Vector2D v, ModelViewTransform mvt ) {
        //XXX put an 'x' at the point where the vector will be
        PText textNode = new PText( "x" );
        textNode.setOffset( _mvt.modelToView( x, z ) );
        return textNode;
    }
}
