/**
 * Class: LampGraphic
 * Package: edu.colorado.phet.lasers.view
 * Original Author: Ron LeMaster
 * Creation Date: Nov 13, 2004
 * Creation Time: 1:55:28 PM
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

/**
 *
 */
public class LampGraphic extends PhetImageGraphic implements CollimatedBeam.WavelengthChangeListener {
    private CollimatedBeam beam;
    private double currWavelength;
    private Color color;
    private BasicStroke bezelStroke = new BasicStroke( 2f );
    private Ellipse2D lens = new Ellipse2D.Double( getImage().getWidth() - 10, 0,
                                                   10, getImage().getHeight() );


    public LampGraphic( CollimatedBeam beam, Component component, BufferedImage image, AffineTransform transform ) {
        super( component, image );
        setTransform( transform );
        this.beam = beam;
        beam.addWavelengthChangeListener( this );

        update();
    }

    public void wavelengthChanged( CollimatedBeam.WavelengthChangeEvent event ) {
        update();
    }

    private void update() {
        if( currWavelength != beam.getWavelength() ) {
            currWavelength = beam.getWavelength();
            color = VisibleColor.wavelengthToColor( currWavelength );
            color = getDuotone( color );
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

    public void paint( Graphics2D g ) {
        saveGraphicsState( g );
        super.paint( g );
        if( isVisible() ) {
            GraphicsUtil.setAntiAliasingOn( g );
            g.transform( getTransform() );
            g.setColor( color );
            g.fill( lens );
            g.setStroke( bezelStroke );
            g.setColor( Color.black );
            g.draw( lens );
        }
        restoreGraphicsState();
    }
}
