/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.phetcommon.ImagePComboBox;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
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
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public abstract class AbstractGunGraphic extends PNode {
    protected static final String GUN_RESOURCE = "images/raygun3-centerbarrel.gif";
    public static final int GUN_PARTICLE_OFFSET = 35;

    private SchrodingerPanel schrodingerPanel;
    private PImage gunImageGraphic;

//    private PNode gunControls;
    private ArrayList listeners = new ArrayList();

    private ImagePComboBox comboBox;

    public AbstractGunGraphic( final SchrodingerPanel schrodingerPanel ) {
        this.schrodingerPanel = schrodingerPanel;
        try {
            BufferedImage image = ImageLoader.loadBufferedImage( GUN_RESOURCE );
            gunImageGraphic = new PImage( image );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        updateGunLocation();
        addChild( gunImageGraphic );
        this.comboBox = initComboBox();
        setVisible( true );
    }

    protected void layoutChildren() {
        super.layoutChildren();
    }

    protected void updateGunLocation() {
        gunImageGraphic.setOffset( getGunLocation() );
    }

    protected abstract Point getGunLocation();

    protected abstract ImagePComboBox initComboBox();

    public DiscreteModel getDiscreteModel() {
        return schrodingerPanel.getDiscreteModel();
    }

    public int getControlOffsetY() {
        return 50;
    }

    public int getFireButtonInsetDX() {
        return -50;
    }

    public PImage getGunImageGraphic() {
        return gunImageGraphic;
    }

    public SchrodingerModule getSchrodingerModule() {
        return schrodingerPanel.getSchrodingerModule();
    }

    public ImagePComboBox getComboBox() {
        return comboBox;
    }

    static Potential getPotential( AbstractGunGraphic abstractGunGraphic ) {
        return abstractGunGraphic.schrodingerPanel.getDiscreteModel().getPotential();
    }

    public int getGunWidth() {
        return (int)gunImageGraphic.getFullBounds().getWidth();
    }

    public SchrodingerPanel getSchrodingerPanel() {
        return schrodingerPanel;
    }

    public PSwingCanvas getComponent() {
        return schrodingerPanel;
    }

    protected abstract void setGunControls( JComponent gunControls );

//    public void setGunControls( PNode gunControls ) {
//        Toolkit.getDefaultToolkit().beep();
//        if( this.gunControls != null ) {
//            removeChild( gunControls );
//        }
//        this.gunControls = gunControls;
//        addChild( gunControls );
//        layoutChildren();
//    }

//    public PNode getGunControls() {
//        return gunControls;
//    }

    public void removeGunControls() {
        setGunControls( null );
//        if( gunControls != null ) {
//            removeChild( gunControls );
//            gunControls = null;
//        }
    }

    public Map getModelParameters() {
        return new HashMap();
    }

    public abstract GunControlPanel getGunControlPanel();

    public static interface MomentumChangeListener {
        void momentumChanged( double val );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public boolean containsListener( Listener listener ) {
        return listeners.contains( listener );
    }

    protected void notifyFireListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.gunFired();
        }
    }

    public static interface Listener {
        void gunFired();
    }
}
