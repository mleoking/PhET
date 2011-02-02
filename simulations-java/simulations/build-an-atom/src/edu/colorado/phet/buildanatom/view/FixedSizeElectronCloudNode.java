// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that represents the electron shell in an atom as a "cloud" that is
 * always the same size and opacity regardless of the number of electrons
 * it represents.  This is a sort of fixed size Schroedinger representation.
 * This was created specifically for the Isotopes and Atomic Mass simulation
 * to make it possible to make an atom appear that it is sitting on a scale.
 *
 * @author John Blanco
 */
public class FixedSizeElectronCloudNode extends PNode {

    // Base color to use when drawing clouds.
    private static final Color CLOUD_BASE_COLOR = Color.BLUE;

    // Cloud version of the representation.
    private final PhetPPath electronCloudNode;

    /**
     * Constructor.
     */
    public FixedSizeElectronCloudNode( final ModelViewTransform2D mvt, final OrbitalViewProperty orbitalView, final Atom atom ) {

        // This representation is always the diameter of the outermost
        // electron shell, which is assumed to be the last one on the list
        // supplied by the atom.
        final double radius = atom.getElectronShells().get( atom.getElectronShells().size() - 1 ).getRadius();
        Shape electronShellShape = mvt.createTransformedShape( new Ellipse2D.Double( atom.getPosition().getX() - radius,
                atom.getPosition().getY() - radius, radius * 2, radius * 2) );

        // Create and add the node that represents the electrons.
        electronCloudNode = new PhetPPath( electronShellShape, Color.BLUE, new BasicStroke(2), Color.GREEN ){{
            orbitalView.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( orbitalView.getValue() == OrbitalView.FIXED_SIZE_CLOUD );
                }
            } );
        }};

        // IMPORTANT NOTE: This node's sister class, which at the time of this
        // writing is called ResizingElectronCloudNode, contains code to
        // listen for mouse events to allow electrons to be extracted from
        // the cloud, and thus the atom.  This isn't needed at this time in
        // this class since this node is never used in a situation where
        // electrons are pulled from it.  If this ever changes, the code from
        // the sister class should be pulled in or otherwise shared (i.e.
        // through refactoring) here.

        electronCloudNode.setPickable( false );
        addChild( electronCloudNode );
    }
}
