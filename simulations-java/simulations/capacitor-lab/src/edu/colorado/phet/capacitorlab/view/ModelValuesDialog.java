/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.Frame;

import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;

/**
 * Dialog that displays "raw" model values.
 * This is intended for use by developers and is not internationalized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ModelValuesDialog extends PaintImmediateDialog {
    
    private final ModelValuesPanel panel;
    
    public ModelValuesDialog( Frame owner, CLModel model ) {
        super( owner );
        setTitle( "Model Values" );
        setResizable( false );
        panel = new ModelValuesPanel( model );
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
