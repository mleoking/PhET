/*  */
package edu.colorado.phet.qm.davissongermer;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 18, 2006
 * Time: 12:23:42 PM
 */

public class SaveDGPanel {
    private int saveCount = 1;

    public void savePanel( DGPlotPanel dgPlotPanel, Frame parentFrame, String text ) {
        dgPlotPanel.saveDataAsLayer( text );
//        Image copy = super.getLayer().toImage( image.getImage().getWidth( null ), image.getImage().getHeight( null ), Color.white );
//        origVersion( dgPlotPanel, parentFrame );
    }

}
