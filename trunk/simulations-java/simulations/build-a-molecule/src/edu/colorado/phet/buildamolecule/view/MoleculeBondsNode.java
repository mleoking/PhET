//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.buildamolecule.model.AtomModel;
import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.MoleculeStructure;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.buildamolecule.model.LewisDotModel.Direction;

/**
 * Handles "bond breaking" nodes for a single molecule
 */
public class MoleculeBondsNode extends PNode {

    /**
     * "Radius" of the bond target that will break the bond
     */
    public static final double BOND_RADIUS = 5;

    public MoleculeBondsNode( final Kit kit, MoleculeStructure moleculeStructure, final ModelViewTransform mvt ) {

        for ( MoleculeStructure.Bond bond : moleculeStructure.getBonds() ) {
            final AtomModel a = kit.getAtomModel( bond.a );
            final AtomModel b = kit.getAtomModel( bond.b );

            Direction bondDirection = kit.getBondDirection( a.getAtomInfo(), b.getAtomInfo() );
            final boolean isHorizontal = bondDirection == Direction.West || bondDirection == Direction.East;

            /*---------------------------------------------------------------------------*
            * bond node
            * TODO: separate class? consolidate molecule parts?
            *----------------------------------------------------------------------------*/
            addChild( new PNode() {
                {
                    // hit target
                    addChild( new PhetPPath( new Ellipse2D.Double( -BOND_RADIUS, -BOND_RADIUS, 2 * BOND_RADIUS, 2 * BOND_RADIUS ) ) {{
                        setPaint( Color.BLUE );
                        setStrokePaint( Color.RED );
                        setTransparency( 0.0f );
                        addInputEventListener( new CursorHandler( isHorizontal ? Cursor.E_RESIZE_CURSOR : Cursor.N_RESIZE_CURSOR ) {
                            @Override public void mouseClicked( PInputEvent event ) {
                                kit.breakBond( a, b );
                            }
                        } );
                    }} );

                    // listeners and setting position
                    a.addPositionListener( new SimpleObserver() {
                        public void update() {
                            updatePosition();
                        }
                    } );
                    b.addPositionListener( new SimpleObserver() {
                        public void update() {
                            updatePosition();
                        }
                    } );
                    updatePosition();
                }

                public void updatePosition() {
                    ImmutableVector2D location = b.getPosition().getSubtractedInstance( a.getPosition() ).getNormalizedInstance().getScaledInstance( a.getRadius() ).getAddedInstance( a.getPosition() );
                    setOffset( mvt.modelToView( location.toPoint2D() ) );
                }
            } );
        }
    }
}
