/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.view;

import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.model.HeatingElement;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * HeatElementGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HeatingElementGraphic extends PhetImageGraphic implements HeatingElement.ChangeListener {

    public HeatingElementGraphic( Component component, boolean isRightFacing ) {
        super( component );

        try {
            BufferedImage img = ImageLoader.loadBufferedImage( DischargeLampsConfig.HEATING_ELEMENT_FILE_NAME );
            if( isRightFacing ) {
                img = BufferedImageUtils.flipX( img );
            }
            setImage( img );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    //----------------------------------------------------------------
    // Implementation of listener interfaces
    //----------------------------------------------------------------

    public void temperatureChanged() {
        MakeDuotoneImageOp op = new MakeDuotoneImageOp( Color.red );
        BufferedImage img = getImage();
//        setImage( op.filter( img, img ));
    }
}
