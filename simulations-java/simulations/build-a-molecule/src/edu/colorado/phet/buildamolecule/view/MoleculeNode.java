package edu.colorado.phet.buildamolecule.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.buildamolecule.control.JmolDialog;
import edu.colorado.phet.buildamolecule.model.CompleteMolecule;
import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.MoleculeStructure;
import edu.colorado.phet.buildamolecule.model.buckets.AtomModel;
import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
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
public class MoleculeNode extends PNode {
    private Kit kit;
    private MoleculeStructure moleculeStructure;
    private ModelViewTransform mvt;

    private Map<AtomModel, SimpleObserver> observerMap = new HashMap<AtomModel, SimpleObserver>();

    private static final double PADDING_BETWEEN_NODE_AND_ATOM = 5;

    public MoleculeNode( final Frame parentFrame, final Kit kit, final MoleculeStructure moleculeStructure, ModelViewTransform mvt ) {
        // SwingLayoutNode was doing some funky stuff (and wasn't centering), so we're rolling back to manual positioning
        this.kit = kit;
        this.moleculeStructure = moleculeStructure;
        this.mvt = mvt;

        if ( moleculeStructure.getAtoms().size() < 2 ) {
            // we don't need anything at all if it is not a "molecule"
            return;
        }

        final CompleteMolecule completeMolecule = moleculeStructure.getMatchingCompleteMolecule();

        final Property<Double> currentX = new Property<Double>( 0.0 );

        if ( completeMolecule != null ) {
            addChild( new HTMLNode( completeMolecule.getMoleculeStructure().getGeneralFormulaFragment() + " (" + completeMolecule.getCommonName() + ")" ) {{ // TODO i18n
                setFont( new PhetFont( 14, true ) );
                currentX.setValue( currentX.getValue() + getFullBounds().getWidth() + 10 );
            }} );
            addChild( new PNode() {{
                addChild( new PImage( PhetCommonResources.getMaximizeButtonImage() ) );
                addInputEventListener( new CursorHandler() {
                    @Override
                    public void mouseClicked( PInputEvent event ) {
                        JmolDialog.displayMolecule3D( parentFrame, completeMolecule );
                    }
                } );
                setOffset( currentX.getValue(), 0 );
                currentX.setValue( currentX.getValue() + getFullBounds().getWidth() + 5 );
            }} );
        }

        addChild( new PNode() {{
            addChild( new PImage( PhetCommonResources.getImage( PhetCommonResources.IMAGE_CLOSE_BUTTON ) ) );
            addInputEventListener( new CursorHandler() {
                @Override
                public void mouseClicked( PInputEvent event ) {
                    kit.breakMolecule( moleculeStructure );
                }
            } );
            setOffset( currentX.getValue(), 0 );
        }} );

        for ( Atom atom : moleculeStructure.getAtoms() ) {
            final AtomModel atomModel = kit.getAtomModel( atom );
            atomModel.addPositionListener( new SimpleObserver() {
                {
                    observerMap.put( atomModel, this );
                }

                public void update() {
                    updatePosition();
                }
            } );
        }

        updatePosition(); // sanity check. should update (unfortunately) a number of times above
    }

    public void destruct() {
        for ( AtomModel atomModel : observerMap.keySet() ) {
            atomModel.removePositionListener( observerMap.get( atomModel ) );
        }
    }

    public void updatePosition() {
        PBounds modelPositionBounds = kit.getMoleculePositionBounds( moleculeStructure );
        Rectangle2D moleculeViewBounds = mvt.modelToView( modelPositionBounds ).getBounds2D();

        setOffset( moleculeViewBounds.getCenterX() - getFullBounds().getWidth() / 2, // horizontally center
                   moleculeViewBounds.getY() - getFullBounds().getHeight() - PADDING_BETWEEN_NODE_AND_ATOM ); // offset from top of molecule
    }
}
