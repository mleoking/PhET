package edu.colorado.phet.motion2d;

//edu.colorado.phet.motion2d.Motion2DApplet.class 06/02/02 M.Dubson

import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Locale;

public class Motion2DApplet extends JApplet {

    private Container myPane;
    private Motion2DPanel motion2DPanel;
    Image ballImage, emptyImage;
    Cursor hide, show;        //Invisible and visible mouse cursors

    public Motion2DPanel getMotion2DPanel() {
        return motion2DPanel;
    }

    public void init() {
        String applicationLocale = Toolkit.getDefaultToolkit().getProperty( "javaws.locale", null );
        if( applicationLocale != null && !applicationLocale.equals( "" ) ) {
            SimStrings.setLocale( new Locale( applicationLocale ) );
        }
        SimStrings.setStrings( Motion2DApplication.localizedStringsPath );

        try {
            ballImage = ImageLoaderSolo.loadBufferedImage( "ballsmall2.gif" );
            emptyImage = ImageLoaderSolo.loadBufferedImage( "empty.gif" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        this.hide = Toolkit.getDefaultToolkit().createCustomCursor( emptyImage, new Point(), "Null" );
        this.show = new Cursor( Cursor.DEFAULT_CURSOR );
        setCursor( show );

        myPane = getContentPane();
        myPane.setLayout( new BorderLayout() );
        motion2DPanel = new Motion2DPanel( this );
        motion2DPanel.setBackground( Color.yellow );
        myPane.add( motion2DPanel, BorderLayout.CENTER );
    }

}