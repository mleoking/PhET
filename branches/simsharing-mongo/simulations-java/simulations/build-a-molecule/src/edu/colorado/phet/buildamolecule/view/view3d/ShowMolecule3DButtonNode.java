//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.view.view3d;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.BuildAMoleculeResources;
import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.CompleteMolecule;
import edu.colorado.phet.common.jmolphet.JmolDialog;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PAffineTransform;

/**
 * A '3d' button that shows a 3d molecule view when pressed. Only allows one instance of the dialog to be present, so it communicates via a
 * dialog-option property.
 */
public class ShowMolecule3DButtonNode extends PNode {
    public final Property<Option<JmolDialog>> dialog;

    public ShowMolecule3DButtonNode( final JmolDialogProperty dialog, final CompleteMolecule completeMolecule ) {
        this.dialog = dialog;

        // create our text for the '3D' button first. We will need its width later on
        PText text = new PText( BuildAMoleculeStrings.ICON_3D ) {{
            setFont( new PhetFont( 12, true ) );
            setTextPaint( Color.WHITE );
        }};

        // if it is too wide, override it
        if ( text.getWidth() > 35 ) {
            text.setText( "3D" );
        }

        // parts of the icon background
        PImage left = new PImage( BuildAMoleculeResources.getImage( BuildAMoleculeConstants.IMAGE_GREEN_LEFT ) );
        PImage middle = new PImage( BuildAMoleculeResources.getImage( BuildAMoleculeConstants.IMAGE_GREEN_MIDDLE ) );
        PImage right = new PImage( BuildAMoleculeResources.getImage( BuildAMoleculeConstants.IMAGE_GREEN_RIGHT ) );

        // natural (square 19x19) width
        double combinedWidth = left.getWidth() + middle.getWidth() + right.getWidth();

        // padding between text and the left/right sides
        double textPadding = 2;

        // get width with padding. minimum is combinedWidth, and take ceiling (so our middle tile fits nicely
        double textWidth = text.getFullBounds().getWidth() + 2 * textPadding; // padding
        if ( textWidth < combinedWidth ) {
            textWidth = combinedWidth;
        }
        textWidth = Math.ceil( textWidth );

        // center it in our bounds
        text.setOffset( ( textWidth - text.getFullBounds().getWidth() ) / 2, ( left.getFullBounds().getHeight() - text.getFullBounds().getHeight() ) / 2 );

        // position the parts of the icon background
        double xScale = textWidth - combinedWidth + 1;
        middle.setTransform( new PAffineTransform(
                xScale, 0, // widen the middle to take up all of the extra space
                0, 1, // same height
                left.getWidth(), 0 // offset to the right
        ) );
        right.setOffset( left.getWidth() + middle.getFullBounds().getWidth(), 0 );

        // background
        addChild( left );
        addChild( middle );
        addChild( right );

        // text over the icon background
        addChild( text );

        addInputEventListener( new CursorHandler() {
            @Override
            public void mouseClicked( PInputEvent event ) {
                // if the 3D dialog is not shown, show it
                if ( dialog.get().isNone() ) {
                    // set our reference to it ("disables" this button)
                    dialog.set( new Option.Some<JmolDialog>( JmolDialog.displayMolecule3D( PhetApplication.getInstance().getPhetFrame(), completeMolecule, BuildAMoleculeStrings.JMOL_3D_SPACE_FILLING, BuildAMoleculeStrings.JMOL_3D_BALL_AND_STICK, BuildAMoleculeStrings.JMOL_3D_LOADING ) ) );
                    System.out.println( "Showing 3D dialog for " + completeMolecule.getDisplayName() + " PubChem CID #" + completeMolecule.getCID() );

                    // listen to when it closes so we can re-enable the button
                    dialog.get().get().addWindowListener( new WindowAdapter() {
                        @Override public void windowClosed( WindowEvent e ) {
                            dialog.set( new Option.None<JmolDialog>() );
                        }
                    } );
                }
                else {
                    dialog.get().get().requestFocus();
                }
            }
        } );

        // change overall transparency based on dialog existence
        dialog.addObserver( new SimpleObserver() {
            public void update() {
                setTransparency( dialog.get().isSome() ? 0.5f : 1f );
            }
        } );

        /*---------------------------------------------------------------------------*
        * gray "disabled" overlay
        *----------------------------------------------------------------------------*/
        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, getFullBounds().getWidth(), getFullBounds().getHeight() ) ) {{
            setPaint( new Color( 128, 128, 128, 64 ) );
            setStroke( null );
            dialog.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( dialog.get().isSome() );
                }
            } );
        }} );
    }
}
