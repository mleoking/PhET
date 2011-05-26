package edu.colorado.phet.buildamolecule.view;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.BuildAMoleculeResources;
import edu.colorado.phet.buildamolecule.model.Atom2D;
import edu.colorado.phet.buildamolecule.model.CompleteMolecule;
import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.Molecule;
import edu.colorado.phet.buildamolecule.view.view3d.JmolDialogProperty;
import edu.colorado.phet.buildamolecule.view.view3d.ShowMolecule3DButtonNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Displays the molecule name and 'X' to break apart the molecule
 */
public class MoleculeMetadataNode extends PNode {
    private Kit kit;
    private Molecule molecule;
    private ModelViewTransform mvt;
    private final JmolDialogProperty dialog = new JmolDialogProperty();

    private Map<Atom2D, SimpleObserver> observerMap = new HashMap<Atom2D, SimpleObserver>();

    private static final double PADDING_BETWEEN_NODE_AND_ATOM = 5;

    public MoleculeMetadataNode( final Kit kit, final Molecule molecule, ModelViewTransform mvt ) {
        // SwingLayoutNode was doing some funky stuff (and wasn't centering), so we're rolling back to manual positioning
        this.kit = kit;
        this.molecule = molecule;
        this.mvt = mvt;

        if ( molecule.getAtoms().size() < 2 ) {
            // we don't need anything at all if it is not a "molecule"
            return;
        }

        final CompleteMolecule completeMolecule = molecule.getMatchingCompleteMolecule();

        final Property<Double> currentX = new Property<Double>( 0.0 );

        if ( completeMolecule != null ) {
            /*---------------------------------------------------------------------------*
            * label with chemical formula and common name
            *----------------------------------------------------------------------------*/
            addChild( new HTMLNode( completeMolecule.getDisplayName() ) {{
                setFont( new PhetFont( 14, true ) );
                currentX.set( currentX.get() + getFullBounds().getWidth() + 10 );
            }} );

            /*---------------------------------------------------------------------------*
            * show 3d button
            *----------------------------------------------------------------------------*/
            addChild( new ShowMolecule3DButtonNode( dialog, completeMolecule ) {{
                setOffset( currentX.get(), 0 );
                currentX.set( currentX.get() + getFullBounds().getWidth() + 5 );
            }} );
        }

        /*---------------------------------------------------------------------------*
        * break-up button
        *----------------------------------------------------------------------------*/
        addChild( new PNode() {{
            addChild( new PImage( BuildAMoleculeResources.getImage( BuildAMoleculeConstants.IMAGE_SPLIT_ICON ) ) );
            addInputEventListener( new CursorHandler() {
                @Override
                public void mouseClicked( PInputEvent event ) {
                    kit.breakMolecule( molecule );
                }
            } );
            setOffset( currentX.get(), 0 );
        }} );

        for ( final Atom2D atom : molecule.getAtoms() ) {
            atom.addPositionListener( new SimpleObserver() {
                {
                    observerMap.put( atom, this );
                }

                public void update() {
                    updatePosition();
                }
            } );
        }

        updatePosition(); // sanity check. should update (unfortunately) a number of times above

        // hide 3D dialogs when the kit is hidden
        kit.visible.addObserver( new SimpleObserver() {
            public void update() {
                if ( !kit.visible.get() ) {
                    dialog.hideDialogIfShown();
                }
            }
        } );
    }

    public void destruct() {
        for ( Atom2D atomModel : observerMap.keySet() ) {
            atomModel.removePositionListener( observerMap.get( atomModel ) );
        }
        dialog.hideDialogIfShown();
    }

    public void updatePosition() {
        PBounds modelPositionBounds = molecule.getPositionBounds();
        Rectangle2D moleculeViewBounds = mvt.modelToView( modelPositionBounds ).getBounds2D();

        setOffset( moleculeViewBounds.getCenterX() - getFullBounds().getWidth() / 2, // horizontally center
                   moleculeViewBounds.getY() - getFullBounds().getHeight() - PADDING_BETWEEN_NODE_AND_ATOM ); // offset from top of molecule
    }
}
