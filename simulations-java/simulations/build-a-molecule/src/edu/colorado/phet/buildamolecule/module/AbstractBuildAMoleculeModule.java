package edu.colorado.phet.buildamolecule.module;

import java.awt.*;

import edu.colorado.phet.buildamolecule.model.*;
import edu.colorado.phet.buildamolecule.model.buckets.Bucket;
import edu.colorado.phet.buildamolecule.view.BuildAMoleculeCanvas;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.buildamolecule.model.buckets.AtomModel.*;

/**
 * Superclass for modules in Build a Molecule. Handles code required for all modules (bounds, canvas handling, and the ability to switch models)
 */
public abstract class AbstractBuildAMoleculeModule extends PiccoloModule {
    protected final LayoutBounds bounds;
    protected BuildAMoleculeCanvas canvas;
    protected Frame parentFrame;

    public AbstractBuildAMoleculeModule( Frame parentFrame, String name, boolean wide ) {
        super( name, new ConstantDtClock( 30 ) );
        this.parentFrame = parentFrame;
        setClockControlPanel( null );
        bounds = new LayoutBounds( wide );
    }

    protected abstract BuildAMoleculeCanvas buildCanvas( KitCollectionModel model );

    protected void setModel( KitCollectionModel model ) {
        canvas = buildCanvas( model );
        setSimulationPanel( canvas );
    }

    protected KitCollectionModel generateModel() {
        // TODO
        return new KitCollectionModel( bounds ) {{
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 600, 200 ), getClock(), HYDROGEN_FACTORY, 3 ),
                             new Bucket( new PDimension( 600, 200 ), getClock(), NITROGEN_FACTORY, 3 )
            ) );
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 400, 200 ), getClock(), HYDROGEN_FACTORY, 4 ),
                             new Bucket( new PDimension( 450, 200 ), getClock(), OXYGEN_FACTORY, 2 )
            ) );
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 350, 200 ), getClock(), CARBON_FACTORY, 1 ),
                             new Bucket( new PDimension( 450, 200 ), getClock(), OXYGEN_FACTORY, 2 )
            ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.NH3, 1 ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.CO2, 1 ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.H2O, 1 ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.H2, 1 ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.N2, 1 ) );
        }};
    }
}
