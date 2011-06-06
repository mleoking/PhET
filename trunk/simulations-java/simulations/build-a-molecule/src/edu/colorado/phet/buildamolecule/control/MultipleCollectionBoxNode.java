package edu.colorado.phet.buildamolecule.control;

import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.CollectionBox;
import edu.colorado.phet.buildamolecule.model.CompleteMolecule;
import edu.colorado.phet.buildamolecule.model.MoleculeList;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Displays a collection box that can collect multiple molecules with two text labels above. One shows the "goal", and the other shows the current
 * quantity present in the box.
 */
public class MultipleCollectionBoxNode extends CollectionBoxNode {
    private static double maxWidth;
    private static double maxHeight;

    static {
        maxWidth = 0;
        maxHeight = 0;

        // compute maximum width and height for all different molecules
        for ( CompleteMolecule molecule : MoleculeList.COLLECTION_BOX_MOLECULES ) {
            PBounds boxBounds = new MultipleCollectionBoxNode( new CollectionBox( molecule, 1 ), new Function1<PNode, Rectangle2D>() {
                public Rectangle2D apply( PNode pNode ) {
                    return null;
                }
            } ).getFullBounds();

            maxWidth = Math.max( maxWidth, boxBounds.getWidth() );
            maxHeight = Math.max( maxHeight, boxBounds.getHeight() );
        }
    }

    public static double getMaxWidth() {
        return maxWidth;
    }

    public static double getMaxHeight() {
        return maxHeight;
    }

    public MultipleCollectionBoxNode( final CollectionBox box, Function1<PNode, Rectangle2D> toModelBounds ) {
        super( box, 2, toModelBounds );

        addHeaderNode( new PNode() {{
            HTMLNode goalNode = new HTMLNode( MessageFormat.format( BuildAMoleculeStrings.COLLECTION_MULTIPLE_GOAL_FORMAT, box.getCapacity(), box.getMoleculeType().getGeneralFormulaFragment() ) ) {{
                setFont( new PhetFont( 15, true ) );
            }};
            addChild( goalNode );
        }} );
        addHeaderNode( new HTMLNode() {{
            /*
             * Adding this text in will make text the same height (generally) whether or not they contain subscripts.
             * Before, the general positioning would be different on molecules which do not involve subscripts (HCN, NO, etc.)
             */
            final String subscriptFix = "<sub> </sub>";

            setFont( new PhetFont( 14 ) );

            // update when the quantity changes
            box.quantity.addObserver( new SimpleObserver() {
                public void update() {
                    if ( box.quantity.get() == 0 ) {
                        setHTML( subscriptFix + BuildAMoleculeStrings.COLLECTION_MULTIPLE_QUANTITY_EMPTY + subscriptFix );
                    }
                    else {
                        setHTML( MessageFormat.format( subscriptFix + BuildAMoleculeStrings.COLLECTION_MULTIPLE_QUANTITY_FORMAT + subscriptFix, box.quantity.get(), box.getMoleculeType().getGeneralFormulaFragment() ) );
                    }
                }
            } );
        }}
        );
    }
}