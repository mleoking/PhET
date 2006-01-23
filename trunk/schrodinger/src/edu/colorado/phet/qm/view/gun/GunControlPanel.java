/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.qm.phetcommon.ShinyPanel;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jan 23, 2006
 * Time: 8:53:50 AM
 * Copyright (c) Jan 23, 2006 by Sam Reid
 */

public class GunControlPanel extends VerticalLayoutPanel {
    private JComponent gunControl;
    private ShinyPanel shinyPanel;
    private PSwing gunControlPSwing;

    public GunControlPanel( SchrodingerPanel schrodingerPanel ) {
        setOpaque( false );
        shinyPanel = new ShinyPanel( this );
        gunControlPSwing = new PSwing( schrodingerPanel, shinyPanel );
    }

    public void setGunControls( JComponent gunControl ) {
        if( this.gunControl != null ) {
            remove( this.gunControl );
        }
        this.gunControl = gunControl;
        if( gunControl != null ) {
            addFullWidth( gunControl );
            gunControl.setDoubleBuffered( false );//workaround since pswing doesn't handle re-componenting gracefully.
            shinyPanel.update();
        }
        invalidate();
        doLayout();
        gunControlPSwing.computeBounds();
    }

    public PNode getPSwing() {
        return gunControlPSwing;

    }

    public Component add( Component comp ) {
        init( comp );
        return super.add( comp );
    }

    void init( Component c ) {
        Component[] children = null;
        if( c instanceof Container ) {
            children = ( (Container)c ).getComponents();
        }

        if( children != null ) {
            for( int j = 0; j < children.length; j++ ) {
                init( children[j] );
            }
        }

        if( c instanceof JComponent ) {
            ( (JComponent)c ).setDoubleBuffered( false );
        }
    }
}
