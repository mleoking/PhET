// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import edu.colorado.phet.chemistry.utils.ChemUtils;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.colorado.phet.jmephet.JMEActionListener;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.control.ArrowButtonNode.Orientation;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.model.RealMolecule;
import edu.colorado.phet.moleculeshapes.module.MoleculeShapesModule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * The panel and controls for the Real Molecule (real examples) 3D view. It does not include the actual
 * 3D model, which is included in the overlay
 */
public class RealMoleculePanelNode extends PNode {

    private final MoleculeModel molecule;

    private final MoleculeShapesModule module;
    // whether the view is minimized or not
    private final Property<Boolean> minimized;

    // size of our 3D view itself (square)
    private final double SIZE = MoleculeShapesConstants.RIGHT_MIN_WIDTH;

    // offset of the 3D view from the top of the panel interior
    private final double CONTROL_OFFSET = 40;

    // extra X-padding for forward/back arrows
    private final double ARROW_X_PADDING = 3;

    // Y offset of the forward/back arrows
    private final double ARROW_Y_OFFSET = 8;

    // our PNode where the viewport of the 3D overlay molecule should be shown
    private PhetPPath overlayTarget;

    // a container for all Piccolo nodes that disappear when "minimized", so we can remove it from the scene graph for the control panel sizing
    private PNode containerNode = new PNode();

    // which of the selected available real molecules is being viewed
    private int kitIndex = 0; // (index)
    private Property<RealMolecule> selectedMolecule = new Property<RealMolecule>( null ); // (the selected molecule itself)

    // list of all of the real examples for the current VSEPR model. we only display at most 1 of these
    private List<RealMolecule> molecules = new ArrayList<RealMolecule>();

