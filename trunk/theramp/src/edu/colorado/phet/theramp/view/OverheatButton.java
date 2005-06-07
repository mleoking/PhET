/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.ShadowHTMLGraphic;
import edu.colorado.phet.theramp.model.RampModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jun 6, 2005
 * Time: 11:34:54 PM
 * Copyright (c) Jun 6, 2005 by Sam Reid
 */

public class OverheatButton extends GraphicLayerSet {
    private RampModel rampModel;
    private double max;
    private RampPanel rampPanel;

    public OverheatButton( RampPanel rampPanel, final RampModel rampModel, double maxDisplayableEnergy ) {
        super( rampPanel );
        this.rampModel = rampModel;
        this.rampPanel = rampPanel;
        ShadowHTMLGraphic shadowHTMLGraphic = new ShadowHTMLGraphic( rampPanel, "Warning: overheated.", new Font( "Lucida Sans", Font.BOLD, 24 ), Color.red, 1, 1, Color.black );
        addGraphic( shadowHTMLGraphic );
        JButton overheat = new JButton( "Remove Heat" );
        overheat.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rampModel.clearHeat();
            }
        } );
        PhetGraphic graphic = PhetJComponent.newInstance( rampPanel, overheat );
        graphic.setLocation( 0, shadowHTMLGraphic.getHeight() );
        addGraphic( graphic );
        rampModel.addListener( new RampModel.Listener() {
            public void appliedForceChanged() {
            }

            public void zeroPointChanged() {
            }

            public void stepFinished() {
                update();
            }
        } );
        this.max = maxDisplayableEnergy * 0.8;
        update();
    }

    private void update() {
        if( rampModel.getThermalEnergy() >= max && !isVisible() ) {
            setVisible( true );
            Point viewLocation = rampPanel.getRampGraphic().getViewLocation( 0 );
            setLocation( viewLocation.x, viewLocation.y - 10 );
        }
        else if( rampModel.getThermalEnergy() < max ) {
            setVisible( false );
        }
    }
}
