// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.util.Fireable;
import edu.colorado.phet.moleculeshapes.util.SimpleTarget;
import edu.colorado.phet.moleculeshapes.view.MoleculeJMEApplication;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

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

    public GeometryNameNode( MoleculeModel molecule, MoleculeJMEApplication app ) {
        this.molecule = molecule;

        app.resetNotifier.addTarget( new SimpleTarget() {
            public void update() {
                showMolecularShapeName.reset();
                showElectronShapeName.reset();
            }
        } );

        /*---------------------------------------------------------------------------*
        * visibility checkboxes
        *----------------------------------------------------------------------------*/

        final PSwing molecularCheckbox = new PSwing( new MoleculeShapesPropertyCheckBox( "Molecule Geometry", showMolecularShapeName ) ) {{
            // center within it's "column"
            setOffset( ( MAX_SHAPE_WIDTH - getFullBounds().getWidth() ) / 2, 0 );
        }};

        PSwing electronCheckbox = new PSwing( new MoleculeShapesPropertyCheckBox( "Electron Geometry", showElectronShapeName ) ) {{
            // center within it's "column"
            setOffset( MAX_SHAPE_WIDTH + PADDING_BETWEEN_LABELS + ( MAX_GEOMETRY_WIDTH - getFullBounds().getWidth() ) / 2, 0 );
        }};


        // calculate where we should put our labels vertically
        readoutHeight = Math.max( molecularCheckbox.getFullBounds().getHeight(), electronCheckbox.getFullBounds().getHeight() ) + VERTICAL_PADDING;

        // create a spacer, so that our control panel will never shrink below this amount of room
        final PNode spacer = new PhetPPath( new Rectangle2D.Double(
                0, 0,
                MAX_GEOMETRY_WIDTH + MAX_SHAPE_WIDTH + PADDING_BETWEEN_LABELS,
                readoutHeight + MAX_TEXT_HEIGHT ), new Color( 0f, 0, 0, 0f ) );

        addChild( spacer );
        addChild( molecularCheckbox );
        addChild( electronCheckbox );

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

        molecule.onGroupChanged.addTarget( new Fireable<PairGroup>() {
            public void fire( PairGroup pairGroup ) {
                updateMolecularText();
                updateElectronText();
            }
        } );
    }

    public void updateMolecularText() {
        // remove old readout
        if ( molecularText != null ) {
            removeChild( molecularText );
        }

        String name = molecule.getConfiguration().name;
        molecularText = getTextLabel( ( name == null ? Strings.SHAPE__EMPTY : name ) ); // replace the unknown value
        molecularText.setOffset( ( MAX_SHAPE_WIDTH - molecularText.getFullBounds().getWidth() ) / 2, readoutHeight );
        molecularText.setVisible( showMolecularShapeName.get() );

        addChild( molecularText );
    }

    public void updateElectronText() {
        if ( electronText != null ) {
            removeChild( electronText );
        }
        double columnOffset = MAX_SHAPE_WIDTH + PADDING_BETWEEN_LABELS; // compensate for the extra needed room

        String name = molecule.getConfiguration().geometry.name;
        electronText = getTextLabel( ( name == null ? Strings.GEOMETRY__EMPTY : name ) ); // replace the unknown value
        electronText.setOffset( columnOffset + ( MAX_SHAPE_WIDTH - electronText.getFullBounds().getWidth() ) / 2, readoutHeight );
        electronText.setVisible( showElectronShapeName.get() );

        addChild( electronText );
    }

    private static PText getTextLabel( final String label ) {
        return new PText( label ) {{
            setFont( new PhetFont( 16 ) );
            setTextPaint( Color.WHITE );
        }};
    }

    private static double getMaximumTextWidth( List<String> strings ) {
        double maxWidth = 0;
        for ( String string : strings ) {
            maxWidth = Math.max( maxWidth, getTextLabel( string ).getFullBounds().getWidth() );
        }
        return maxWidth;
    }

    private static double getMaximumTextHeight( List<String> strings ) {
        double maxHeight = 0;
        for ( String string : strings ) {
            maxHeight = Math.max( maxHeight, getTextLabel( string ).getFullBounds().getHeight() );
        }
        return maxHeight;
    }
}
