/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.cck3.CCK3Module;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 9:29:56 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class InteractiveBranchGraphic extends DefaultInteractiveGraphic {
    private boolean isDragging = false;
    private ImmutableVector2D.Double toStart;//a vector from the mouse to the start junction
    private ImmutableVector2D.Double toEnd;
    private Junction startTarget;
    private Junction endTarget;
    private BranchGraphic branchGraphic;
    private CCK3Module module;

    public InteractiveBranchGraphic( final CircuitGraphic circuitGraphic, final BranchGraphic branchGraphic, final ModelViewTransform2D transform,CCK3Module module ) {
        super( branchGraphic );
        this.branchGraphic = branchGraphic;
        this.module = module;
        addCursorHandBehavior();
        MouseInputListener mouseListener = new WireMouseListener( circuitGraphic, branchGraphic );
        addMouseInputListener( mouseListener );

        BranchPopupMenu bpm = new BranchPopupMenu( circuitGraphic.getCircuit(), branchGraphic.getBranch(),module );
        addPopupMenuBehavior( bpm );
    }

    public BranchGraphic getBranchGraphic() {
        return branchGraphic;
    }

    public Branch getBranch() {
        return branchGraphic.getBranch();
    }

    class BranchPopupMenu extends JPopupMenu {
        private Circuit circuit;
        private Branch branch;

        public BranchPopupMenu( final Circuit circuit, final Branch branch,final CCK3Module module ) {
            this.circuit = circuit;
            this.branch = branch;
            JMenuItem item = new JMenuItem( "Remove" );
            item.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
//                    circuit.remove( branch );
                    module.removeBranch(branch);
                }
            } );
            add( item );
        }
    }
}
