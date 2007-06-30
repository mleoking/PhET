package edu.colorado.phet.motion2d;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Locale;

public class Motion2DApplet extends JApplet {

    private Container myPane;
    private Motion2DPanel motion2DPanel;
    private Image ballImage, emptyImage;
    Cursor hide, show;        //Invisible and visible mouse cursors

    public void init() {
        String applicationLocale = Toolkit.getDefaultToolkit().getProperty( "javaws.phet.locale", null );
        if( applicationLocale != null && !applicationLocale.equals( "" ) ) {
            SimStrings.getInstance().setLocale( new Locale( applicationLocale ) );
        }
        SimStrings.getInstance().addStrings( Motion2DApplication.localizedStringsPath );

        try {
            ballImage = ImageLoader.loadBufferedImage( "motion-2d/images/ballsmall2.gif" );
            emptyImage = ImageLoader.loadBufferedImage( "motion-2d/images/empty.gif" );
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

    public Image getBallImage() {
        return ballImage;
    }
}