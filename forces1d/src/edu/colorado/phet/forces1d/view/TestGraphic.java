/** Sam Reid*/
package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.forces1d.common.InteractivePhetGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * User: Sam Reid
 * Date: Nov 13, 2004
 * Time: 10:53:50 AM
 * Copyright (c) Nov 13, 2004 by Sam Reid
 */
public class TestGraphic extends InteractivePhetGraphic {
    private PhetShapeGraphic shape1;
    private PhetShapeGraphic shape2;

    public TestGraphic( ApparatusPanel panel ) {
        super( panel );
        shape1 = new PhetShapeGraphic( panel, new Rectangle( 50, 400, 60, 80 ), Color.green );
        shape2 = new PhetShapeGraphic( panel, new Ellipse2D.Double( 50, 400 - 10, 20, 20 ), Color.yellow );
        addGraphic( shape1 );
        addGraphic( shape2 );
        JPopupMenu pop = new JPopupMenu();
        pop.add( new JMenuItem( "Hello" ) );

        addPopupMenuBehavior( pop );
        addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                shape1.translate( dx, dy );
                shape2.translate( dx, dy );
            }
        } );

        addCursorHandBehavior();
    }
}
