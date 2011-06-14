// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.dialog;

import java.awt.Dialog;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.opticaltweezers.charts.PositionHistogramPanel;

/**
 * PositionHistogramSnapshotDialog is a nonmodal dialog that displays a snapshot of the position histogram.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PositionHistogramSnapshotDialog extends PaintImmediateDialog {
    
    /**
     * Constructor.
     * 
     * @param owner parent of this dialog
     * @param positionHistogramPanel
     */
    public PositionHistogramSnapshotDialog( Dialog owner, String title, PositionHistogramPanel positionHistogramPanel ) {
        super( owner );
        assert( owner != null );
        
        setResizable( false );
        setModal( false );
        setTitle( title );
        
        Image image = positionHistogramPanel.getSnapshotImage();
        Icon icon = new ImageIcon( image );
        JLabel label = new JLabel( icon );
        getContentPane().add( label );
        pack();
    }
}
