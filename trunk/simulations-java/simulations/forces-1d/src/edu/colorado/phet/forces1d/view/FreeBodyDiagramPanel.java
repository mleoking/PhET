package edu.colorado.phet.forces1d.view;

import java.awt.*;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.forces1d.Force1DResources;
import edu.colorado.phet.forces1d.Forces1DModule;
import edu.colorado.phet.forces1d.common.WiggleMe;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDevice;
import edu.colorado.phet.forces1d.model.Force1DModel;
import edu.colorado.phet.forces1d.phetcommon.math.Vector2D;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.PhetGraphic;

/**
 * User: Sam Reid
 * Date: Jan 16, 2005
 * Time: 8:11:43 PM
 */

public class FreeBodyDiagramPanel extends BufferedPhetPCanvas {

    private FreeBodyDiagramNode freeBodyDiagram;
    private WiggleMe fbdWiggleMe;
    private PlotDevice forcePlotDevice;

    public FreeBodyDiagramPanel( final Forces1DModule module ) {
        setLayout( null );
        int fbdWidth = 180;
        if ( Toolkit.getDefaultToolkit().getScreenSize().width < 1280 ) {
            fbdWidth = 157;
        }
        setPreferredSize( new Dimension( fbdWidth, fbdWidth ) );
        freeBodyDiagram = new FreeBodyDiagramNode( module );
        addScreenChild( freeBodyDiagram );

        int fbdInset = 3;
        freeBodyDiagram.setBounds( fbdInset, fbdInset, fbdWidth - 2 * fbdInset, fbdWidth - 2 * fbdInset );

        WiggleMe.Target target = new WiggleMe.Target() {
            public Point getLocation() {
                return new Point( getWidth() - 10, getHeight() / 2 - fbdWiggleMe.getHeight() );
            }

            public int getHeight() {
                return 0;
            }
        };
        fbdWiggleMe = new WiggleMe( this, module.getClock(), Force1DResources.get( "FreeBodyDiagramPanel.clickHelp" ), target );
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
                //todo:
//                removeScreenChild( fbdWiggleMe );
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
        addMouseListener( listener );

        MouseInputAdapter mia = new MouseInputAdapter() {
            public void mousePressed( MouseEvent e ) {
                freeBodyDiagram.setForce( e.getPoint() );
                freeBodyDiagram.setUserClicked( true );
            }

            public void mouseDragged( MouseEvent e ) {
                freeBodyDiagram.setForce( e.getPoint() );
            }

            public void mouseReleased( MouseEvent e ) {
                freeBodyDiagram.setAppliedForce( 0.0 );
            }
        };

        final MouseInputListener listener2 = new ThresholdedDragAdapter( mia, 10, 0, 1000 );
        addMouseListener( listener2 );
        addMouseMotionListener( listener2 );
    }

    public void updateGraphics() {
        freeBodyDiagram.updateAll();
//        if ( isShowing() ) {
//            paint();
//        }
        //???
    }

    public void reset() {
        if ( !freeBodyDiagram.isUserClicked() ) {//TODO maybe this should be smarter.
            fbdWiggleMe.setVisible( true );
            if ( !containsGraphic( fbdWiggleMe ) ) {
                //todo?
//                addGraphic( fbdWiggleMe );
            }
        }
    }

    private boolean containsGraphic( PhetGraphic graphic ) {
        return false;
//        PhetGraphic[] g = getGraphic().getGraphics();
//        for ( int i = 0; i < g.length; i++ ) {
//            PhetGraphic phetGraphic = g[i];
//            if ( phetGraphic == graphic ) {
//                return true;
//            }
//        }
//        return false;

    }

    public void handleUserInput() {
    }
}
