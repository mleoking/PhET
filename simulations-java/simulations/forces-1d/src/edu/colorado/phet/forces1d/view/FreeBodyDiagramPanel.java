package edu.colorado.phet.forces1d.view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
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
import edu.colorado.phet.forces1d.phetcommon.model.clock.SwingTimerClock;
import edu.colorado.phet.forces1d.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.PhetGraphic;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Jan 16, 2005
 * Time: 8:11:43 PM
 */

public class FreeBodyDiagramPanel extends BufferedPhetPCanvas {

    private FreeBodyDiagramNode freeBodyDiagram;
    private WiggleMe fbdWiggleMe;
    private PlotDevice forcePlotDevice;
    private ArrayList ignoreAreas = new ArrayList();

    public FreeBodyDiagramPanel( final Forces1DModule module ) {
        setLayout( null );
        int fbdWidth = 180;
        if ( Toolkit.getDefaultToolkit().getScreenSize().width < 1280 ) {
            fbdWidth = 157;
        }
        setPreferredSize( new Dimension( fbdWidth, fbdWidth ) );
        freeBodyDiagram = new FreeBodyDiagramNode( module );
        addScreenChild( freeBodyDiagram );

        freeBodyDiagram.setSize( fbdWidth, fbdWidth );
//        freeBodyDiagram.setBounds( fbdInset, fbdInset, fbdWidth - 2 * fbdInset, fbdWidth - 2 * fbdInset );

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
                if ( !ignore( e ) ) {
                    forcePlotDevice.getPlotDeviceModel().setRecordMode();
                    forcePlotDevice.getPlotDeviceModel().setPaused( false );
                }
            }
        };
        addMouseListener( listener );

        MouseInputAdapter mia = new MouseInputAdapter() {
            public void mousePressed( MouseEvent e ) {
                if ( !ignore( e ) ) {
                    freeBodyDiagram.setForce( e.getPoint() );
                    freeBodyDiagram.setUserClicked( true );
                }
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

    //This workaround allows us to ignore mouse events over the PSwing; a better solution would be
    //to move this mouse event code to the PNode instead
    private boolean ignore( MouseEvent e ) {
        for ( int i = 0; i < ignoreAreas.size(); i++ ) {
            PNode pNode = (PNode) ignoreAreas.get( i );
            if (pNode.getGlobalFullBounds().contains( e.getPoint() )){
                return true;
            }
        }
        return false;
    }

    public void updateGraphics() {
        freeBodyDiagram.updateAll();
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

    public static void main( String[] args ) throws IOException {
        FreeBodyDiagramPanel a = new FreeBodyDiagramPanel( new Forces1DModule( new SwingTimerClock( 1, 30 ), new PhetLookAndFeel() ) );
        JFrame frame = new JFrame();
        frame.setContentPane( a );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 400, 400 );
        frame.setVisible( true );
    }

    public void addIgnoreArea( PNode ignoreArea ) {
        ignoreAreas.add( ignoreArea );
    }
}
