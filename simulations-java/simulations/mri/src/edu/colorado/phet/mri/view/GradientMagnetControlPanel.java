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

import java.awt.GridLayout;

import javax.swing.JPanel;

import edu.colorado.phet.mri.MriResources;
import edu.colorado.phet.mri.controller.GradientMagnetControl;
import edu.colorado.phet.mri.model.GradientElectromagnet;
import edu.colorado.phet.mri.util.ControlBorderFactory;

/**
 * GradientMagnetControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class GradientMagnetControlPanel extends JPanel {

    public GradientMagnetControlPanel( GradientElectromagnet horizontalMagnet, GradientElectromagnet verticalMagnet ) {
        super( new GridLayout( 2, 1 ) );
        setBorder( ControlBorderFactory.createPrimaryBorder( MriResources.getString( "ControlPanel.GradientMagnets" ) ) );
//        setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),
//                                                     SimStrings.get( "ControlPanel.GradientMagnets" ) ) );
        GradientMagnetControl horizontalControl = new GradientMagnetControl( horizontalMagnet,
                                                                             MriResources.getString( "ControlPanel.Horizontal" ) );
        GradientMagnetControl verticalControl = new GradientMagnetControl( verticalMagnet,
                                                                           MriResources.getString( "ControlPanel.Vertical" ) );
        add( horizontalControl );
        add( verticalControl );
    }
}
