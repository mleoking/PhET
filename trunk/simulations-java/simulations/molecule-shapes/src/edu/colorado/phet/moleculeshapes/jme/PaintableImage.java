// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import com.jme3.texture.Image;

public abstract class PaintableImage extends Image {
    private BufferedImage backImg;
    private ByteBuffer scratch;

    public PaintableImage( int width, int height, boolean hasAlpha ) {
        // TODO: fix alpha support. broken currently
        super( hasAlpha ? Format.RGBA8 : Format.BGR8, width, height, ByteBuffer.allocateDirect( 4 * width * height ) );
        scratch = data.get( 0 );
        backImg = new BufferedImage( width, height, hasAlpha ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR );

    }

    public void refreshImage() {
        Graphics2D g = backImg.createGraphics();
        paint( g );
        g.dispose();

        /* get the image data */
        byte data[] = (byte[]) backImg.getRaster().getDataElements( 0, 0, backImg.getWidth(), backImg.getHeight(), null );
        scratch.clear();
        scratch.put( data, 0, data.length );
        scratch.rewind();
        setData( scratch );
    }

    public abstract void paint( Graphics2D graphicsContext );

}
