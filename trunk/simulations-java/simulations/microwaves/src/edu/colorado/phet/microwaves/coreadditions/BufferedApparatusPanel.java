/**
 * Class: BufferedApparatusPanel
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Nov 21, 2003
 */
package edu.colorado.phet.microwaves.coreadditions;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import edu.colorado.phet.microwaves.common.graphics.AffineTransformFactory;
import edu.colorado.phet.microwaves.common.graphics.ApparatusPanel;

public class BufferedApparatusPanel extends ApparatusPanel {
    private BufferedImage bImg;
    Rectangle bounds = new Rectangle();
    ImageObserver imgObs = new ImageObserver() {
        public boolean imageUpdate( Image img, int infoflags,
                                    int x, int y, int width, int height ) {
            return false;
        }
    };

    public BufferedApparatusPanel( final AffineTransformFactory tx ) {
        super( tx );
        this.bImg = bImg;
        this.bounds = bounds;
        this.imgObs = imgObs;
    }

    protected void paintComponent( Graphics graphics ) {

        if ( bounds.getMinX() != super.getBounds().getMinX()
             || bounds.getMinY() != super.getBounds().getMinY()
             || bounds.getMaxX() != super.getBounds().getMaxX()
             || bounds.getMaxY() != super.getBounds().getMaxY() ) {
            bImg = new BufferedImage( (int) getBounds().getWidth(),
                                      (int) getBounds().getHeight(),
                                      BufferedImage.TYPE_INT_ARGB );

            bounds.setBounds( super.getBounds() );
        }
        Graphics2D g2 = (Graphics2D) bImg.getGraphics();
        super.paintComponent( g2 );
        ( (Graphics2D) graphics ).drawImage( bImg, 0, 0, imgObs );
    }
}
