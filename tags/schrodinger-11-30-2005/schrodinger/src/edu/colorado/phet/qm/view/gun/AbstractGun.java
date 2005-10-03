/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.piccolo.pswing.PSwingCanvas;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.phetcommon.ImagePComboBox;
import edu.colorado.phet.qm.util.QMLogger;
import edu.colorado.phet.qm.view.swing.SchrodingerPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 4:03:38 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public abstract class AbstractGun extends PNode {
    private SchrodingerPanel schrodingerPanel;

    private PImage gunImageGraphic;
    private ImagePComboBox comboBox;
    private PSwing comboBoxGraphic;

    public AbstractGun( final SchrodingerPanel schrodingerPanel ) {
        super();
        this.schrodingerPanel = schrodingerPanel;
        String imageResourceName = getGunImageResource();
        try {
            BufferedImage image = ImageLoader.loadBufferedImage( imageResourceName );
            gunImageGraphic = new PImage( image );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        initGunLocation();
        addChild( gunImageGraphic );
        this.comboBox = initComboBox();

        comboBoxGraphic = new PSwing( schrodingerPanel, comboBox );
        comboBox.setEnvironment( comboBoxGraphic, schrodingerPanel );
        addChild( comboBoxGraphic );
        setVisible( true );
    }

    protected void layoutChildren() {
        super.layoutChildren();
        comboBoxGraphic.setOffset( -100, 50 );
    }

    protected void initGunLocation() {
        gunImageGraphic.setOffset( getOrigGunLocation() );
    }

    protected String getGunImageResource() {
//        String imageResourceName = "images/raygun3-200x160-scaled-matt.gif";
        String imageResourceName = "images/raygun3-centerbarrel.gif";
        return imageResourceName;
    }

    protected Point getOrigGunLocation() {
        return new Point( -10, 35 );
    }

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
//        double scaleX = schrodingerPanel.getGraphicTx().getScaleX();
//        double scaleY = schrodingerPanel.getGraphicTx().getScaleY();
        //todo piccolo
        double scaleX = 1.0;
        double scaleY = 1.0;
        comboBox.setBounds( (int)( ( x - comboBox.getPreferredSize().width - 2 ) * scaleX ), (int)( ( y + getControlOffsetY() ) * scaleY ),
                            comboBox.getPreferredSize().width, comboBox.getPreferredSize().height );
        QMLogger.debug( "comboBox.getLocation() = " + comboBox.getLocation() );
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        comboBox.setVisible( visible );
    }

    //todo piccolo
//    public void componentResized( ComponentEvent e ) {
//        this.setLocation( getOffset());//to fix combobox
//    }

    public PImage getGunImageGraphic() {
        return gunImageGraphic;
    }

    public SchrodingerModule getSchrodingerModule() {
        return schrodingerPanel.getSchrodingerModule();
    }

    public JComboBox getComboBox() {
        return comboBox;
    }

    static Potential getPotential( AbstractGun abstractGun ) {
        return abstractGun.schrodingerPanel.getDiscreteModel().getPotential();
    }

    public int getGunWidth() {
        return (int)gunImageGraphic.getFullBounds().getWidth();
    }

    public SchrodingerPanel getSchrodingerPanel() {
        return schrodingerPanel;
    }

//    protected void setComboBox( JComboBox comboBox ) {
//        this.comboBox = comboBox;
//    }

    public PSwingCanvas getComponent() {
        return schrodingerPanel;
    }

    public static interface MomentumChangeListener {
        void momentumChanged( double val );
    }

    public PSwing getComboBoxGraphic() {
        return comboBoxGraphic;
    }
}
