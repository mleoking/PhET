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

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.coreadditions.ColorFromWavelength;

import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;

public class LampGraphic extends PhetImageGraphic implements CollimatedBeam.WavelengthChangeListener {
    private CollimatedBeam beam;
    private double currWavelength;
    private Color color;
    private BasicStroke bezelStroke = new BasicStroke( 2f );
    private Ellipse2D lens = new Ellipse2D.Double( getImage().getWidth() - 10, 0,
                                                   10, getImage().getHeight() );

    public LampGraphic( CollimatedBeam beam, Component component, BufferedImage image, AffineTransform transform ) {
        super( component, image, transform );
        this.beam = beam;
        beam.addListener2( this );
        update();
    }

    public void wavelengthChangeOccurred( CollimatedBeam.WavelengthChangeEvent event ) {
        update();
    }

    private void update() {
        if( currWavelength != beam.getWavelength() ) {
            currWavelength = beam.getWavelength();
            color = VisibleColor.wavelengthToColor( currWavelength );
            repaint();
        }
    }

    public void paint( Graphics2D g ) {
        saveGraphicsState( g );
        super.paint( g );
        GraphicsUtil.setAntiAliasingOn( g );
        g.transform( getTransform() );
//        if( currWavelength != beam.getWavelength() ) {
//            currWavelength = beam.getWavelength();
//            color = VisibleColor.wavelengthToColor( currWavelength );
//        }
        g.setColor( color );
        g.fill( lens );
        g.setStroke( bezelStroke );
        g.setColor( Color.black );
        g.draw( lens );
        restoreGraphicsState();
    }
}
