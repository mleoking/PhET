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

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.mri.controller.GradientMagnetControl;
import edu.colorado.phet.mri.model.GradientElectromagnet;
import edu.colorado.phet.mri.util.ControlBorderFactory;

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
        setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.getInstance().getString( "ControlPanel.GradientMagnets" ) ) );
//        setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),
//                                                     SimStrings.get( "ControlPanel.GradientMagnets" ) ) );
        GradientMagnetControl horizontalControl = new GradientMagnetControl( horizontalMagnet,
                                                                             SimStrings.getInstance().getString( "ControlPanel.Horizontal" ) );
        GradientMagnetControl verticalControl = new GradientMagnetControl( verticalMagnet,
                                                                           SimStrings.getInstance().getString( "ControlPanel.Vertical" ) );
        add( horizontalControl );
        add( verticalControl );
    }
}
