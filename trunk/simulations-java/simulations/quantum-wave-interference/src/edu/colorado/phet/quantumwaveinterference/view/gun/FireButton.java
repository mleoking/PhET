/*  */
package edu.colorado.phet.quantumwaveinterference.view.gun;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.quantumwaveinterference.QWIModule;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.view.QWIPanel;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Feb 6, 2006
 * Time: 10:32:23 PM
 */

public class FireButton extends JButton {
    private ImageIcon outIcon;
    private ImageIcon inIcon;
    private FireParticle fireParticle;

    public FireButton( FireParticle fireParticle ) {

        super( QWIResources.getString( "gun.fire" ) );
        this.fireParticle = fireParticle;
        setFont( new PhetFont( Font.BOLD, 18 ) );
        setForeground( Color.red );
        setMargin( new Insets( 2, 2, 2, 2 ) );
        setVerticalTextPosition( AbstractButton.BOTTOM );
        setHorizontalTextPosition( AbstractButton.CENTER );
        try {
            outIcon = new ImageIcon( ImageLoader.loadBufferedImage( "quantum-wave-interference/images/button-out-40.gif" ) );
            inIcon = new ImageIcon( ImageLoader.loadBufferedImage( "quantum-wave-interference/images/button-in-40.gif" ) );
            setIcon( outIcon );
            setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                if( fireButtonEnabled() ) {
                    setIcon( inIcon );
                }
            }

            public void mouseReleased( MouseEvent e ) {
                if( fireButtonEnabled() ) {
                    setIcon( outIcon );
                }
            }
        } );

        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
//                setEnabled( false );
                fireParticle();
            }

        } );
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                pullback();
            }


            public void mouseReleased( MouseEvent e ) {
                releasePullback();
            }
        } );

    }

    private void releasePullback() {
        if( fireButtonEnabled() ) {
            updateGunLocation();
        }
    }

    private void pullback() {
        if( fireButtonEnabled() ) {
            updateGunLocation();
            getGunImageGraphic().translate( 0, 10 );
        }
    }

    private void fireParticle() {
        clearAndFire();
        setIcon( outIcon );
        updateGunLocation();
        getSchrodingerPanel().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }

    public void addButtonEnableDisable() {
        getSchrodingerModule().getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                double magnitude = getSchrodingerModule().getQWIModel().getWavefunction().getMagnitude();
                if( magnitude <= AutoFire.THRESHOLD || Double.isNaN( magnitude ) ) {
                    if( !fireButtonEnabled() ) {
                        setEnabled( true );
                    }
                }
                else {
                    if( fireButtonEnabled() ) {
                        setEnabled( false );
                    }
                }
            }
        } );
    }

    private boolean fireButtonEnabled() {
        return isEnabled();
    }

    private QWIModule getSchrodingerModule() {
        return fireParticle.getSchrodingerModule();
    }

    private QWIPanel getSchrodingerPanel() {
        return getSchrodingerModule().getSchrodingerPanel();
    }

    private void updateGunLocation() {
        fireParticle.updateGunLocation();
    }

    private void clearAndFire() {
        fireParticle.clearAndFire();
    }

    private PNode getGunImageGraphic() {
        return fireParticle.getGunImageGraphic();
    }
}
