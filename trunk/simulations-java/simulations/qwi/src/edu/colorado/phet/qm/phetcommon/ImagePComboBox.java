/*  */
package edu.colorado.phet.qm.phetcommon;

import edu.umd.cs.piccolox.pswing.PComboBox;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Feb 4, 2005
 * Time: 7:55:21 AM
 *
 */

public class ImagePComboBox extends PComboBox {

    public ImagePComboBox( final ImageComboBox.Item[] rampObjects ) {
        super( ImageComboBox.toLabelArray( rampObjects ) );
        setRenderer( new ImageComboBox.ComboBoxRenderer() );
        ImageIcon[] ii = ImageComboBox.toLabelArray( rampObjects );
        int maxWidth = 0;
        for( int i = 0; i < ii.length; i++ ) {
            ImageIcon icon = ii[i];
            JLabel label = new JLabel( icon.getDescription(), icon, JLabel.LEFT );
            if( label.getPreferredSize().width > maxWidth ) {
                maxWidth = label.getPreferredSize().width;
            }
        }
    }

}
