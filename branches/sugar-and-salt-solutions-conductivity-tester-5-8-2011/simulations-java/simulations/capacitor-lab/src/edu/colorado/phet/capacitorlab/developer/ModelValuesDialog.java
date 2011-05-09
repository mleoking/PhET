// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.developer;

import java.awt.Frame;

import edu.colorado.phet.capacitorlab.module.dielectric.DielectricModel;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;

/**
 * Dialog that displays "raw" model values.
 * This is intended for use by developers and is not internationalized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class ModelValuesDialog extends PaintImmediateDialog {

    private final DielectricModelValuesPanel panel;

    public ModelValuesDialog( Frame owner, DielectricModel model ) {
        super( owner );
        setTitle( "Model Values" );
        setResizable( false );
        panel = new DielectricModelValuesPanel( model );
        setContentPane( panel );
        pack();
    }

    /**
     * Performs cleanup before disposing of the dialog.
     */
    public void dispose() {
        panel.cleanup(); // deletes observer relationship with model
        super.dispose();
    }
}
