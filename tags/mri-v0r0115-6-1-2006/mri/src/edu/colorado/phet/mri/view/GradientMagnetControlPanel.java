/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.view;

import edu.colorado.phet.mri.controller.GradientMagnetControl;
import edu.colorado.phet.mri.model.GradientElectromagnet;

import javax.swing.*;
import java.awt.*;

/**
 * GradientMagnetControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class GradientMagnetControlPanel extends JPanel {

    public GradientMagnetControlPanel( GradientElectromagnet horizontalMagnet, GradientElectromagnet verticalMagnet ) {
        super( new GridLayout( 2, 1 ) );
        setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),
                                                     "Current to Gradient Magnets" ) );
        GradientMagnetControl horizontalControl = new GradientMagnetControl( horizontalMagnet, "Horizontal Magnet" );
        GradientMagnetControl verticalControl = new GradientMagnetControl( verticalMagnet, "Vertical Magnet" );
        add( horizontalControl );
        add( verticalControl );
    }
}
