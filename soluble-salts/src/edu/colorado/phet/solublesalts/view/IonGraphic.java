/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.view;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.colorado.phet.solublesalts.model.Ion;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.piccolo.PImageFactory;

/**
 * IonGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class IonGraphic extends PNode implements SimpleObserver {

    private Ion ion;
    private PImage image;

    public IonGraphic( Ion ion, String imageName ) {
        this.ion = ion;
        ion.addObserver( this );
        image = PImageFactory.create( imageName );
        this.addChild( image );
        update();
    }

    public void update() {
        this.setOffset( ion.getPosition().getX(), ion.getPosition().getY() );
    }
}
