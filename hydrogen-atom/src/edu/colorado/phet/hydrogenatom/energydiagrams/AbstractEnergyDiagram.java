/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.energydiagrams;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;


public abstract class AbstractEnergyDiagram extends PhetPNode {

    public AbstractEnergyDiagram() {
        super();
        
        PPath path = new PPath( new Rectangle2D.Double( 0, 0, 300, 380 ) );
        path.setStroke( new BasicStroke( 2f ) );
        path.setStrokePaint( Color.BLACK );
        
        addChild( path );
    }
}
