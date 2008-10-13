/*  */
package edu.colorado.phet.quantumwaveinterference.view.gun;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.quantumwaveinterference.QWIModule;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.model.Potential;
import edu.colorado.phet.quantumwaveinterference.model.QWIModel;
import edu.colorado.phet.quantumwaveinterference.phetcommon.ImagePComboBox;
import edu.colorado.phet.quantumwaveinterference.view.QWIPanel;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.BlueGunDetails;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 4:03:38 PM
 */

public abstract class AbstractGunNode extends PNode {
    private QWIPanel QWIPanel;
    private PImage gunImageGraphic;
    private ArrayList listeners = new ArrayList();
    private ImagePComboBox comboBox;
    private PNode onGunGraphic;

    protected static final String GUN_RESOURCE = "quantum-wave-interference/images/raygun3-centerbarrel-2.gif";
    public static final int GUN_PARTICLE_OFFSET = 35;

    public AbstractGunNode( final QWIPanel QWIPanel ) {
        this.QWIPanel = QWIPanel;
        try {
            BufferedImage image = ImageLoader.loadBufferedImage( GUN_RESOURCE );
            gunImageGraphic = new PImage( image );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        addChild( gunImageGraphic );
        onGunGraphic = new PSwing( new JButton( QWIResources.getString( "gun.fire" ) ) );
        addChild( onGunGraphic );
        this.comboBox = initComboBox();
        updateGunLocation();
        setVisible( true );
    }

    public PNode getOnGunGraphic() {
        return onGunGraphic;
    }

    public abstract GunControlPanel getGunControlPanel();

    public abstract boolean isPhotonMode();

    public static interface MomentumChangeListener {
        void momentumChanged( double val );
    }

    protected void setOnGunControl( PNode pswing ) {
        if( onGunGraphic != null ) {
            removeChild( onGunGraphic );
        }
        onGunGraphic = pswing;
        addChild( onGunGraphic );
        QWIPanel.invalidate();
        QWIPanel.doLayout();
        QWIPanel.repaint();
        invalidateLayout();
        repaint();
    }

    protected void layoutChildren() {
        super.layoutChildren();
        onGunGraphic.setOffset( gunImageGraphic.getFullBounds().getX() + gunImageGraphic.getFullBounds().getWidth() / 2 - onGunGraphic.getFullBounds().getWidth() / 2 + BlueGunDetails.onGunControlDX, BlueGunDetails.gunControlAreaY + gunImageGraphic.getFullBounds().getY() );
    }

    public void updateGunLocation() {
        gunImageGraphic.setOffset( getGunLocation() );
        layoutChildren();
    }

    protected abstract Point getGunLocation();

    protected abstract ImagePComboBox initComboBox();

    public QWIModel getDiscreteModel() {
        return QWIPanel.getDiscreteModel();
    }

    public PImage getGunImageGraphic() {
        return gunImageGraphic;
    }

    public QWIModule getSchrodingerModule() {
        return QWIPanel.getSchrodingerModule();
    }

    public ImagePComboBox getComboBox() {
        return comboBox;
    }

    static Potential getPotential( AbstractGunNode abstractGunNode ) {
        return abstractGunNode.QWIPanel.getDiscreteModel().getPotential();
    }

    public int getGunWidth() {
        return (int)gunImageGraphic.getFullBounds().getWidth();
    }

    public QWIPanel getSchrodingerPanel() {
        return QWIPanel;
    }

    public PSwingCanvas getComponent() {
        return QWIPanel;
    }

    protected abstract void setGunControls( JComponent gunControls );

    public void removeGunControls() {
        setGunControls( null );
    }

    public Map getModelParameters() {
        return new HashMap();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public boolean containsListener( Listener listener ) {
        return listeners.contains( listener );
    }

    protected void notifyGunFired() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.gunFired();
        }
    }

    public static interface Listener {
        void gunFired();
    }
}
