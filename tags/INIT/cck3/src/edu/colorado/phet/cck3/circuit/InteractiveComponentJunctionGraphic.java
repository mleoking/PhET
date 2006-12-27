/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.components.CircuitComponent;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 26, 2004
 * Time: 9:46:27 PM
 * Copyright (c) May 26, 2004 by Sam Reid
 */
public class InteractiveComponentJunctionGraphic extends DefaultInteractiveGraphic implements HasJunctionGraphic {
    private JunctionGraphic junctionGraphic;
    private CircuitGraphic cg;
    private Branch branch;

    public InteractiveComponentJunctionGraphic( final CircuitGraphic cg, final JunctionGraphic junctionGraphic, final CircuitComponent branch, CCK3Module module ) {
        super( junctionGraphic );
        this.cg = cg;
        this.branch = branch;
        addCursorHandBehavior();
        this.junctionGraphic = junctionGraphic;
        MouseInputListener mouse = new MouseInputAdapter() {

            public void mouseDragged( MouseEvent e ) {
                Point2D.Double pt = junctionGraphic.getTransform().viewToModel( e.getPoint() );
                Junction opposite = branch.opposite( junctionGraphic.getJunction() );
                AbstractVector2D vec = new ImmutableVector2D.Double( opposite.getPosition(), pt );
                vec = vec.getInstanceOfMagnitude( branch.getComponentLength() );
                Point2D dst = vec.getDestination( opposite.getPosition() );
                junctionGraphic.getJunction().setPosition( dst.getX(), dst.getY() );
                branch.notifyObservers();
            }

            public void mousePressed( MouseEvent e ) {
            }

            public void mouseReleased( MouseEvent e ) {
            }
        };
        addMouseInputListener( mouse );

        JPopupMenu menu = new JPopupMenu();
        menu.add( new JunctionSplitter( getJunction(), cg, module ).toJMenuItem() );
        addPopupMenuBehavior( menu );
    }

    private Junction getJunction() {
        return junctionGraphic.getJunction();
    }

    public JunctionGraphic getJunctionGraphic() {
        return junctionGraphic;
    }
}
