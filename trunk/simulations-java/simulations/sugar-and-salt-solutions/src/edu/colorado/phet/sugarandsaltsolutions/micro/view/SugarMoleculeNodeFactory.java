package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ItemList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.SugarMolecule;

/**
 * This class observes an ItemList and creates a PNode when an item is added to the model list,
 * and removes it when the item is removed from the model list.
 *
 * @author Sam Reid
 */
public class SugarMoleculeNodeFactory implements VoidFunction1<SugarMolecule> {
    private final ItemList<SugarMolecule> list;
    private final ModelViewTransform transform;
    private final MicroCanvas canvas;

    public SugarMoleculeNodeFactory( ItemList<SugarMolecule> list, ModelViewTransform transform, MicroCanvas canvas ) {
        this.list = list;
        this.transform = transform;
        this.canvas = canvas;
    }

    //Create the PNode for the particle, and wire it up to be removed when the particle leaves the model
    public void apply( SugarMolecule particle ) {
        final SugarMoleculeNode node = new SugarMoleculeNode( transform, particle );
        canvas.addChild( node );
        list.addItemRemovedListener( new VoidFunction1<SugarMolecule>() {
            public void apply( SugarMolecule sphericalParticle ) {
                list.removeItemRemovedListener( this );
                canvas.removeChild( node );
            }
        } );
    }
}
