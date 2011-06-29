package edu.colorado.phet.moleculeshapes.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.ShadedSphereNode;
import edu.colorado.phet.moleculeshapes.model.Atom;
import edu.colorado.phet.moleculeshapes.model.ImmutableVector3D;

public class AtomNode extends ShadedSphereNode implements MSNode {
    private final Atom atom;

    public AtomNode( final Atom atom ) {
        super( atom.radius * 2, Color.GRAY );
        this.atom = atom;

        atom.position.addObserver( new SimpleObserver() {
            public void update() {
                // orthographic for now
                ImmutableVector2D position2d = Projection.project( atom.position.get() );
                setOffset( position2d.getX(), position2d.getY() );
            }
        } );
    }

    public ImmutableVector3D getCenter() {
        return atom.position.get();
    }
}
