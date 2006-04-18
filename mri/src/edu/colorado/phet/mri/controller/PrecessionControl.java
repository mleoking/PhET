/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.controller;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.model.Dipole;
import edu.colorado.phet.mri.model.MriModel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.List;

/**
 * PrecessionControl
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PrecessionControl extends ModelSlider {
    public PrecessionControl( final MriModel model ) {
        super( "Max Precession", "rad", 0, Math.PI, MriConfig.InitialConditions.DIPOLE_PRECESSION );

        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                List dipoles = model.getDipoles();
                for( int i = 0; i < dipoles.size(); i++ ) {
                    Dipole dipole = (Dipole)dipoles.get( i );
                    dipole.setPrecession( getValue() );
                }
            }
        } );
    }
}
