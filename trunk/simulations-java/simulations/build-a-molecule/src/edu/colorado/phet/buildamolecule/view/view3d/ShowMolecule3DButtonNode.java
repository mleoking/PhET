//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.view.view3d;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.BuildAMoleculeResources;
import edu.colorado.phet.buildamolecule.model.CompleteMolecule;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * A '3d' button that shows a 3d molecule view when pressed. Only allows one instance of the dialog to be present, so it communicates via a
 * dialog-option property.
 */
public class ShowMolecule3DButtonNode extends PNode {
    public final Property<Option<JmolDialog>> dialog;

    public ShowMolecule3DButtonNode( final Frame parentFrame, final JmolDialogProperty dialog, final CompleteMolecule completeMolecule ) {
        this.dialog = dialog;

        PImage image = new PImage( BuildAMoleculeResources.getImage( BuildAMoleculeConstants.IMAGE_3D_ICON ) );
        addChild( image );
        addInputEventListener( new CursorHandler() {
            @Override
            public void mouseClicked( PInputEvent event ) {
                // if the 3D dialog is not shown, show it
                if ( dialog.get().isNone() ) {
                    // set our reference to it ("disables" this button)
                    dialog.set( new Option.Some<JmolDialog>( JmolDialog.displayMolecule3D( parentFrame, completeMolecule ) ) );

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
        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, image.getWidth(), image.getHeight() ) ) {{
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
