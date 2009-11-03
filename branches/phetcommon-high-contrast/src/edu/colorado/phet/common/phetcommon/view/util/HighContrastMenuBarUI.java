package edu.colorado.phet.common.phetcommon.view.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.plaf.MenuBarUI;
import javax.swing.plaf.metal.MetalMenuBarUI;
import javax.swing.*;
import javax.imageio.ImageIO;

import edu.colorado.phet.common.phetcommon.view.HighContrastOperation;

public class HighContrastMenuBarUI extends MetalMenuBarUI {
    @Override
    public void paint( Graphics g, JComponent c ) {
        System.out.println( "p" );
        BufferedImage buffer = new BufferedImage( c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB );
        super.paint( buffer.createGraphics(), c );
        try {
            ImageIO.write( buffer, "png", new File( "C:\\Documents and Settings\\Jonathan Olson\\My Documents\\phet\\tmp\\out.png" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        BufferedImage result = ( new HighContrastOperation() ).filter( buffer, null );
        g.drawImage( result, 0, 0, null );

    }

    @Override
    public void update( Graphics g, JComponent c ) {
        System.out.println( "u" );
        //super.update( g, c );
        g.setColor( Color.RED );
        g.drawLine( 0, 0, 500, 500 );
    }
}
