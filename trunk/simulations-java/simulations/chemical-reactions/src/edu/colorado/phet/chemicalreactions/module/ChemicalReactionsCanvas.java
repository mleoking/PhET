// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.module;

import java.awt.Dimension;
import java.util.ArrayList;

import edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants;
import edu.colorado.phet.chemicalreactions.control.KitPanel;
import edu.colorado.phet.chemicalreactions.model.Atom;
import edu.colorado.phet.chemicalreactions.model.ChemicalReactionsModel;
import edu.colorado.phet.chemicalreactions.model.Kit;
import edu.colorado.phet.chemicalreactions.model.KitCollection;
import edu.colorado.phet.chemicalreactions.model.LayoutBounds;
import edu.colorado.phet.chemicalreactions.model.Molecule;
import edu.colorado.phet.chemicalreactions.model.MoleculeBucket;
import edu.colorado.phet.chemicalreactions.view.KitView;
import edu.colorado.phet.chemicalreactions.view.MoleculeNode;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.chemicalreactions.model.MoleculeShape.*;
import static edu.colorado.phet.chemistry.model.Element.*;

public class ChemicalReactionsCanvas extends PhetPCanvas {

    private final PNode root = new PNode();

    private ChemicalReactionsModel model;

    public ChemicalReactionsCanvas( IClock clock ) {
        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new CenteredStage( this, ChemicalReactionsConstants.STAGE_SIZE ) );

        setBackground( ChemicalReactionsConstants.CANVAS_BACKGROUND_COLOR );

        addWorldChild( root );

        final LayoutBounds layoutBounds = new LayoutBounds();

        model = new ChemicalReactionsModel( clock, layoutBounds );

        root.addChild( new KitPanel( model.getKitCollection(), layoutBounds.getAvailableKitBounds() ) );

        for ( Kit kit : model.getKitCollection().getKits() ) {
            final KitView kitView = new KitView( kit, this );

            root.addChild( kitView.getBottomLayer() );
            root.addChild( kitView.getAtomLayer() );
            root.addChild( kitView.getTopLayer() );
            root.addChild( kitView.getMetadataLayer() );
        }

        /*---------------------------------------------------------------------------*
        * temporary example molecules
        *----------------------------------------------------------------------------*/

