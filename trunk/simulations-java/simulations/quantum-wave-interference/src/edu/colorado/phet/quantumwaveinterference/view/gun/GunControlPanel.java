/*  */
package edu.colorado.phet.quantumwaveinterference.view.gun;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.phetcommon.ShinyPanel;
import edu.colorado.phet.quantumwaveinterference.view.QWIPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jan 23, 2006
 * Time: 8:53:50 AM
 */

public class GunControlPanel extends VerticalLayoutPanel {
    private JComponent gunControl;
    private ShinyPanel shinyPanel;
    private PSwing gunControlPSwing;
    private JLabel titleLabel;

    public GunControlPanel( QWIPanel QWIPanel ) {
        setOpaque( false );
        shinyPanel = new ShinyGunControlPanel( this );
        gunControlPSwing = new PSwing( shinyPanel );
        titleLabel = new JLabel( QWIResources.getString( "gun.controls" ) ) {
            protected void paintComponent( Graphics g ) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintComponent( g );
            }
        };
        titleLabel.setForeground( Color.white );
        titleLabel.setOpaque( false );
        titleLabel.setFont( new PhetFont( Font.BOLD, 14 ) );
        add( titleLabel );
    }

    protected JLabel getTitleLabel() {
        return titleLabel;
    }

    public JComponent getGunControls() {
        return gunControl;
    }

    static class ShinyGunControlPanel extends ShinyPanel {

        public ShinyGunControlPanel( JComponent component ) {
            super( component, new Color( 91, 91, 91 ), new Color( 80, 80, 80 ) );
        }
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
        gunControlPSwing.getComponent().invalidate();//TODO: some children were coming up empty!  This fixes it.
        gunControlPSwing.getComponent().revalidate();
        gunControlPSwing.getComponent().repaint();
        gunControlPSwing.getComponent().doLayout();
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
