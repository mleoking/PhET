// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.chemistry.utils.ChemUtils;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.BackButton;
import edu.colorado.phet.common.piccolophet.nodes.kit.ForwardButton;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel.AnyChangeAdapter;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.model.RealMolecule;
import edu.colorado.phet.moleculeshapes.view.MoleculeJMEApplication;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Displays a 3D view for molecules that are "real" versions of the currently visible VSEPR model
 */
public class RealMoleculePanelNode extends PNode {

    private final MoleculeModel molecule;
    private final MoleculeJMEApplication app;
    private final double SIZE = MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH;
    private final double CONTROL_OFFSET = 40;
    private final double ARROW_Y_OFFSET = 5;
    private PhetPPath overlayTarget;

    private int kitIndex = 0;
    private Property<RealMolecule> selectedMolecule = new Property<RealMolecule>( null );
    private List<RealMolecule> molecules = new ArrayList<RealMolecule>();

    public RealMoleculePanelNode( MoleculeModel molecule, final MoleculeJMEApplication app, final RealMoleculeOverlayNode overlayNode ) {
        this.molecule = molecule;
        this.app = app;

        // make sure we have something at the very top so the panel doesn't shrink in
        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, SIZE, 10 ), new Color( 0, 0, 0, 0 ) ) );

        /*---------------------------------------------------------------------------*
        * back button
        *----------------------------------------------------------------------------*/
        addChild( new BackButton() {{
            selectedMolecule.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( kitIndex > 0 && !molecules.isEmpty() );
                }
            } );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    kitIndex--;
                    synchronized ( app ) {
                        selectedMolecule.set( molecules.get( kitIndex ) );
                    }
                }
            } );
            setOffset( 0, ARROW_Y_OFFSET );
        }} );

        /*---------------------------------------------------------------------------*
        * forward button
        *----------------------------------------------------------------------------*/
        addChild( new ForwardButton() {{
            selectedMolecule.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( kitIndex < molecules.size() - 1 && !molecules.isEmpty() );
                }
            } );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    kitIndex++;
                    synchronized ( app ) {
                        selectedMolecule.set( molecules.get( kitIndex ) );
                    }
                }
            } );
            setOffset( SIZE - getFullBounds().getWidth(), ARROW_Y_OFFSET );
        }} );

        /*---------------------------------------------------------------------------*
        * molecular formula label
        *----------------------------------------------------------------------------*/
        addChild( new HTMLNode( "", MoleculeShapesConstants.CONTROL_PANEL_BORDER_COLOR, new PhetFont( 14, true ) ) {{
            selectedMolecule.addObserver( new SimpleObserver() {
                public void update() {
                    synchronized ( app ) {
                        if ( selectedMolecule.get() != null ) {
                            setHTML( ChemUtils.toIonSuperscript( ChemUtils.toSubscript( selectedMolecule.get().getDisplayName() ) ) );
                        }
                        else {
                            setHTML( "(none)" );
                        }

                        // center vertically and horizontally
                        setOffset( ( SIZE - getFullBounds().getWidth() ) / 2, ( CONTROL_OFFSET - getFullBounds().getHeight() ) / 2 );

                        // if it goes past 0, push it down
                        if ( getFullBounds().getMinY() < 0 ) {
                            setOffset( getOffset().getX(), getOffset().getY() - getFullBounds().getMinY() );
                        }

                        repaint();
                    }
                }
            } );
        }} );

        /*---------------------------------------------------------------------------*
        * overlay target
        *----------------------------------------------------------------------------*/
        overlayTarget = new PhetPPath( new Rectangle2D.Double( 0, 0, SIZE, SIZE ), new Color( 0f, 0f, 0f, 0f ) ) {{
            setStroke( new BasicStroke( 1 ) );
            setStrokePaint( new Color( 60, 60, 60 ) );

            // make room for the buttons and labels above
            setOffset( 0, CONTROL_OFFSET );

            // if the user presses the mouse here, start dragging the molecule
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( PInputEvent event ) {
                    app.startOverlayMoleculeDrag();
                }
            } );
        }};
        addChild( overlayTarget );

        /*---------------------------------------------------------------------------*
        * display type selection
        *----------------------------------------------------------------------------*/
//        addChild( new PSwing( new JPanel() {{
//            setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
//            setOpaque( false );
//            final ButtonGroup group = new ButtonGroup();
//            add( new JRadioButton( "Space Fill", true ) {{ // 50% size
//                group.add( this );
//                setOpaque( false );
//                setForeground( MoleculeShapesConstants.CONTROL_PANEL_BORDER_COLOR );
//                addActionListener( new ActionListener() {
//                    public void actionPerformed( ActionEvent e ) {
//
//                    }
//                } );
//            }} );
//            add( new JRadioButton( "Ball and Stick", false ) {{
//                group.add( this );
//                setOpaque( false );
//                setForeground( MoleculeShapesConstants.CONTROL_PANEL_BORDER_COLOR );
//                addActionListener( new ActionListener() {
//                    public void actionPerformed( ActionEvent e ) {
//
//                    }
//                } );
//            }} );
//        }} ) {{
//            setOffset( 0, SIZE + CONTROL_OFFSET );
//        }} );

        onModelChange();

        // when the VSEPR molecule changes, update our possible molecules
        molecule.addListener( new AnyChangeAdapter() {
            @Override public void onGroupChange( PairGroup group ) {
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

    public PBounds getOverlayBounds() {
        return overlayTarget.getGlobalFullBounds();
    }

    private void onModelChange() {
        synchronized ( app ) {
            // get the list of real molecules that correspond to our VSEPR model
            molecules = RealMolecule.getMatchingMolecules( molecule );
            kitIndex = 0;

            boolean showingMolecule = !molecules.isEmpty();

            if ( showingMolecule ) {
                selectedMolecule.set( molecules.get( 0 ) );
            }
            else {
                selectedMolecule.set( null );
            }

//            if ( getChildrenReference().contains( overlayTarget ) != showingMolecule ) {
//                if ( showingMolecule ) {
//                    addChild( overlayTarget );
//                }
//                else {
//                    removeChild( overlayTarget );
//                }
//            }

            repaint();
        }
    }

    private static <A, B> List<B> map( List<A> list, Function1<A, B> map ) {
        // TODO: move to somewhere more convenient
        List<B> result = new ArrayList<B>();
        for ( A element : list ) {
            result.add( map.apply( element ) );
        }
        return result;
    }

    private PText labelText( String str ) {
        return new PText( str ) {{
            setFont( new PhetFont( 14 ) );
            setTextPaint( MoleculeShapesConstants.CONTROL_PANEL_BORDER_COLOR );
        }};
    }
}
