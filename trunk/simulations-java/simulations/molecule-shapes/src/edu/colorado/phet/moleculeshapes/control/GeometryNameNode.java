// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.simsharingcore.SimSharingEvents.addPropertyListener;

/**
 * Shows the molecular and electron geometry names, and has checkboxes which allow toggling their visibility
 */
public class GeometryNameNode extends PNode {

    // string list needed to compute maximum label bounds
    private static final List<String> GEOMETRY_STRINGS = Arrays.asList(
            Strings.GEOMETRY__EMPTY,
            Strings.GEOMETRY__DIATOMIC,
            Strings.GEOMETRY__LINEAR,
            Strings.GEOMETRY__OCTAHEDRAL,
            Strings.GEOMETRY__TETRAHEDRAL,
            Strings.GEOMETRY__TRIGONAL_BIPYRAMIDAL,
            Strings.GEOMETRY__TRIGONAL_PLANAR
    );

    // string list needed to compute maximum label bounds
    private static final List<String> SHAPE_STRINGS = Arrays.asList(
            Strings.SHAPE__BENT,
            Strings.SHAPE__DIATOMIC,
            Strings.SHAPE__EMPTY,
            Strings.SHAPE__LINEAR,
            Strings.SHAPE__OCTAHEDRAL,
            Strings.SHAPE__SEESAW,
            Strings.SHAPE__SQUARE_PLANAR,
            Strings.SHAPE__SQUARE_PYRAMIDAL,
            Strings.SHAPE__T_SHAPED,
            Strings.SHAPE__TETRAHEDRAL,
            Strings.SHAPE__TRIGONAL_BIPYRAMIDAL,
            Strings.SHAPE__TRIGONAL_PLANAR,
            Strings.SHAPE__TRIGONAL_PYRAMIDAL
    );

    // bounds for the maximum size of the labels
    private static final double MAX_GEOMETRY_WIDTH = getMaximumTextWidth( GEOMETRY_STRINGS );
    private static final double MAX_SHAPE_WIDTH = getMaximumTextWidth( SHAPE_STRINGS );
    private static final double MAX_TEXT_HEIGHT = Math.max( getMaximumTextHeight( GEOMETRY_STRINGS ), getMaximumTextHeight( SHAPE_STRINGS ) );

    // horizontal padding between the molecular and electron labels
    private static final double PADDING_BETWEEN_LABELS = 15;

    // vertical padding between the checkboxes and the labels
    private static final double VERTICAL_PADDING = 0; // currently 0, since it looks nice on Windows

    public final Property<Boolean> showMolecularShapeName = new Property<Boolean>( false );
    public final Property<Boolean> showElectronShapeName = new Property<Boolean>( false );

    private PText molecularText;
    private PText electronText;

    // the vertical location where our readout labels should be placed
    private double readoutHeight;

    private final MoleculeModel molecule;
    private final boolean showElectronGeometry;

