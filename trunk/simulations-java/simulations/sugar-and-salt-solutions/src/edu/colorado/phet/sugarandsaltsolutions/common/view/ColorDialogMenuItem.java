// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.dialogs.ColorChooserFactory;
import edu.colorado.phet.common.phetcommon.dialogs.ColorChooserFactory.Listener;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * This JMenuItem shows a Color dialog when selected so the user can change the color of the given color property.
 *
 * @author Sam Reid
 */
public class ColorDialogMenuItem extends JMenuItem {
    public ColorDialogMenuItem( Component parent, String title, final Property<Color> colorProperty ) {
        super( title );

        //Adapter to pass information from the ColorChooserFactory.Listener to the Property<Color>
        Listener listener = new Listener() {
            public void colorChanged( Color color ) {
                colorProperty.set( color );
            }

            public void ok( Color color ) {
                colorProperty.set( color );
            }

            public void cancelled( Color originalColor ) {
                colorProperty.set( originalColor );
            }
        };

        //Create the dialog
        final JDialog dialog = ColorChooserFactory.createDialog( title, parent, colorProperty.get(), listener );

        //Show the dialog if/when the user presses the button
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                SwingUtils.centerInParent( dialog );
                dialog.setVisible( true );
            }
        } );
    }
}
