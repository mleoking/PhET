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

import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.nuclearphysics.model.ControlRod;

import java.awt.*;

/**
 * ControlRodGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ControlRodGraphic extends PhetShapeGraphic implements ControlRod.ChangeListener {
    private static Color defaultColor = Color.blue;
    private static Stroke stroke = new BasicStroke( 1 );
    private static Color strokeColor = Color.black;
    private Rectangle shape = new Rectangle();
    private ControlRod controlRod;

    public ControlRodGraphic( Component component, ControlRod controlRod ) {
        super( component, null, defaultColor, stroke, strokeColor );
        this.controlRod = controlRod;
        controlRod.addChangeListener( this );
        setShape( shape );
        update();
    }

    public void update() {
        Shape rod = controlRod.getShape();
        shape.setRect( rod.getBounds().getLocation().getX(),
                       rod.getBounds().getLocation().getY(),
                       rod.getBounds().getWidth(),
                       rod.getBounds().getHeight() );
        setBoundsDirty();
        repaint();
    }

    public void translate( double dx, double dy ) {
        controlRod.translate( dx, dy );
    }

    //----------------------------------------------------------------
    // Implementation of ControlRod.ChangeListener
    //----------------------------------------------------------------
    public void changed( ControlRod.ChangeEvent event ) {
        update();
    }
}

