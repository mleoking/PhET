/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.RepaintDebugGraphic;
import edu.colorado.phet.forces1d.Force1DModule;
import edu.colorado.phet.forces1d.common.ApparatusPanel3;
import edu.colorado.phet.forces1d.common.WiggleMe;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDevice;
import edu.colorado.phet.forces1d.model.Force1DModel;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Jan 16, 2005
 * Time: 8:11:43 PM
 * Copyright (c) Jan 16, 2005 by Sam Reid
 */

public class FreeBodyDiagramPanel {

    private FreeBodyDiagram freeBodyDiagram;
    private ApparatusPanel3 fbdPanel;
    private WiggleMe fbdWiggleMe;
    private PlotDevice forcePlotDevice;

    public FreeBodyDiagramPanel( final Force1DModule module ) {
        fbdPanel = new ApparatusPanel3( module.getModel(), module.getClock() );
        fbdPanel.addGraphicsSetup( new BasicGraphicsSetup() );
        int fbdWidth = 180;
        fbdPanel.setPreferredSize( new Dimension( fbdWidth, fbdWidth ) );
        freeBodyDiagram = new FreeBodyDiagram( fbdPanel, module );
        freeBodyDiagram.setComponent( fbdPanel );
        fbdPanel.addGraphic( freeBodyDiagram );

        int fbdInset = 3;
        freeBodyDiagram.setBounds( fbdInset, fbdInset, fbdWidth - 2 * fbdInset, fbdWidth - 2 * fbdInset );

        WiggleMe.Target target = new WiggleMe.Target() {
            public Point getLocation() {
                return new Point( fbdPanel.getWidth() - 10, fbdPanel.getHeight() / 2 );
            }

            public int getHeight() {
                return 0;
            }
        };
        fbdWiggleMe = new WiggleMe( fbdPanel, "Click to set Force", target, new Font( "Lucida Sans", Font.BOLD, 14 ), 1, 1 );
        fbdWiggleMe.setVisible( false );

        module.getForceModel().addListener( new Force1DModel.Listener() {
            public void appliedForceChanged() {
                fbdWiggleMe.setVisible( false );
                fbdPanel.removeGraphic( fbdWiggleMe );
            }

            public void gravityChanged() {
            }
        } );
        forcePlotDevice = module.getForcePanel().getPlotDevice();
        MouseInputAdapter listener = new MouseInputAdapter() {
            public void mousePressed( MouseEvent e ) {
                forcePlotDevice.getPlotDeviceModel().setRecordMode();
                forcePlotDevice.getPlotDeviceModel().setPaused( false );
            }
        };
        freeBodyDiagram.addMouseInputListener( listener );
        RepaintDebugGraphic.enable( fbdPanel, module.getClock() );
        fbdPanel.setUseOffscreenBuffer( true );
    }

    public FreeBodyDiagram getFreeBodyDiagram() {
        return freeBodyDiagram;
    }

    public ApparatusPanel3 getFBDPanel() {
        return fbdPanel;
    }

    public void updateGraphics() {
        freeBodyDiagram.updateAll();
        if( fbdPanel.isShowing() ) {
//            fbdPanel.repaint( 0, 0, fbdPanel.getWidth(), fbdPanel.getHeight() );
            fbdPanel.paint();
        }
    }

    public void setVisible( boolean visible ) {
        fbdPanel.setVisible( visible );
    }

    public void reset() {
        if( !freeBodyDiagram.isUserClicked() ) {//TODO maybe this should be smarter.
            fbdWiggleMe.setVisible( true );
            if( !containsGraphic( fbdWiggleMe ) ) {
                fbdPanel.addGraphic( fbdWiggleMe );
            }
        }
    }

    private boolean containsGraphic( PhetGraphic graphic ) {

        PhetGraphic[] g = fbdPanel.getGraphic().getGraphics();
        for( int i = 0; i < g.length; i++ ) {
            PhetGraphic phetGraphic = g[i];
            if( phetGraphic == graphic ) {
                return true;
            }
        }
        return false;

    }
}
