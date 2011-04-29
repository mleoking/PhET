package edu.colorado.phet.buildamolecule.view;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.BuildAMoleculeResources;
import edu.colorado.phet.buildamolecule.control.JmolDialog;
import edu.colorado.phet.buildamolecule.model.AtomModel;
import edu.colorado.phet.buildamolecule.model.CompleteMolecule;
import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.MoleculeStructure;
import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Displays the molecule name and 'X' to break apart the molecule
 */
public class MoleculeMetadataNode extends PNode {
    private Kit kit;
    private MoleculeStructure moleculeStructure;
    private ModelViewTransform mvt;
    private Property<Option<JmolDialog>> dialog = new Property<Option<JmolDialog>>( new Option.None<JmolDialog>() );

    private Map<AtomModel, SimpleObserver> observerMap = new HashMap<AtomModel, SimpleObserver>();

    private static final double PADDING_BETWEEN_NODE_AND_ATOM = 5;

    public MoleculeMetadataNode( final Frame parentFrame, final Kit kit, final MoleculeStructure moleculeStructure, ModelViewTransform mvt ) {
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
            /*---------------------------------------------------------------------------*
            * label with chemical formula and common name
            *----------------------------------------------------------------------------*/
            addChild( new HTMLNode( completeMolecule.getCommonName() ) {{
                setFont( new PhetFont( 14, true ) );
                currentX.setValue( currentX.getValue() + getFullBounds().getWidth() + 10 );
            }} );

            /*---------------------------------------------------------------------------*
            * show 3d button
            *----------------------------------------------------------------------------*/
            addChild( new PNode() {{
                PImage image = new PImage( BuildAMoleculeResources.getImage( BuildAMoleculeConstants.IMAGE_EYE_ICON ) );
                addChild( image );
                addInputEventListener( new CursorHandler() {
                    @Override
                    public void mouseClicked( PInputEvent event ) {
                        // if the 3D dialog is not shown, show it
                        if ( dialog.getValue().isNone() ) {
                            // set our reference to it ("disables" this button)
                            dialog.setValue( new Option.Some<JmolDialog>( JmolDialog.displayMolecule3D( parentFrame, completeMolecule ) ) );

                            // listen to when it closes so we can re-enable the button
                            dialog.getValue().get().addWindowListener( new WindowAdapter() {
                                @Override public void windowClosed( WindowEvent e ) {
                                    dialog.setValue( new Option.None<JmolDialog>() );
                                }
                            } );
                        }
                        else {
                            dialog.getValue().get().requestFocus();
                        }
                    }
                } );
                setOffset( currentX.getValue(), 0 );
                currentX.setValue( currentX.getValue() + getFullBounds().getWidth() + 5 );

                // change overall transparency based on dialog existence
                dialog.addObserver( new SimpleObserver() {
                    public void update() {
                        setTransparency( dialog.getValue().isSome() ? 0.5f : 1f );
                    }
                } );

                /*---------------------------------------------------------------------------*
                * gray "disabled" overlay
                *----------------------------------------------------------------------------*/
                addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, image.getWidth(), image.getHeight() ) ) {{
                    setPaint( new Color( 128, 128, 128, 64 ) );
                    setStroke( null );
                    dialog.addObserver( new SimpleObserver() {
                        public void update() {
                            setVisible( dialog.getValue().isSome() );
                        }
                    } );
                }} );
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

        // hide 3D dialogs when the kit is hidden
        kit.visible.addObserver( new SimpleObserver() {
            public void update() {
                if ( !kit.visible.getValue() ) {
                    hideDialogIfShown();
                }
            }
        } );
    }

    public void destruct() {
        for ( AtomModel atomModel : observerMap.keySet() ) {
            atomModel.removePositionListener( observerMap.get( atomModel ) );
        }
        hideDialogIfShown();
    }

    public void hideDialogIfShown() {
        if ( dialog.getValue().isSome() ) {
            dialog.getValue().get().dispose();
            dialog.setValue( new Option.None<JmolDialog>() );
        }
    }

    public void updatePosition() {
        PBounds modelPositionBounds = kit.getMoleculePositionBounds( moleculeStructure );
        Rectangle2D moleculeViewBounds = mvt.modelToView( modelPositionBounds ).getBounds2D();

        setOffset( moleculeViewBounds.getCenterX() - getFullBounds().getWidth() / 2, // horizontally center
                   moleculeViewBounds.getY() - getFullBounds().getHeight() - PADDING_BETWEEN_NODE_AND_ATOM ); // offset from top of molecule
    }
}
