package edu.colorado.phet.common.phetcommon.view.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;

import javax.swing.plaf.MenuBarUI;
import javax.swing.plaf.MenuItemUI;
import javax.swing.plaf.metal.MetalButtonUI;
import javax.swing.*;
import javax.imageio.ImageIO;

import edu.colorado.phet.common.phetcommon.view.HighContrastOperation;

public class HighContrastMenuItemUI extends MetalButtonUI {
    private BufferedImage buffer;
    private ColorModel colorModel = ColorModel.getRGBdefault();

    @Override
    public void paint( Graphics g, JComponent c ) {
        System.out.println( "paint" );
        super.paint( buffer.createGraphics(), c );
    }

    @Override
    public void update( Graphics g, JComponent c ) {
        System.out.println( "update" );
        buffer = new BufferedImage( colorModel, colorModel.createCompatibleWritableRaster( c.getWidth(), c.getHeight() ), false, null );
        super.update( g, c );
        try {
            ImageIO.write( buffer, "png", new File( "C:\\Documents and Settings\\Jonathan Olson\\My Documents\\phet\\tmp\\out.png" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        BufferedImage result = ( new HighContrastOperation() ).filter( buffer, null );
        g.drawImage( result, 0, 0, c );
        g.setColor( Color.RED );
        g.drawLine( 0, 0, 500, 500 );
    }
}