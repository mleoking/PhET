/*  */
package edu.colorado.phet.waveinterference.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
 * Adds controls to an intensity reader detector.
 * 
 * @author Sam Reid
 */
public class IntensityReaderDecorator extends PhetPNode {
    
    private final ArrayList listeners = new ArrayList();
    private final IntensityReader intensityReader;
    private final PSwing buttonPSwing;

    public IntensityReaderDecorator( String title, final PSwingCanvas pSwingCanvas, WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates, IClock clock ) {
        
        // detector
        this.intensityReader = new IntensityReader( title, waveModel, latticeScreenCoordinates, clock );
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
        
        // close button
        JButton close = null;
        try {
            BufferedImage image = ImageLoader.loadBufferedImage( "wave-interference/images/x-20.png" );
            image = BufferedImageUtils.rescaleYMaintainAspectRatio( image, (int) ( image.getHeight() * 0.6 ) );
            close = new JButton( new ImageIcon( image ) );
            close.setOpaque( false );
            close.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    delete();
                }
            } );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        buttonPSwing = new PSwing( close );
        
        // rendering order
        addChild( intensityReader );
        addChild( buttonPSwing );
    }

    public void delete() {
        notifyDeleted();
    }

    private void notifyDeleted() {
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
