/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.quantum.model.Beam;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.EventListener;
import java.util.EventObject;


/**
 * Class: LampGraphic
 * Package: edu.colorado.phet.lasers.view
 * Original Author: Ron LeMaster
 * Creation Date: Nov 13, 2004
 * Creation Time: 1:55:28 PM
 * Latest Change:
 * $Author$
 * $Date$
 * $Name$
 * $Revision$
 */
public class LampGraphic extends CompositePhetGraphic implements Beam.WavelengthChangeListener {
    private Beam beam;
    private double currWavelength;
    private BasicStroke bezelStroke = new BasicStroke( 2f );
    private Ellipse2D lens;
    private PhetShapeGraphic lensGraphic;


    public LampGraphic( Beam beam, Component component, BufferedImage image, AffineTransform transform ) {
        super( component );

        setTransform( transform );
        this.beam = beam;
        beam.addWavelengthChangeListener( this );

        PhetImageGraphic lampImageGraphic = new PhetImageGraphic( component, image );
        addGraphic( lampImageGraphic );
        double lensHeight = lampImageGraphic.getImage().getHeight();
        double lensWidth = lampImageGraphic.getImage().getHeight() / 8;
        lens = new Ellipse2D.Double( lampImageGraphic.getImage().getWidth() - lensWidth / 2 - 3, 0,
//        lens = new Ellipse2D.Double( lampImageGraphic.getImage().getWidth() - 10, 0,
                                     lensWidth, lensHeight );
//                                     10, lampImageGraphic.getImage().getHeight() );
        lensGraphic = new PhetShapeGraphic( component, lens, bezelStroke, Color.black );
        addGraphic( lensGraphic );

        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        update();
    }

    public void wavelengthChanged( Beam.WavelengthChangeEvent event ) {
        update();
    }

    private void update() {
        if( currWavelength != beam.getWavelength() ) {
            currWavelength = beam.getWavelength();
            Color color = VisibleColor.wavelengthToColor( currWavelength );
            color = getDuotone( color );
            lensGraphic.setColor( color );
            repaint();
        }
    }

    public static Color getDuotone( Color baseColor ) {
        // Need to figure out how to shade the color. Take a look at MakeDuotoneOp.
        double grayRefLevel = MakeDuotoneImageOp.getGrayLevel( baseColor );
        int newRGB = MakeDuotoneImageOp.getDuoToneRGB( baseColor.getRed(),
                                                       baseColor.getGreen(),
                                                       baseColor.getBlue(),
                                                       baseColor.getAlpha(),
                                                       grayRefLevel,
                                                       baseColor );
        return new Color( newRGB );
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        changeListenerProxy.changed( new ChangeEvent( this ) );
    }

    //----------------------------------------------------------------
    // Events and event handling
    //----------------------------------------------------------------

    public interface ChangeListener extends EventListener {
        public void changed( ChangeEvent event );
    }

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Object source ) {
            super( source );
        }

        public LampGraphic getLampGraphic() {
            return (LampGraphic)getSource();
        }
    }

    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }
}
