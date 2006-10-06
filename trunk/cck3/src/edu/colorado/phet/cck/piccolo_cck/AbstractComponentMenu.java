package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.common.JPopupMenuRepaintWorkaround;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.CircuitComponent;
import edu.colorado.phet.cck.model.components.SeriesAmmeter;
import edu.colorado.phet.cck.model.components.Switch;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Oct 6, 2006
 * Time: 7:47:34 AM
 * Copyright (c) Oct 6, 2006 by Sam Reid
 */

public class AbstractComponentMenu extends JPopupMenuRepaintWorkaround {
    public AbstractComponentMenu( Component target ) {
        super( target );
    }

    protected JCheckBoxMenuItem createShowValueButton( JPopupMenuRepaintWorkaround menu, final ICCKModule module, final Branch branch ) {
        final JCheckBoxMenuItem showValue = new JCheckBoxMenuItem( SimStrings.get( "CircuitComponentInteractiveGraphic.ShowValueMenuItem" ) );
        menu.addPopupMenuListener( new PopupMenuListener() {
            public void popupMenuCanceled( PopupMenuEvent e ) {
            }

            public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {
            }

            public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {
                showValue.setSelected( module.isReadoutVisible( branch ) );
            }
        } );
        showValue.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setReadoutVisible( branch, showValue.isSelected() );
            }
        } );
        if( branch instanceof CircuitComponent && !( branch instanceof SeriesAmmeter ) && !( branch instanceof Switch ) ) {
            if( module.getParameters().allowShowReadouts() ) {
                menu.add( showValue );
            }

        }
        return showValue;
    }

    protected void addRemoveButton( JPopupMenuRepaintWorkaround menu, final ICCKModule module, final Branch branch ) {
        JMenuItem remove = new JMenuItem( SimStrings.get( "CircuitComponentInteractiveGraphic.RemoveMenuItem" ) );
        remove.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getCCKModel().getCircuit().removeBranch( branch );
            }
        } );
        int num = menu.getComponentCount();
        if( num > 0 ) {
            menu.addSeparator();
        }
        menu.add( remove );
    }


}
