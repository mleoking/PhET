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

import java.awt.*;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.DischargeLampsResources;
import edu.colorado.phet.dischargelamps.model.DischargeLampModel;

/**
 * CircuitGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CircuitGraphic extends PhetImageGraphic implements DischargeLampModel.ChangeListener {
    private BufferedImage positiveVoltageImage;
    private BufferedImage negativeVoltageImage;
    private BufferedImage currImage;

    public CircuitGraphic( Component component, AffineTransformOp scalingOp ) {
        super( component );
        positiveVoltageImage = DischargeLampsResources.getImage( DischargeLampsConfig.POSITIVE_CIRCUIT_IMAGE_FILE_NAME );
        negativeVoltageImage = DischargeLampsResources.getImage( DischargeLampsConfig.NEGATIVE_CIRCUIT_IMAGE_FILE_NAME );
        positiveVoltageImage = scalingOp.filter( positiveVoltageImage, null );
        negativeVoltageImage = scalingOp.filter( negativeVoltageImage, null );

        currImage = positiveVoltageImage;
        setImage( currImage );

    }

    //----------------------------------------------------------------
    // Implementation of DischargeLampModel.ChangeListener
    //----------------------------------------------------------------

    public void energyLevelsChanged( DischargeLampModel.ChangeEvent event ) {
        // noop
    }

    public void voltageChanged( DischargeLampModel.ChangeEvent event ) {
        if ( event.getDischargeLampModel().getVoltage() > 0
             && currImage != positiveVoltageImage ) {
            currImage = positiveVoltageImage;
            setImage( currImage );
            setBoundsDirty();
            repaint();
        }
        if ( event.getDischargeLampModel().getVoltage() < 0
             && currImage != negativeVoltageImage ) {
            currImage = negativeVoltageImage;
            setImage( currImage );
            setBoundsDirty();
            repaint();
        }
    }

    public void currentChanged( DischargeLampModel.ChangeEvent event ) {
        // noop
    }
}