    public RealMoleculePanelNode( MoleculeModel molecule, final MoleculeShapesModule module, final RealMoleculeOverlayNode overlayNode,
                                  final Property<Boolean> minimized ) {
        this.molecule = molecule;
        this.module = module;
        this.minimized = minimized;

        // when minimization changes, update our visual state
        minimized.addObserver( JMEUtils.swingObserver( new Runnable() {
            public void run() {
                // since event ordering can be weird between the threads, double-check before adding or removing the container node
                if ( minimized.get() && containerNode.getParent() != null ) {
                    removeChild( containerNode );
                }
                if ( !minimized.get() && containerNode.getParent() == null ) {
                    addChild( containerNode );
                }
            }
        } ), false );

        // initialize to the correct state
        if ( !minimized.get() ) {
            addChild( containerNode );
        }

        // make sure we have something at the very top so the panel doesn't shrink in
        addChild( new Spacer( 0, 0, MoleculeShapesControlPanel.INNER_WIDTH, 10 ) );

        final double horizontalPadding = ( MoleculeShapesControlPanel.INNER_WIDTH - SIZE ) / 2;

        /*---------------------------------------------------------------------------*
        * back button
        *----------------------------------------------------------------------------*/
        containerNode.addChild( new ArrowButtonNode( Orientation.LEFT ) {{
            final Function0<Boolean> visibilityCondition = new Function0<Boolean>() {
                public Boolean apply() {
                    return kitIndex > 0 && !molecules.isEmpty();
                }
            };

            // when the selected molecule changes, update our view in the EDT
            selectedMolecule.addObserver( JMEUtils.swingObserver( new Runnable() {
                public void run() {
                    setVisible( visibilityCondition.apply() );
                }
            } ), false );
            setVisible( visibilityCondition.apply() );

            addActionListener( new JMEActionListener( new Runnable() {
                public void run() {
                    // sanity check for visibility
                    if ( visibilityCondition.apply() ) {
                        kitIndex--;
                        selectedMolecule.set( molecules.get( kitIndex ) );
                    }
                }
            } ) );
            setOffset( horizontalPadding + ARROW_X_PADDING, ARROW_Y_OFFSET );
        }} );

        /*---------------------------------------------------------------------------*
        * forward button
        *----------------------------------------------------------------------------*/
        containerNode.addChild( new ArrowButtonNode( Orientation.RIGHT ) {{
            final Function0<Boolean> visibilityCondition = new Function0<Boolean>() {
                public Boolean apply() {
                    return kitIndex < molecules.size() - 1 && !molecules.isEmpty();
                }
            };

            // when the selected molecule changes, update our view in the EDT
            selectedMolecule.addObserver( JMEUtils.swingObserver( new Runnable() {
                public void run() {
                    setVisible( visibilityCondition.apply() );
                }
            } ), false );
            setVisible( visibilityCondition.apply() );

            addActionListener( new JMEActionListener( new Runnable() {
                public void run() {
                    // sanity check for visibility
                    if ( visibilityCondition.apply() ) {
                        kitIndex++;
                        selectedMolecule.set( molecules.get( kitIndex ) );
                    }
                }
            } ) );
            setOffset( MoleculeShapesControlPanel.INNER_WIDTH - getFullBounds().getWidth() - horizontalPadding - ARROW_X_PADDING, ARROW_Y_OFFSET );
        }} );

        /*---------------------------------------------------------------------------*
        * molecular formula label
        *----------------------------------------------------------------------------*/
        containerNode.addChild( new HTMLNode( "", MoleculeShapesColor.REAL_EXAMPLE_FORMULA.get(), MoleculeShapesConstants.EXAMPLE_MOLECULAR_FORMULA_FONT ) {
            {
                MoleculeShapesColor.REAL_EXAMPLE_FORMULA.addColorObserver( new VoidFunction1<Color>() {
                    public void apply( Color color ) {
                        setHTMLColor( color );
                    }
                } );
                selectedMolecule.addObserver( JMEUtils.swingObserver( new Runnable() {
                    public void run() {
                        updateView();
                    }
                } ), false );
                updateView();
            }

            public void updateView() {
                if ( selectedMolecule.get() != null ) {
                    setHTML( ChemUtils.toSubscript( selectedMolecule.get().getDisplayName() ) );
                }
                else {
                    setHTML( "(none)" );
                }

                // center vertically and horizontally
                setOffset( ( MoleculeShapesControlPanel.INNER_WIDTH - getFullBounds().getWidth() ) / 2, ( CONTROL_OFFSET - getFullBounds().getHeight() ) / 2 );

                // if it goes past 0, push it down
                if ( getFullBounds().getMinY() < 0 ) {
                    setOffset( getOffset().getX(), getOffset().getY() - getFullBounds().getMinY() );
                }
            }
        } );

        /*---------------------------------------------------------------------------*
        * overlay target
        *----------------------------------------------------------------------------*/
        final float overlayBorderWidth = 1;
        overlayTarget = new PhetPPath( new Rectangle2D.Double( 0, 0, SIZE - overlayBorderWidth, SIZE - overlayBorderWidth ), new Color( 0f, 0f, 0f, 0f ) ) {{
            setStroke( new BasicStroke( overlayBorderWidth ) );

            MoleculeShapesColor.REAL_EXAMPLE_BORDER.addColorObserver( new VoidFunction1<Color>() {
                public void apply( Color color ) {
                    setStrokePaint( color );
                }
            } );

            // make room for the buttons and labels above
            setOffset( ( MoleculeShapesControlPanel.INNER_WIDTH - SIZE ) / 2, CONTROL_OFFSET );

            // if the user presses the mouse here, start dragging the molecule
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( PInputEvent event ) {
                    JMEUtils.invoke( new Runnable() {
                        public void run() {
                            if ( selectedMolecule.get() != null ) {
                                module.startOverlayMoleculeDrag();
                            }
                        }
                    } );
                }
            } );
        }};
        containerNode.addChild( overlayTarget );

        onModelChange();

        // when the VSEPR molecule changes, update our possible molecules
        molecule.onGroupChanged.addListener( new VoidFunction1<PairGroup>() {
            public void apply( PairGroup pairGroup ) {
                onModelChange();
            }
        } );

        // update the overlay (3D molecule view) when our selected molecule changes
        selectedMolecule.addObserver( new SimpleObserver() {
            public void update() {
                overlayNode.showMolecule( selectedMolecule.get() );
            }
        } );
    }

    public boolean isOverlayVisible() {
        return !minimized.get();
    }

    public PBounds getRealMoleculeOverlayBounds() {
        return overlayTarget.getGlobalFullBounds();
    }

    private void onModelChange() {
        // get the list of real molecules that correspond to our VSEPR model
        molecules = RealMolecule.getMatchingMolecules( molecule );
        kitIndex = 0;

        final boolean showingMolecule = !molecules.isEmpty();

        if ( showingMolecule ) {
            selectedMolecule.set( molecules.get( 0 ) );
        }
        else {
            selectedMolecule.set( null );
        }

        // TODO: allow the collapse-on-no-model changes?
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                // TODO: are we going to hit synchronization issues here again? YES
                if ( getChildrenReference().contains( overlayTarget ) != showingMolecule ) {
                    if ( showingMolecule ) {
                        containerNode.addChild( overlayTarget );
                    }
                    else {
                        containerNode.removeChild( overlayTarget );
                    }
                }
            }
        } );
    }
}
