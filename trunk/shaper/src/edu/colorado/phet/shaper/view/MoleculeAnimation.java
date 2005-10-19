/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.shaper.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;


/**
 * MoleculeAnimation
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MoleculeAnimation extends CompositePhetGraphic {

    public MoleculeAnimation( Component component ) {
        super( component );
        
        PhetShapeGraphic background = new PhetShapeGraphic( component );
        background.setShape( new Rectangle( 0, 0, 250, 250 ) );
        background.setColor( Color.LIGHT_GRAY );
        addGraphic( background );
    }
}
