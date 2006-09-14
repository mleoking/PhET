/** Sam Reid*/
package edu.colorado.phet.cck.phetgraphics_cck.circuit;

import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.CircuitComponent;
import edu.colorado.phet.cck.phetgraphics_cck.CCKModule;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common_cck.view.graphics.DefaultInteractiveGraphic;

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
    private CircuitGraphic circuitGraphic;
    private Branch branch;

    public InteractiveComponentJunctionGraphic( final CircuitGraphic cg, final JunctionGraphic junctionGraphic, final CircuitComponent branch, CCKModule module ) {
        super( junctionGraphic );
        this.circuitGraphic = cg;
        this.branch = branch;
        addCursorHandBehavior();
        this.junctionGraphic = junctionGraphic;
        MouseInputListener mouse = new MouseInputAdapter() {

            public void mouseDragged( MouseEvent e ) {
                Point2D pt = junctionGraphic.getTransform().viewToModel( e.getPoint() );
                Junction opposite = branch.opposite( junctionGraphic.getJunction() );
                AbstractVector2D vec = new ImmutableVector2D.Double( opposite.getPosition(), pt );
                vec = vec.getInstanceOfMagnitude( branch.getComponentLength() );
                Point2D dst = vec.getDestination( opposite.getPosition() );
                junctionGraphic.getJunction().setPosition( dst.getX(), dst.getY() );
                branch.notifyObservers();
                cg.getCircuit().fireJunctionsMoved();
            }

            public void mousePressed( MouseEvent e ) {
                if( e.isControlDown() ) {
                    junctionGraphic.getJunction().setSelected( true );
                }
                else {
                    cg.getCircuit().setSelection( junctionGraphic.getJunction() );
                }
            }

            public void mouseReleased( MouseEvent e ) {
                circuitGraphic.bumpAway( InteractiveComponentJunctionGraphic.this );
            }
        };
        addMouseInputListener( mouse );
        JunctionPopupMenu menu = new JunctionPopupMenu( getJunction(), cg, module );
        addPopupMenuBehavior( menu );
    }

    private Junction getJunction() {
        return junctionGraphic.getJunction();
    }

    public JunctionGraphic getJunctionGraphic() {
        return junctionGraphic;
    }

    public void delete() {
        junctionGraphic.delete();
    }
}
