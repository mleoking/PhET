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

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.colorado.phet.mri.model.Electromagnet;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.common.util.SimpleObserver;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.*;

/**
 * ElectromagnetGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ElectromagnetGraphic extends PNode implements Electromagnet.ChangeListener {
    private PNode graphic;
    private Electromagnet electromagnet;

    public ElectromagnetGraphic( Electromagnet electromagnet ) {

        this.electromagnet = electromagnet;
        electromagnet.addChangeListener( this );

        Rectangle2D bounds = electromagnet.getBounds();
        Ellipse2D shape = new Ellipse2D.Double( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() );
        graphic = new PPath( shape );
        addChild( graphic );
        update();
    }

    public void update() {
        int blueComponent = (int)( 200 - 200 * ( electromagnet.getCurrent() / MriConfig.MAX_FADING_COIL_CURRENT ) );
        graphic.setPaint( new Color( 200, 200, blueComponent ) );
    }

    public void stateChanged( Electromagnet.ChangeEvent event ) {
        update();
    }
}
