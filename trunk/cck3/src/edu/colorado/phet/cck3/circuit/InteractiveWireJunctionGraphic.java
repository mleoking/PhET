/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.common.RepaintyMenu;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

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
    private CircuitGraphic.DragMatch dragMatch;

    public InteractiveWireJunctionGraphic( final CircuitGraphic circuitGraphic, final JunctionGraphic jg, final ModelViewTransform2D transform, CCK3Module module ) {
        super( jg );
        final Circuit circuit = circuitGraphic.getCircuit();
        this.circuitGraphic = circuitGraphic;
        this.junctionGraphic = jg;
        this.transform = transform;
        addCursorHandBehavior();

        MouseInputAdapter input = new MouseInputAdapter() {
            public void mouseEntered( MouseEvent e ) {
            }

            public void mouseDragged( MouseEvent e ) {
                Point2D pt = transform.viewToModel( e.getX(), e.getY() );

                //find a potential match.
                Branch[] sc = circuit.getStrongConnections( getJunction() );
                Junction[] j = Circuit.getJunctions( sc );
                ArrayList ju = new ArrayList( Arrays.asList( j ) );
                if( !ju.contains( getJunction() ) ) {
                    ju.add( getJunction() );
                }

                Junction[] jx = (Junction[])ju.toArray( new Junction[0] );
                Vector2D dx = new Vector2D.Double( getJunction().getPosition(), pt );
                dragMatch = circuitGraphic.getBestDragMatch( jx, dx );
                if( dragMatch != null ) {
                    dx = dragMatch.getVector();
                }

                BranchSet bs = new BranchSet( circuit, sc );
                bs.addJunction( jg.getJunction() );
                bs.translate( dx );
            }

            public void mousePressed( MouseEvent e ) {
                dragMatch = null;
                if( e.isControlDown() ) {
                    junctionGraphic.getJunction().setSelected( true );
                }
                else {
                    circuit.setSelection( junctionGraphic.getJunction() );
                }
            }

            public void mouseReleased( MouseEvent e ) {
                if( dragMatch != null ) {
                    circuitGraphic.collapseJunctions( dragMatch.getSource(), dragMatch.getTarget() );
                    dragMatch = null;
                }

            }
        };
        addMouseInputListener( input );
        RepaintyMenu menu = new JunctionPopupMenu( getJunction(), this.circuitGraphic, module );
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

    public void delete() {
        junctionGraphic.delete();
    }

    public SimpleObservable getObservable() {
        return junctionGraphic.getJunction();
    }

}
