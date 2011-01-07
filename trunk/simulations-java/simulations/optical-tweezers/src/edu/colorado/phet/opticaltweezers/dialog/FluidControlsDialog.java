// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.dialog;

import java.awt.Font;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.control.FluidControlPanel;
import edu.colorado.phet.opticaltweezers.model.Fluid;

/**
 * FluidControlsDialog is a nonmodal dialog that display the fluid controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FluidControlsDialog extends PaintImmediateDialog {

    private FluidControlPanel _fluidControlPanel;
    
    /**
     * Constructor.
     * 
     * @param owner parent of this dialog
     * @param font font to used for all controls
     * @param fluid model that we'll be controlling
     */
    public FluidControlsDialog( Frame owner, Font font, Fluid fluid ) {
        super( owner );
        assert( owner != null );
        setResizable( false );
        setModal( false );
        setTitle( OTResources.getString( "title.fluidControls" ) );
        
        _fluidControlPanel = new FluidControlPanel( fluid, font ); 
        
        getContentPane().add( _fluidControlPanel );
        pack();
    }
    
    /**
     * Performs cleanup before disposing of the dialog.
     */
    public void dispose() {
        _fluidControlPanel.cleanup(); // deletes observer relationship with model
        super.dispose();
    }
}
