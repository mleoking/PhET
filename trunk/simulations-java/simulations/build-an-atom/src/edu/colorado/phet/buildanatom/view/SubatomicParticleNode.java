package edu.colorado.phet.buildanatom.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.model.SubatomicParticle;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

public class SubatomicParticleNode extends PNode {
    private ModelViewTransform2D mvt;
    private final SphericalNode sphericalNode;
    private SubatomicParticle subatomicParticle;
    
    public SubatomicParticleNode( final ModelViewTransform2D mvt, final SubatomicParticle subatomicParticle, final Color baseColor ) {
        this.mvt = mvt;
        this.subatomicParticle = subatomicParticle;
        sphericalNode = new SphericalNode( mvt.modelToViewDifferentialX( subatomicParticle.getDiameter() ), new SubatomicParticleGradient( subatomicParticle.getRadius(), baseColor ), false );
        addChild( sphericalNode );

        updatePosition();
        
        addInputEventListener( new CursorHandler( ) );
        
        subatomicParticle.addListener( new SubatomicParticle.Listener() {
            public void positionChanged() {
                updatePosition();
            }
        } );
        addInputEventListener( new PBasicInputEventHandler(){
            @Override
            public void mouseDragged( PInputEvent event ) {
                PDimension delta=event.getDeltaRelativeTo( getParent() );
                Point2D modelDelta= mvt.viewToModelDifferential( delta.width,delta.height );
                subatomicParticle.translate(modelDelta.getX(),modelDelta.getY());
            }
        } );
    }
    private void updatePosition() {
        Point2D location = mvt.modelToViewDouble( subatomicParticle.getPosition() );
        sphericalNode.setOffset( location );
    }
}
