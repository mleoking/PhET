/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.nuclearphysics.model.ControlRod;

import java.awt.*;

/**
 * ControlRodGroupGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ControlRodGroupGraphic extends CompositePhetGraphic {

    public ControlRodGroupGraphic( Component component, ControlRod[] controlRods ) {
        super( component );
        for( int i = 0; i < controlRods.length; i++ ) {
            ControlRod controlRod = controlRods[i];
            ControlRodGraphic controlRodGraphic = new ControlRodGraphic( component, controlRod );
            this.addGraphic( controlRodGraphic );
        }
    }
}
