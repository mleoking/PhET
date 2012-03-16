// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.phscale.developer;

import java.awt.Frame;

import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.phscale.model.LiquidDescriptor;

/**
 * LiquidColorsDialog contains developer controls for experimenting with liquid colors.
 * This dialog will not be available to the user, and is not localized.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LiquidColorsDialog extends PaintImmediateDialog {

    public LiquidColorsDialog( Frame owner ) {
        super( owner );
        super.setTitle( "Liquid Colors" );
        super.setModal( false );
        super.setResizable( false );

        JPanel inputPanel = new LiquidColorsPanel( owner, LiquidDescriptor.getAllInstances() );

        getContentPane().add( inputPanel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }
}