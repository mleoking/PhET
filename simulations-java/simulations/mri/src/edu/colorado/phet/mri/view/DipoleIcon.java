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

import javax.swing.ImageIcon;

import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.MriResources;

/**
 * DipoleIcon
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DipoleIcon extends ImageIcon {

    public DipoleIcon() {
        super( MriResources.getImage( MriConfig.DIPOLE_IMAGE ) );
    }
}
