/*  */
package edu.colorado.phet.forces1d.view;

import java.awt.*;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.forces1d.common_force1d.math.Vector2D;
import edu.colorado.phet.forces1d.common_force1d.view.ApparatusPanel2;
import edu.colorado.phet.forces1d.common_force1d.view.BasicGraphicsSetup;
import edu.colorado.phet.forces1d.common_force1d.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.forces1d.Force1DResources;
import edu.colorado.phet.forces1d.Forces1DModule;
import edu.colorado.phet.forces1d.common.WiggleMe;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDevice;
import edu.colorado.phet.forces1d.model.Force1DModel;

/**
 * User: Sam Reid
 * Date: Jan 16, 2005
 * Time: 8:11:43 PM
 */

public class FreeBodyDiagramPanel {

    private FreeBodyDiagram freeBodyDiagram;
    private ApparatusPanel2 fbdPanel;
    private WiggleMe fbdWiggleMe;
    private PlotDevice forcePlotDevice;
    private Forces1DModule module;

    public FreeBodyDiagramPanel( final Forces1DModule module ) {
        this.module = module;
        fbdPanel = new ApparatusPanel2( module.getClock() ) {
            protected void paintComponent( Graphics graphics ) {
                super.paintComponent( graphics );
            }
        };
        fbdPanel.setLayout( new BoxLayout( fbdPanel, BoxLayout.Y_AXIS ) );
        fbdPanel.addGraphicsSetup( new BasicGraphicsSetup() );
        int fbdWidth = 180;
        if ( Toolkit.getDefaultToolkit().getScreenSize().width < 1280 ) {
            fbdWidth = 157;
        }
        fbdPanel.setPreferredSize( new Dimension( fbdWidth, fbdWidth ) );
        freeBodyDiagram = new FreeBodyDiagram( fbdPanel, module );
        freeBodyDiagram.setComponent( fbdPanel );//todo is this necessary?
        fbdPanel.addGraphic( freeBodyDiagram );

        int fbdInset = 3;
        freeBodyDiagram.setBounds( fbdInset, fbdInset, fbdWidth - 2 * fbdInset, fbdWidth - 2 * fbdInset );

        WiggleMe.Target target = new WiggleMe.Target() {
            public Point getLocation() {
                return new Point( fbdPanel.getWidth() - 10, fbdPanel.getHeight() / 2 - fbdWiggleMe.getHeight() );
            }

            public int getHeight() {
                return 0;
            }
        };
        fbdWiggleMe = new WiggleMe( fbdPanel, module.getClock(), Force1DResources.get( "FreeBodyDiagramPanel.clickHelp" ), target );
        fbdWiggleMe.setArrowColor( new Color( 0, 30, 240, 128 ) );
        fbdWiggleMe.setFont( new Font( PhetFont.getDefaultFontName(), Font.BOLD, 14 ) );
        fbdWiggleMe.setArrow( 0, 40 );
        fbdWiggleMe.setAmplitude( 10 );
        fbdWiggleMe.setFrequency( 5.0 );
        fbdWiggleMe.setOscillationAxis( new Vector2D.Double( 1, 0 ) );
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
//        RepaintDebugGraphic.enable( fbdPanel, module.getClock() );//TODO optimize.

        fbdPanel.setUseOffscreenBuffer( true );
    }

    public FreeBodyDiagram getFreeBodyDiagram() {
        return freeBodyDiagram;
    }

    public ApparatusPanel2 getFBDPanel() {
        return fbdPanel;
    }

    public void updateGraphics() {
        freeBodyDiagram.updateAll();
        if ( fbdPanel.isShowing() ) {
            fbdPanel.paint();
        }
    }

    public void setVisible( boolean visible ) {
        fbdPanel.setVisible( visible );
    }

    public void reset() {
        if ( !freeBodyDiagram.isUserClicked() ) {//TODO maybe this should be smarter.
            fbdWiggleMe.setVisible( true );
            if ( !containsGraphic( fbdWiggleMe ) ) {
                fbdPanel.addGraphic( fbdWiggleMe );
            }
        }
    }

    private boolean containsGraphic( PhetGraphic graphic ) {

        PhetGraphic[] g = fbdPanel.getGraphic().getGraphics();
        for ( int i = 0; i < g.length; i++ ) {
            PhetGraphic phetGraphic = g[i];
            if ( phetGraphic == graphic ) {
                return true;
            }
        }
        return false;

    }
}
