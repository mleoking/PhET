// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.view;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.chemicalreactions.model.Atom;
import edu.colorado.phet.chemicalreactions.model.Molecule;
import edu.colorado.phet.chemistry.nodes.LabeledAtomNode;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.MODEL_VIEW_TRANSFORM;

public class MoleculeNode extends PNode {
    public MoleculeNode( final Molecule molecule ) {
        for ( final Atom atom : molecule.getAtoms() ) {
            addChild( new LabeledAtomNode( atom.getElement() ) {{
                scale( MODEL_VIEW_TRANSFORM.modelToViewDeltaX( 1 ) );
                atom.position.addObserver( new SimpleObserver() {
                    public void update() {
                        final ImmutableVector2D modelPosition = atom.position.get();
                        final ImmutableVector2D viewPosition = MODEL_VIEW_TRANSFORM.modelToView( modelPosition );
                        setOffset( viewPosition.getX(), viewPosition.getY() );
                    }
                } );
            }} );
        }

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PDragEventHandler() {
            @Override public void mouseDragged( PInputEvent event ) {
                final PDimension delta = event.getDeltaRelativeTo( getParent() );
                final Dimension2D modelDelta = MODEL_VIEW_TRANSFORM.viewToModelDelta( delta );
                molecule.setPosition( molecule.position.get().plus( modelDelta ) );
            }

            @Override protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                molecule.userControlled.set( true );
            }

            @Override protected void endDrag( PInputEvent event ) {
                super.endDrag( event );
                molecule.userControlled.set( false );
            }
        } );
    }
}
