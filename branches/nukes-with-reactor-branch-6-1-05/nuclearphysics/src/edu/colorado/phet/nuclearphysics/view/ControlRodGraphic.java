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
public class ControlRodGraphic extends PhetShapeGraphic {
    private static Color defaultColor = Color.blue;
    private static Stroke stroke = new BasicStroke( 1 );
    private static Color strokeColor = Color.black;

    public ControlRodGraphic( Component component, ControlRod controlRod ) {
        super( component, null, defaultColor, stroke, strokeColor );

        Shape rodShape = controlRod.getShape();
        Rectangle shape = new Rectangle( rodShape.getBounds().getLocation(),
                                         new Dimension( (int)rodShape.getBounds().getWidth(),
                                                        (int)rodShape.getBounds().getHeight()) );
        setShape( shape );
        update();
    }

    private void update() {
        setBoundsDirty();
        repaint();
    }
}

