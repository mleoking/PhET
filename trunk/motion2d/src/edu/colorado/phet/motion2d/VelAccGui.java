package edu.colorado.phet.motion2d;

//edu.colorado.phet.motion2d.VelAccGui.class 06/02/02 M.Dubson

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class VelAccGui extends JApplet {

    private Container myPane;
    private MyJPanel myJPanel;
    Image ballImage, emptyImage;
    Cursor hide, show;		//Invisible and visible mouse cursors

    public void init() {
        try {
            ballImage = ImageIO.read( getClass().getClassLoader().getResource( "ballsmall2.gif" ) );
            emptyImage = ImageIO.read( getClass().getClassLoader().getResource( "empty.gif" ) );

//            ballImage = ImageLoader.loadBufferedImage( "ballsmall2.gif" );//getImage(getDocumentBase(), "ballsmall2.gif");
//            emptyImage = ImageLoader.loadBufferedImage( "empty.gif" );//getImage(getDocumentBase(), "empty.gif");
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        this.hide = Toolkit.getDefaultToolkit().createCustomCursor( emptyImage, new Point(), "Null" );
        this.show = new Cursor( Cursor.DEFAULT_CURSOR );
        setCursor( show );

        myPane = getContentPane();
        myPane.setLayout( new BorderLayout() );
        myJPanel = new MyJPanel( this );
        myJPanel.setBackground( Color.yellow );
        myPane.add( myJPanel, BorderLayout.CENTER );
    }

}