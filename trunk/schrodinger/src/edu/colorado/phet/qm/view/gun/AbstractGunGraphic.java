/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.model.QWIModel;
import edu.colorado.phet.qm.phetcommon.ImagePComboBox;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.piccolo.BlueGunDetails;
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
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public abstract class AbstractGunGraphic extends PNode {
    protected static final String GUN_RESOURCE = "images/raygun3-centerbarrel-2.gif";
    public static final int GUN_PARTICLE_OFFSET = 35;
    private SchrodingerPanel schrodingerPanel;
    private PImage gunImageGraphic;
    private ArrayList listeners = new ArrayList();
    private ImagePComboBox comboBox;
    private PNode onGunGraphic;

    public AbstractGunGraphic( final SchrodingerPanel schrodingerPanel ) {
        this.schrodingerPanel = schrodingerPanel;
        try {
            BufferedImage image = ImageLoader.loadBufferedImage( GUN_RESOURCE );
            gunImageGraphic = new PImage( image );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        addChild( gunImageGraphic );
        onGunGraphic = new PSwing( schrodingerPanel, new JButton( "Fire" ) );
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

//    public void reset() {
//
//    }

    public static interface MomentumChangeListener {
        void momentumChanged( double val );
    }

    protected void setOnGunControl( PNode pswing ) {
        if( onGunGraphic != null ) {
            removeChild( onGunGraphic );
        }
        onGunGraphic = pswing;
        addChild( onGunGraphic );
        schrodingerPanel.invalidate();
        schrodingerPanel.doLayout();
        schrodingerPanel.repaint();
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
        return schrodingerPanel.getDiscreteModel();
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
