package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.buildanatom.model.Electron;
import edu.colorado.phet.buildanatom.model.ElectronShell;
import edu.colorado.phet.buildanatom.model.SubatomicParticle;
import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.buildanatom.view.ElectronNode;
import edu.colorado.phet.buildanatom.view.ElectronShellNode;
import edu.colorado.phet.buildanatom.view.NeutronNode;
import edu.colorado.phet.buildanatom.view.ProtonNode;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.umd.cs.piccolo.PNode;

/**
 * This shows the model of the atom (e.g. nucleus + shells + particles)
* @author Sam Reid
*/
public class GameAtomModelNode extends PNode {
    PNode backLayer = new PNode();
    PNode particleLayer = new PNode();
    private ModelViewTransform2D mvt;

    GameAtomModelNode( AtomValue atomValue ) {
        //user shouldn't be able to modify the described problem
        setPickable( false );
        setChildrenPickable( false );

        mvt = new ModelViewTransform2D( new Point2D.Double( 0, 0 ),
                                        new Point( (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.width * 0.30 ),
                                                   (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.height * 0.45 ) ),
                                        2.0, true );
        // Add the atom's electron shells to the canvas.
        final Atom atom = atomValue.toAtom();
        for ( ElectronShell electronShell : atom.getElectronShells() ) {
            backLayer.addChild( new ElectronShellNode( mvt, new BooleanProperty( true ), atom, electronShell, true ) );
        }
        addChild( backLayer );
        addChild( particleLayer );

        // Add the subatomic particles.
        final ArrayList<Electron> electrons = atom.getElectrons();
        for ( int i = 0; i < electrons.size(); i++ ) {
            electrons.get(i).moveToDestination();
            particleLayer.addChild( new ElectronNode( mvt, electrons.get(i )) );
        }

        //TODO: very different logic here than in BAACanvas because it assumed existence of BAAModel and other things
        for ( int i = 0; i < Math.max( atom.getNumProtons(), atom.getNumNeutrons() ); i++ ) {
            if ( i < atom.getNumProtons()) {
                final SubatomicParticle proton = atom.getProton( i );
                proton.moveToDestination();
                particleLayer.addChild( new ProtonNode( mvt, proton ) );
            }
            if ( i < atom.getNumNeutrons() ) {
                final SubatomicParticle particle = atom.getNeutron( i );
                particle.moveToDestination();
                particleLayer.addChild( new NeutronNode( mvt, particle ) );
            }
        }
    }
}
