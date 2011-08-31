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
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Displays a 3D view for molecules that are "real" versions of the currently visible VSEPR model
 */
public class RealMoleculePanelNode extends PNode {

    private PNode child = null;
    private final MoleculeModel molecule;
    private final double SIZE = MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH;
    private PhetPPath overlayTarget;

    private int kitIndex = 0;
    private Property<RealMolecule> selectedMolecule = new Property<RealMolecule>( null );
    private List<RealMolecule> molecules = new ArrayList<RealMolecule>();

    public RealMoleculePanelNode( MoleculeModel molecule, final RealMoleculeOverlayNode overlayNode ) {
        this.molecule = molecule;

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
                    selectedMolecule.set( molecules.get( kitIndex ) );
                }
            } );
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
                    selectedMolecule.set( molecules.get( kitIndex ) );
                }
            } );
            setOffset( SIZE - getFullBounds().getWidth(), 0 );
        }} );

        /*---------------------------------------------------------------------------*
        * molecular formula label
        *----------------------------------------------------------------------------*/
        addChild( new HTMLNode( "", MoleculeShapesConstants.CONTROL_PANEL_BORDER_COLOR, new PhetFont( 14, true ) ) {{
            selectedMolecule.addObserver( new SimpleObserver() {
                public void update() {
                    if ( selectedMolecule.get() != null ) {
                        setHTML( ChemUtils.toIonSuperscript( ChemUtils.toSubscript( selectedMolecule.get().getDisplayName() ) ) );
                        setOffset( ( SIZE - getFullBounds().getWidth() ) / 2, 0 );
                    }
                    else {
                        setHTML( "" );
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
            setOffset( 0, 30 );
        }};
        addChild( overlayTarget );

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
        if ( child != null ) {
            overlayTarget.removeChild( child );
            child = null;
        }

        // get the list of real molecules that correspond to our VSEPR model
        molecules = RealMolecule.getMatchingMolecules( molecule );
        kitIndex = 0;

        if ( molecules.isEmpty() ) {
            selectedMolecule.set( null );

            // TODO i18n
            child = new PText( "(none)" ) {{
                setFont( new PhetFont( 16 ) );
                setTextPaint( MoleculeShapesConstants.CONTROL_PANEL_BORDER_COLOR );
                setOffset( ( SIZE - getWidth() ) / 2, ( SIZE - getHeight() ) / 2 );
            }};

            overlayTarget.addChild( child );
        }
        else {
            selectedMolecule.set( molecules.get( 0 ) );
        }

        repaint();
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
