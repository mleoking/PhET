package edu.colorado.phet.buildamolecule.control;

import java.text.MessageFormat;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.CollectionBox;
import edu.colorado.phet.buildamolecule.view.BuildAMoleculeCanvas;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;

/**
 * Displays a collection box that can collect multiple molecules with two text labels above. One shows the "goal", and the other shows the current
 * quantity present in the box.
 */
public class MultipleCollectionBoxNode extends CollectionBoxNode {
    public MultipleCollectionBoxNode( final BuildAMoleculeCanvas canvas, final CollectionBox box ) {
        super( canvas, box,
               new HTMLNode( MessageFormat.format( BuildAMoleculeStrings.COLLECTION_MULTIPLE_GOAL_FORMAT, box.getCapacity(), box.getMoleculeType().getMoleculeStructure().getGeneralFormulaFragment() ) ) {{
                   setFont( new PhetFont( 16, true ) );
               }},
               new HTMLNode() {{
                   // TODO: figure out a possible better way to assure that we don't see a "bumping" size change due to increase of string height with subscripts:
                   final String subscriptFix = "<sub> </sub>";

                   setFont( new PhetFont( 16 ) );

                   // update when the quantity changes
                   box.quantity.addObserver( new SimpleObserver() {
                       public void update() {
                           if ( box.quantity.get() == 0 ) {
                               setHTML( subscriptFix + BuildAMoleculeStrings.COLLECTION_MULTIPLE_QUANTITY_EMPTY + subscriptFix );
                           }
                           else {
                               setHTML( MessageFormat.format( subscriptFix + BuildAMoleculeStrings.COLLECTION_MULTIPLE_QUANTITY_FORMAT + subscriptFix, box.quantity.get(), box.getMoleculeType().getMoleculeStructure().getGeneralFormulaFragment() ) );
                           }
                       }
                   } );
               }} );
    }
}