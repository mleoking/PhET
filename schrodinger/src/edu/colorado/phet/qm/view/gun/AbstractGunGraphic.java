/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.piccolo.pswing.PSwingCanvas;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.phetcommon.ImagePComboBox;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
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
    private ImagePComboBox comboBox;
    private PSwing comboBoxGraphic;
    private PNode gunControls;

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

        comboBoxGraphic = new PSwing( schrodingerPanel, comboBox );
        comboBox.setEnvironment( comboBoxGraphic, schrodingerPanel );
        addChild( comboBoxGraphic );
        setVisible( true );
    }

    protected void layoutChildren() {
        super.layoutChildren();
        comboBoxGraphic.setOffset( 0, -comboBoxGraphic.getFullBounds().getHeight() );
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

    public void setLocation( int x, int y ) {
        super.setOffset( x, y );
        double scaleX = 1.0;
        double scaleY = 1.0;
        comboBox.setBounds( (int)( ( x - comboBox.getPreferredSize().width - 2 ) * scaleX ), (int)( ( y + getControlOffsetY() ) * scaleY ),
                            comboBox.getPreferredSize().width, comboBox.getPreferredSize().height );
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        comboBox.setVisible( visible );
    }

    public PImage getGunImageGraphic() {
        return gunImageGraphic;
    }

    public SchrodingerModule getSchrodingerModule() {
        return schrodingerPanel.getSchrodingerModule();
    }

    public JComboBox getComboBox() {
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

    public void setGunControls( PNode gunControls ) {
        this.gunControls = gunControls;
        addChild( gunControls );
        layoutChildren();
    }

    public PNode getGunControls() {
        return gunControls;
    }

    public void removeGunControls() {
        if( gunControls != null ) {
            removeChild( gunControls );
            gunControls = null;
        }
    }

    public Map getModelParameters() {
        return new HashMap();
    }

    public static interface MomentumChangeListener {
        void momentumChanged( double val );
    }

    public PSwing getComboBoxGraphic() {
        return comboBoxGraphic;
    }
}
