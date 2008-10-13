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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetgraphics.view.help.HelpItem;
import edu.colorado.phet.common.phetgraphics.view.help.HelpManager;
import edu.colorado.phet.lasers.LasersResources;

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

        Point2D energyLevelHILoc = new Point2D.Double( 50, 100 );
        HelpItem energyLevelHI = new HelpItem( component,
                                               LasersResources.getString( "Help.energyLevel" ),
                                               energyLevelHILoc.getX(),
                                               energyLevelHILoc.getY() - 20,
                                               HelpItem.RIGHT, HelpItem.ABOVE );
        energyLevelHI.setForegroundColor( helpColor );
        energyLevelHI.setDisplayDropShadow( false );
        energyLevelHI.setAntiAlias( true );
        energyLevelHI.setFont( font );

        HelpItem lifetimeSliderHI = new HelpItem( component,
                                                  LasersResources.getString( "Help.lifetimeSlider" ),
                                                  energyLevelHILoc,
                                                  HelpItem.RIGHT, HelpItem.BELOW );
        lifetimeSliderHI.setForegroundColor( helpColor );
        lifetimeSliderHI.setDisplayDropShadow( false );
        lifetimeSliderHI.setAntiAlias( true );
        lifetimeSliderHI.setFont( font );

        helpManager.addHelpItem( lifetimeSliderHI );
        helpManager.addHelpItem( energyLevelHI );
    }
}
