/*  */
package edu.colorado.phet.waveinterference.view;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * User: Sam Reid
 * Date: Mar 23, 2006
 * Time: 9:58:29 PM
 *
 */

/**
 * Decorates with buttons and controls.
 */
public class IntensityReaderDecorator extends PhetPNode {
    
    private ArrayList listeners = new ArrayList();
    private IntensityReader intensityReader;
    private PSwing buttonPSwing;

    public IntensityReaderDecorator( String title, final PSwingCanvas pSwingCanvas, WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates, IClock clock ) {
        this.intensityReader = new IntensityReader( title, waveModel, latticeScreenCoordinates, clock );
        JButton close = null;
        try {
            BufferedImage image = ImageLoader.loadBufferedImage( "wave-interference/images/x-20.png" );
            image = BufferedImageUtils.rescaleYMaintainAspectRatio( image, (int) ( image.getHeight() * 0.6 ) );
            close = new JButton( new ImageIcon( image ) );
            close.setOpaque( false );
            close.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    doDelete();
                }
            } );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        
        buttonPSwing = new PSwing( close );
        addChild( intensityReader );
        addChild( buttonPSwing );
        intensityReader.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateLocation();
            }
        } );
        intensityReader.getStripChartJFCNode().addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateLocation();
            }
        } );
    }

    public void delete() {
        doDelete();
    }

    private void doDelete() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.deleted();
        }
    }

    public void setConstrainedToMidline( boolean midline ) {
        intensityReader.setConstrainedToMidline( midline );
    }

    public static interface Listener {
        void deleted();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    private void updateLocation() {
        Rectangle2D bounds = intensityReader.getStripChartJFCNode().getFullBounds();
        intensityReader.localToParent( bounds );
        buttonPSwing.setOffset( bounds.getX() + 2, bounds.getMaxY() - buttonPSwing.getFullBounds().getHeight() - 2 );
    }

    public void update() {
        intensityReader.update();
    }
}
