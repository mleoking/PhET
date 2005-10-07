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

import edu.colorado.phet.common.view.help.HelpItem;
import edu.colorado.phet.common.view.help.HelpManager;
import edu.colorado.phet.common.view.util.SimStrings;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * SingleAtomModuleHelp
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class EnergyLevelPanelHelp {
    private Color helpColor = new Color( 20, 140, 40 );
    private Font font = new Font( HelpItem.DEFAULT_FONT.getName(), HelpItem.DEFAULT_FONT.getStyle(), 14 );

    public EnergyLevelPanelHelp( HelpManager helpManager ) {
        Component component = helpManager.getComponent();

        Point2D energyLevelHILoc = new Point2D.Double( 30, 100 );
        HelpItem energyLevelHI = new HelpItem( component,
                                               SimStrings.get( "Help.energyLevel" ),
                                               energyLevelHILoc,
                                               HelpItem.RIGHT, HelpItem.BELOW );
        energyLevelHI.setForegroundColor( helpColor );
        energyLevelHI.setFont( font );

        Point2D lifetimeSliderHILoc = new Point2D.Double( 320, 20 );
        HelpItem lifetimeSliderHI = new HelpItem( component,
                                                  SimStrings.get( "Help.lifetimeSlider" ),
                                                  lifetimeSliderHILoc,
                                                  HelpItem.LEFT, HelpItem.BELOW );
        lifetimeSliderHI.setForegroundColor( helpColor );
        lifetimeSliderHI.setFont( font );

        helpManager.addHelpItem( lifetimeSliderHI );
        helpManager.addHelpItem( energyLevelHI );
    }
}
