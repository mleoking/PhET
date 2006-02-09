/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.help;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.help.HelpItem;
import edu.colorado.phet.common.view.help.HelpManager;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * SingleAtomModuleHelp
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ApparatusPanelHelp extends HelpManager {
    private Color helpColor = new Color( 20, 140, 40 );
    private BaseLaserModule module;
    private HelpItem reflectivityHI;

    public ApparatusPanelHelp( BaseLaserModule module ) {
        Component component = module.getApparatusPanel();
        this.module = module;

        Point2D reflectivityHILoc = new Point2D.Double( 300, 300 );
        reflectivityHI = new HelpItem( component,
                                       SimStrings.get( "Help.reflectivitySlider" ),
                                       reflectivityHILoc );
        reflectivityHI.setForegroundColor( helpColor );

        addHelpItem( reflectivityHI );
    }

    public void setHelpEnabled( ApparatusPanel apparatusPanel, boolean h ) {
        reflectivityHI.setVisible( module.isMirrorsEnabled() );
        super.setHelpEnabled( apparatusPanel, h );
    }
}
