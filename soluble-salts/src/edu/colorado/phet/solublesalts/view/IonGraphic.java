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

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.piccolo.PImageFactory;
import edu.colorado.phet.solublesalts.model.Ion;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;

/**
 * IonGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class IonGraphic extends PNode implements SimpleObserver {

    private Ion ion;
    private PImage pImage;

    public IonGraphic( Ion ion, String imageName ) {
        this.ion = ion;
        ion.addObserver( this );
        pImage = PImageFactory.create( imageName, new Dimension( (int)ion.getRadius() * 2,
                                                                 (int)ion.getRadius() * 10 ) );
        this.addChild( pImage );
        update();
    }

    public void update() {
        this.setOffset( ion.getPosition().getX() - pImage.getWidth() / 2,
                        ion.getPosition().getY() - pImage.getHeight() / 2 );
    }
}