        final double scale = ChemicalReactionsConstants.MODEL_VIEW_TRANSFORM.modelToViewDeltaX( 1 );
        {
            final double xCenter = 100;
            // 2H2, O2
            root.addChild( new MoleculeNode( new Molecule() {{
                addAtom( new Atom( O, new Property<ImmutableVector2D>( new ImmutableVector2D( 0, O.getRadius() ) ) ) );
                addAtom( new Atom( O, new Property<ImmutableVector2D>( new ImmutableVector2D( 0, -O.getRadius() ) ) ) );

                addAtom( new Atom( H, new Property<ImmutableVector2D>( new ImmutableVector2D( -O.getRadius() * 1.9, H.getRadius() ) ) ) );
                addAtom( new Atom( H, new Property<ImmutableVector2D>( new ImmutableVector2D( -O.getRadius() * 1.9, -H.getRadius() ) ) ) );

                addAtom( new Atom( H, new Property<ImmutableVector2D>( new ImmutableVector2D( O.getRadius() * 1.9, H.getRadius() ) ) ) );
                addAtom( new Atom( H, new Property<ImmutableVector2D>( new ImmutableVector2D( O.getRadius() * 1.9, -H.getRadius() ) ) ) );
            }} ) {{
                setOffset( xCenter, 250 );
                scale( scale );
            }} );

            root.addChild( new MoleculeNode( new Molecule() {{
                addAtom( new Atom( O, new Property<ImmutableVector2D>( new ImmutableVector2D( 0, O.getRadius() ) ) ) );
                addAtom( new Atom( O, new Property<ImmutableVector2D>( new ImmutableVector2D( 0, -O.getRadius() ) ) ) );

                addAtom( new Atom( H, new Property<ImmutableVector2D>( new ImmutableVector2D( -O.getRadius() * 1.5, H.getRadius() ) ) ) );
                addAtom( new Atom( H, new Property<ImmutableVector2D>( new ImmutableVector2D( -O.getRadius() * 1.5, -H.getRadius() ) ) ) );

                addAtom( new Atom( H, new Property<ImmutableVector2D>( new ImmutableVector2D( O.getRadius() * 1.5, H.getRadius() ) ) ) );
                addAtom( new Atom( H, new Property<ImmutableVector2D>( new ImmutableVector2D( O.getRadius() * 1.5, -H.getRadius() ) ) ) );
            }} ) {{
                setOffset( xCenter, 375 );
                scale( scale );
            }} );

            // H2Os
            root.addChild( new MoleculeNode( new Molecule( H2O ) ) {{
                setOffset( xCenter, 500 + O.getRadius() * scale );
                scale( scale );
            }} );
            root.addChild( new MoleculeNode( new Molecule( H2O ) ) {{
                setOffset( xCenter, 500 - O.getRadius() * scale );
                scale( scale );
            }} );

            root.addChild( new MoleculeNode( new Molecule( H2O ) ) {{
                setOffset( xCenter, 615 + O.getRadius() * scale * 1.2 );
                scale( scale );
            }} );
            root.addChild( new MoleculeNode( new Molecule( H2O ) ) {{
                setOffset( xCenter, 615 - O.getRadius() * scale * 1.2 );
                scale( scale );
            }} );
        }
        {
            final double xCenter = 300;
            // 2NH3
            root.addChild( new MoleculeNode( new Molecule() {{
                addAtom( new Atom( N, new Property<ImmutableVector2D>( new ImmutableVector2D( N.getRadius(), 0 ) ) ) );
                addAtom( new Atom( N, new Property<ImmutableVector2D>( new ImmutableVector2D( -N.getRadius(), 0 ) ) ) );

                final ImmutableVector2D a = new ImmutableVector2D( N.getRadius(), N.getRadius() + H.getRadius() );
                final ImmutableVector2D b = new ImmutableVector2D( N.getRadius() * 2 + H.getRadius(), 0 );
                final ImmutableVector2D c = a.plus( b.minus( a ).getNormalizedInstance().times( ( b.minus( a ).getMagnitude() - H.getRadius() * 2 ) / 2 ) ).plus( 18, 18 );
                final ImmutableVector2D d = b.plus( a.minus( b ).getNormalizedInstance().times( ( a.minus( b ).getMagnitude() - H.getRadius() * 2 ) / 2 ) ).plus( 18, 18 );
                addAtom( new Atom( H, new Property<ImmutableVector2D>( c ) ) );
                addAtom( new Atom( H, new Property<ImmutableVector2D>( d ) ) );

                addAtom( new Atom( H, new Property<ImmutableVector2D>( new ImmutableVector2D( -c.getX(), c.getY() ) ) ) );
                addAtom( new Atom( H, new Property<ImmutableVector2D>( new ImmutableVector2D( -d.getX(), d.getY() ) ) ) );

                addAtom( new Atom( H, new Property<ImmutableVector2D>( new ImmutableVector2D( -H.getRadius(), -N.getRadius() - H.getRadius() + 7 ) ) ) );
                addAtom( new Atom( H, new Property<ImmutableVector2D>( new ImmutableVector2D( H.getRadius(), -N.getRadius() - H.getRadius() + 7 ) ) ) );
            }} ) {{
                setOffset( xCenter, 400 );
                scale( scale );
            }} );

            // N2, 3H2
            root.addChild( new MoleculeNode( new Molecule() {{
                addAtom( new Atom( N, new Property<ImmutableVector2D>( new ImmutableVector2D( N.getRadius(), 0 ) ) ) );
                addAtom( new Atom( N, new Property<ImmutableVector2D>( new ImmutableVector2D( -N.getRadius(), 0 ) ) ) );

                addAtom( new Atom( H, new Property<ImmutableVector2D>( new ImmutableVector2D( N.getRadius(), N.getRadius() + H.getRadius() ) ) ) );
                addAtom( new Atom( H, new Property<ImmutableVector2D>( new ImmutableVector2D( N.getRadius(), -N.getRadius() - H.getRadius() ) ) ) );
                addAtom( new Atom( H, new Property<ImmutableVector2D>( new ImmutableVector2D( N.getRadius() * 2 + H.getRadius(), 0 ) ) ) );

                addAtom( new Atom( H, new Property<ImmutableVector2D>( new ImmutableVector2D( -N.getRadius(), N.getRadius() + H.getRadius() ) ) ) );
                addAtom( new Atom( H, new Property<ImmutableVector2D>( new ImmutableVector2D( -N.getRadius(), -N.getRadius() - H.getRadius() ) ) ) );
                addAtom( new Atom( H, new Property<ImmutableVector2D>( new ImmutableVector2D( -N.getRadius() * 2 - H.getRadius(), 0 ) ) ) );
            }} ) {{
                setOffset( xCenter, 600 );
                scale( scale );
            }} );
        }
    }

}
