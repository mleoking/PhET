// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;

import edu.colorado.phet.beerslawlab.common.BLLResources.Images;
import edu.colorado.phet.beerslawlab.concentration.model.ConcentrationModel;

/**
 * Editor for color schemes that map concentration to color.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ColorSchemeEditorDialog extends JDialog {

    public static class ColorSchemeEditorButton extends JButton {
        public ColorSchemeEditorButton( ConcentrationModel model ) {
            super( "dev", new ImageIcon( Images.COLOR_SCHEME_EDITOR_ICON ));
        }
    }
}
