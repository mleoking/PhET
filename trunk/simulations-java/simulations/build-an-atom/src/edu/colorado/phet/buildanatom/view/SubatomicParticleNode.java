package edu.colorado.phet.buildanatom.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.model.SubatomicParticle;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.umd.cs.piccolo.PNode;

public class SubatomicParticleNode extends PNode {
    private ModelViewTransform2D mvt;
    private final SphericalNode sphericalNode;
    private SubatomicParticle subatomicParticle;
    
    public SubatomicParticleNode( ModelViewTransform2D mvt, SubatomicParticle subatomicParticle, final Color baseColor ) {
        this.mvt = mvt;
        this.subatomicParticle = subatomicParticle;
        sphericalNode = new SphericalNode( mvt.modelToViewDifferentialX( subatomicParticle.getDiameter() ), new SubatomicParticleGradient( subatomicParticle.getRadius(), baseColor ), false );
        addChild( sphericalNode );

        updatePosition();
    }
    private void updatePosition() {
        Point2D location = mvt.modelToViewDouble( subatomicParticle.getPosition() );
        sphericalNode.setOffset( location );
    }
}
