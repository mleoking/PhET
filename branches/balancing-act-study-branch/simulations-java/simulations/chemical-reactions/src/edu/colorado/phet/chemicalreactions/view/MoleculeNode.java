// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.view;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.chemicalreactions.ChemicalReactionsApplication;
import edu.colorado.phet.chemicalreactions.model.Atom;
import edu.colorado.phet.chemicalreactions.model.Molecule;
import edu.colorado.phet.chemistry.nodes.LabeledAtomNode;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.MODEL_VIEW_TRANSFORM;

public class MoleculeNode extends PNode {

    private List<LabeledAtomNode> atomNodes = new ArrayList<LabeledAtomNode>();

    public MoleculeNode( final Molecule molecule ) {
        for ( final Atom atom : molecule.getAtoms() ) {
            LabeledAtomNode atomNode = new LabeledAtomNode( atom.getElement() ) {{
                scale( MODEL_VIEW_TRANSFORM.modelToViewDeltaX( 1 ) );
                atom.position.addObserver( new SimpleObserver() {
                    public void update() {
                        final Vector2D modelPosition = atom.position.get();
                        final Vector2D viewPosition = MODEL_VIEW_TRANSFORM.modelToView( modelPosition );
                        setOffset( viewPosition.getX(), viewPosition.getY() );

                        if ( ChemicalReactionsApplication.ATOM_LABELS_ROTATE.get() ) {
                            rotateTo( molecule.getAngle() );
                        }
                        else {
                            rotateTo( 0 );
                        }
                    }
                } );
            }};
            addChild( atomNode );
            atomNodes.add( atomNode );
        }

        addInputEventListener( new CursorHandler() );

        molecule.disposeNotifier.addUpdateListener( new UpdateListener() {
            public void update() {
                setVisible( false );
            }
        }, false );
    }

    public void setLabelVisible( boolean visible ) {
        for ( LabeledAtomNode atomNode : atomNodes ) {
            atomNode.getLabelNode().setVisible( visible );
        }
    }
}
