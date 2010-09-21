package edu.colorado.phet.lasers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.lasers.controller.PhotoWindow;

/**
 * Created by: Sam
 * May 25, 2008 at 5:58:26 PM
 */
public class ShowActualButton extends JButton {
    private JDialog photoDlg;

    public ShowActualButton() {
        super( LasersResources.getString( "LaserPhotoButtonLabel" ) );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( photoDlg == null ) {
                    photoDlg = new PhotoWindow( PhetUtilities.getPhetFrame() );
                    SwingUtils.centerDialogInParent( photoDlg );
                }
                photoDlg.setVisible( true );
            }
        } );
    }
}
