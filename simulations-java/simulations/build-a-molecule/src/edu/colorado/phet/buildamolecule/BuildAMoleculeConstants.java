// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 */
public class BuildAMoleculeConstants {

    /* Not intended for instantiation. */
    private BuildAMoleculeConstants() {
    }

    public static final String PROJECT_NAME = "build-a-molecule";

    /*---------------------------------------------------------------------------*
    * layout
    *----------------------------------------------------------------------------*/

    public static final PDimension STAGE_SIZE = new PDimension( 1008, 679 ); // the size of our "view" coordinate area.

    public static final ModelViewTransform MODEL_VIEW_TRANSFORM = ModelViewTransform.createSinglePointScaleInvertedYMapping(
            new Point2D.Double( 0, 0 ),
            new Point( (int) Math.round( BuildAMoleculeConstants.STAGE_SIZE.width * 0.5 ),
                       (int) Math.round( BuildAMoleculeConstants.STAGE_SIZE.height * 0.5 ) ),
            0.3 ); // "Zoom factor" - smaller zooms out, larger zooms in.

    // size of our model area. model centered around 0 (and there are a few dependencies on this)
    public static final PDimension MODEL_SIZE = new PDimension( MODEL_VIEW_TRANSFORM.viewToModelDeltaX( STAGE_SIZE.getWidth() ), // don't simplify. working around MVT bug
                                                                Math.abs( MODEL_VIEW_TRANSFORM.viewToModelDeltaY( STAGE_SIZE.getHeight() ) ) );

    // padding between simulation edges and kit/collection areas
    public static final double VIEW_PADDING = 18;
    public static final double MODEL_PADDING = BuildAMoleculeConstants.MODEL_VIEW_TRANSFORM.viewToModelDeltaX( VIEW_PADDING );

    /*---------------------------------------------------------------------------*
    * colors
    *----------------------------------------------------------------------------*/

    public static final Color CANVAS_BACKGROUND_COLOR = new Color( 198, 226, 246 ); // main play area background
    public static final Color MOLECULE_COLLECTION_BACKGROUND = new Color( 238, 238, 238 ); // collection area background
    public static final Color MOLECULE_COLLECTION_BORDER = Color.BLACK; // border around collection area
    public static final Property<Color> MOLECULE_COLLECTION_BOX_HIGHLIGHT = new Property<Color>( Color.YELLOW ); // box highlight (border when full)
    public static final Color MOLECULE_COLLECTION_BOX_BACKGROUND = Color.BLACK; // box background

    public static final Color MOLECULE_COLLECTION_BOX_BACKGROUND_BLINK = MOLECULE_COLLECTION_BOX_BACKGROUND; // box background when blinking
    public static final Color MOLECULE_COLLECTION_BOX_BORDER_BLINK = Color.BLUE; // box border when blinking

    public static final Color KIT_BACKGROUND = Color.WHITE; // kit area background
    public static final Color KIT_BORDER = Color.BLACK; // border around the kit area

    public static final Color KIT_ARROW_BACKGROUND_ENABLED = Color.YELLOW; // kit next/prev arrow background
    public static final Color KIT_ARROW_BORDER_ENABLED = Color.BLACK; // kit next/prev arrow border

    public static final Color COMPLETE_BACKGROUND_COLOR = MOLECULE_COLLECTION_BACKGROUND;

    /*---------------------------------------------------------------------------*
    * images
    *----------------------------------------------------------------------------*/

    public static final String IMAGE_EYE_ICON = "eye.png";
    public static final String IMAGE_SPLIT_ICON = "split-blue.png";
    public static final String IMAGE_3D_ICON = "3d.png";
    public static final String IMAGE_SCISSORS_ICON = "scissors.png";
    public static final String IMAGE_SCISSORS_CLOSED_ICON = "scissors-closed.png";

    /*---------------------------------------------------------------------------*
    * misc
    *----------------------------------------------------------------------------*/

    public static final Element[] SUPPORTED_ELEMENTS = new Element[] {
            Element.B, Element.Br, Element.C, Element.Cl, Element.F, Element.H, Element.I, Element.N, Element.O, Element.P, Element.S, Element.Si
    };
}
