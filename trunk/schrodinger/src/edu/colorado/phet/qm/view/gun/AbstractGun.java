/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.util.QMLogger;
import edu.colorado.phet.qm.view.SchrodingerPanel;

import javax.swing.*;
import java.awt.event.ComponentEvent;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 4:03:38 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public abstract class AbstractGun extends GraphicLayerSet {
    private SchrodingerPanel schrodingerPanel;
    private PhetImageGraphic gunImageGraphic;
    private JComboBox comboBox;

    public AbstractGun( final SchrodingerPanel schrodingerPanel ) {
        super( schrodingerPanel );
        this.schrodingerPanel = schrodingerPanel;
//        gunImageGraphic = new PhetImageGraphic( getComponent(), "images/laser.gif" );
//        gunImageGraphic = new PhetImageGraphic( getComponent(), "images/raygun3-scaled.gif" );
        gunImageGraphic = new PhetImageGraphic( getComponent(), "images/raygun3-200x160-scaled-matt.gif" );
        gunImageGraphic.setLocation( -10, 35 );
        addGraphic( gunImageGraphic );
        this.comboBox = initComboBox();
        schrodingerPanel.add( comboBox );
        setVisible( true );
    }

    protected abstract JComboBox initComboBox();

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
        super.setLocation( x, y );
        double scaleX = schrodingerPanel.getGraphicTx().getScaleX();
        double scaleY = schrodingerPanel.getGraphicTx().getScaleY();
        comboBox.setBounds( (int)( ( x - comboBox.getPreferredSize().width - 2 ) * scaleX ), (int)( ( y + getControlOffsetY() ) * scaleY ),
                            comboBox.getPreferredSize().width, comboBox.getPreferredSize().height );
        QMLogger.debug( "comboBox.getLocation() = " + comboBox.getLocation() );
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        comboBox.setVisible( visible );
    }

    public void componentResized( ComponentEvent e ) {
        this.setLocation( getLocation() );//to fix combobox
    }

    public PhetImageGraphic getGunImageGraphic() {
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
        return gunImageGraphic.getWidth();
    }

    public SchrodingerPanel getSchrodingerPanel() {
        return schrodingerPanel;
    }


    protected void setComboBox( JComboBox comboBox ) {
        this.comboBox = comboBox;
    }

    public static interface MomentumChangeListener {
        void momentumChanged( double val );
    }
}
