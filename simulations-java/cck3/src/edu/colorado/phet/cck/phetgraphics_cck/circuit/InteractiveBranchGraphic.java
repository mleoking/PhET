/** Sam Reid*/
package edu.colorado.phet.cck.phetgraphics_cck.circuit;

import edu.colorado.phet.cck.common.JPopupMenuRepaintWorkaround;
import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.phetgraphics_cck.CCKPhetgraphicsModule;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common_cck.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;

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
public class InteractiveBranchGraphic extends DefaultInteractiveGraphic implements Deletable {
    private boolean isDragging = false;
    private ImmutableVector2D.Double toStart;//a vector from the mouse to the start junction
    private ImmutableVector2D.Double toEnd;
    private Junction startTarget;
    private Junction endTarget;
    private BranchGraphic branchGraphic;
    private CCKPhetgraphicsModule module;

    public InteractiveBranchGraphic( final CircuitGraphic circuitGraphic, final BranchGraphic branchGraphic, final ModelViewTransform2D transform, CCKPhetgraphicsModule module ) {
        super( branchGraphic );
        this.branchGraphic = branchGraphic;
        this.module = module;
        addCursorHandBehavior();
        MouseInputListener mouseListener = new WireMouseListener( circuitGraphic, branchGraphic );
        addMouseInputListener( mouseListener );

        BranchPopupMenu bpm = new BranchPopupMenu( circuitGraphic.getCircuit(), branchGraphic.getBranch(), module );
        addPopupMenuBehavior( bpm );
    }

    public BranchGraphic getBranchGraphic() {
        return branchGraphic;
    }

    public Branch getBranch() {
        return branchGraphic.getBranch();
    }

    public void delete() {
        branchGraphic.delete();
    }

    class BranchPopupMenu extends JPopupMenuRepaintWorkaround {
        private Circuit circuit;
        private Branch branch;

        public BranchPopupMenu( final Circuit circuit, final Branch branch, final CCKPhetgraphicsModule module ) {
            super( module.getApparatusPanel() );
            this.circuit = circuit;
            this.branch = branch;
            JMenuItem item = new JMenuItem( SimStrings.get( "InteractiveBranchGraphic.RemoveMenuItem" ) );
            item.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.removeBranch( branch );
                }
            } );
            add( item );
        }
    }
}
