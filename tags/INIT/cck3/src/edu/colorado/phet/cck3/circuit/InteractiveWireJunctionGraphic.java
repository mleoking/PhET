/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 9:10:07 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class InteractiveWireJunctionGraphic extends DefaultInteractiveGraphic implements HasJunctionGraphic {
    private CircuitGraphic circuitGraphic;
    private JunctionGraphic junctionGraphic;
    private ModelViewTransform2D transform;
    private Junction mytarget;

    public InteractiveWireJunctionGraphic( final CircuitGraphic circuitGraphic, final JunctionGraphic jg, final ModelViewTransform2D transform, CCK3Module module ) {
        super( jg );
        final Circuit circuit = circuitGraphic.getCircuit();
        this.circuitGraphic = circuitGraphic;
        this.junctionGraphic = jg;
        this.transform = transform;
        addCursorHandBehavior();

        MouseInputAdapter input = new MouseInputAdapter() {
            public void mouseEntered( MouseEvent e ) {
//                System.out.println( "Mouse entered junction=" + jg.getJunction() );
            }

            public void mouseDragged( MouseEvent e ) {
//                System.out.println( "Dragging junction="+junctionGraphic.getJunction() );
                Point2D.Double pt = transform.viewToModel( e.getX(), e.getY() );

                //find a potential match.
                Junction target = circuitGraphic.getBestDragMatch( jg.getJunction(), pt );
                Point2D dst = null;
                if( target != null ) {
                    dst = target.getPosition();
                    mytarget = target;
                }
                else {
                    dst = pt;
                    mytarget = null;
                }
                Point2D orig = jg.getJunction().getPosition();
                ImmutableVector2D vec = new ImmutableVector2D.Double( orig, dst );
                Branch[] connections = circuit.getStrongConnections( jg.getJunction() );
                BranchSet bs = new BranchSet( circuit, connections );
                bs.addJunction( jg.getJunction() );
                bs.translate( vec );
            }

            public void mousePressed( MouseEvent e ) {
            }

            public void mouseReleased( MouseEvent e ) {
                if( mytarget != null ) {
                    //make a connection.
                    //This means killing one junction and its corresponding graphic.
//                    System.out.println( "Released, target=" + mytarget );
                    circuitGraphic.collapseJunctions( mytarget, jg.getJunction() );
//                    System.out.println( "released, circuitGraphic=" + circuitGraphic.getCircuit() );
//                    System.out.println( "circuitGraphic graphic=" + circuitGraphic );
                }
            }
        };
        addMouseInputListener( input );
        JPopupMenu menu = new JunctionPopupMenu( getJunction(), this.circuitGraphic, module );
//        menu.add( new JunctionSplitter( getJunction(), circuitGraphic ).toJMenuItem());
        addPopupMenuBehavior( menu );
    }

    public String toString() {
        return "InteractiveJuncitonGraphic, Junction=" + getJunction();
    }

    public Junction getJunction() {
        return junctionGraphic.getJunction();
    }

    public JunctionGraphic getJunctionGraphic() {
        return junctionGraphic;
    }

    public SimpleObservable getObservable() {
        return junctionGraphic.getJunction();
    }

}