    public GeometryNameNode( MoleculeModel molecule, boolean showElectronGeometry ) {
        this.molecule = molecule;
        this.showElectronGeometry = showElectronGeometry;

        /*---------------------------------------------------------------------------*
        * visibility checkboxes
        *----------------------------------------------------------------------------*/

        final PSwing molecularCheckbox = new PropertyCheckBoxNode( Strings.CONTROL__MOLECULE_GEOMETRY, showMolecularShapeName, MoleculeShapesColor.MOLECULAR_GEOMETRY_NAME ) {{
            // center within it's "column"
            setOffset( ( MAX_SHAPE_WIDTH - getFullBounds().getWidth() ) / 2, 0 );
            addPropertyListener( showMolecularShapeName, "Show molecule geometry" );
        }};

        PSwing electronCheckbox = new PropertyCheckBoxNode( Strings.CONTROL__ELECTRON_GEOMETRY, showElectronShapeName, MoleculeShapesColor.ELECTRON_GEOMETRY_NAME ) {{
            // center within it's "column"
            setOffset( MAX_SHAPE_WIDTH + PADDING_BETWEEN_LABELS + ( MAX_GEOMETRY_WIDTH - getFullBounds().getWidth() ) / 2, 0 );
            addPropertyListener( showElectronShapeName, "Show electron geometry" );
        }};


        // calculate where we should put our labels vertically
        readoutHeight = Math.max( molecularCheckbox.getFullBounds().getHeight(), electronCheckbox.getFullBounds().getHeight() ) + VERTICAL_PADDING;

        // create a spacer, so that our control panel will never shrink below this amount of room
        final PNode spacer = new Spacer( 0, 0,
                                         MAX_GEOMETRY_WIDTH + ( showElectronGeometry ? ( MAX_SHAPE_WIDTH + PADDING_BETWEEN_LABELS ) : 0 ),
                                         readoutHeight + MAX_TEXT_HEIGHT );

        addChild( spacer );
        addChild( molecularCheckbox );
        if ( showElectronGeometry ) {
            addChild( electronCheckbox );
        }

        /*---------------------------------------------------------------------------*
        * visibility listeners
        *----------------------------------------------------------------------------*/

        showMolecularShapeName.addObserver( new SimpleObserver() {
            public void update() {
                updateMolecularText();
            }
        } );

        showElectronShapeName.addObserver( new SimpleObserver() {
            public void update() {
                updateElectronText();
            }
        } );

        /*---------------------------------------------------------------------------*
        * change listeners
        *----------------------------------------------------------------------------*/

        molecule.onGroupChanged.addListener( new VoidFunction1<PairGroup>() {
            public void apply( PairGroup pairGroup ) {
                updateMolecularText();
                updateElectronText();
            }
        } );
    }

    public void updateMolecularText() {
        final String name = molecule.getConfiguration().name;
        final boolean visible = showMolecularShapeName.get();

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                // remove old readout
                if ( molecularText != null ) {
                    removeChild( molecularText );
                }

                molecularText = getTextLabel( ( name == null ? Strings.SHAPE__EMPTY : name ),
                                              MoleculeShapesColor.MOLECULAR_GEOMETRY_NAME.getProperty() ); // replace the unknown value
                molecularText.setOffset( ( MAX_SHAPE_WIDTH - molecularText.getFullBounds().getWidth() ) / 2, readoutHeight );
                molecularText.setVisible( visible );

                addChild( molecularText );
            }
        } );
    }

    public void updateElectronText() {
        if ( !showElectronGeometry ) {
            return;
        }
        final String name = molecule.getConfiguration().geometry.name;
        final boolean visible = showElectronShapeName.get();

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                if ( electronText != null ) {
                    removeChild( electronText );
                }
                double columnOffset = MAX_SHAPE_WIDTH + PADDING_BETWEEN_LABELS; // compensate for the extra needed room

                electronText = getTextLabel( ( name == null ? Strings.GEOMETRY__EMPTY : name ),
                                             MoleculeShapesColor.ELECTRON_GEOMETRY_NAME.getProperty() ); // replace the unknown value
                electronText.setOffset( columnOffset + ( MAX_SHAPE_WIDTH - electronText.getFullBounds().getWidth() ) / 2, readoutHeight );
                electronText.setVisible( visible );

                addChild( electronText );
            }
        } );
    }

    private static PText getTextLabel( final String label, final Property<Color> color ) {
        return new PText( label ) {{
            setFont( MoleculeShapesConstants.GEOMETRY_NAME_FONT );

            color.addObserver( new SimpleObserver() {
                public void update() {
                    setTextPaint( color.get() );
                }
            } );
        }};
    }

    private static double getMaximumTextWidth( List<String> strings ) {
        double maxWidth = 0;
        for ( String string : strings ) {
            maxWidth = Math.max( maxWidth, getTextLabel( string, new Property<Color>( Color.WHITE ) ).getFullBounds().getWidth() );
        }
        return maxWidth;
    }

    private static double getMaximumTextHeight( List<String> strings ) {
        double maxHeight = 0;
        for ( String string : strings ) {
            maxHeight = Math.max( maxHeight, getTextLabel( string, new Property<Color>( Color.WHITE ) ).getFullBounds().getHeight() );
        }
        return maxHeight;
    }
}
