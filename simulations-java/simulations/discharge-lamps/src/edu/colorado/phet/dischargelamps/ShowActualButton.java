package edu.colorado.phet.dischargelamps;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.lasers.controller.PhotoWindow;

/**
 * Created by: Sam
 * May 25, 2008 at 5:55:42 PM
 */
public class ShowActualButton extends JButton {
    private static PhotoWindow photoWindow;

    public ShowActualButton() {
        super( DischargeLampsResources.getString( "Misc.ActualPixBtn.label" ) );
        addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                if ( photoWindow == null ) {
                    try {
                        BufferedImage bi = ImageLoader.loadBufferedImage( "discharge-lamps/images/actual-lamps.jpg" );
                        photoWindow = new PhotoWindow( PhetUtilities.getPhetFrame(), bi );
                        SwingUtils.centerDialogInParent( photoWindow );
                    }
                    catch( IOException e1 ) {
                        e1.printStackTrace();
                    }
                }
                photoWindow.setVisible( true );
            }
        } );
    }
}
