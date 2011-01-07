// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.developer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;

import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.CapacitorLabApplication;
import edu.colorado.phet.capacitorlab.module.CLModule;
import edu.colorado.phet.common.phetcommon.application.Module;

/**
 * Menu item for enabling/disabling the rendering of Shapes related to E-Field measurement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EFieldShapesDebugMenuItem extends JCheckBoxMenuItem {

    public EFieldShapesDebugMenuItem( final CapacitorLabApplication app ) {
        super( "Show E-Field measurement shapes" );
        setForeground( CLPaints.EFIELD_SHAPES );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                for ( Module module : app.getModules() ) {
                    if ( module instanceof CLModule ) {
                        ( (CLModule) module ).setEFieldShapesDebugEnabled( isSelected() );
                    }
                }
            }
        } );
    }
}
