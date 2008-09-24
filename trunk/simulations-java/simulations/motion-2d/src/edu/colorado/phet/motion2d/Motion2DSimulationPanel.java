package edu.colorado.phet.motion2d;

import java.awt.*;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;

public class Motion2DSimulationPanel extends JPanel {

    private Motion2DPanel motion2DPanel;
    private Image ballImage, emptyImage;
    Cursor hide, show;        //Invisible and visible mouse cursors
    private ConstantDtClock clock;

    public Motion2DSimulationPanel( ConstantDtClock clock ) {
        this.clock = clock;
    }

    public void init() throws IOException {
        ballImage = ImageLoader.loadBufferedImage( "motion-2d/images/ballsmall2.gif" );
        emptyImage = ImageLoader.loadBufferedImage( "motion-2d/images/empty.gif" );

        this.hide = Toolkit.getDefaultToolkit().createCustomCursor( emptyImage, new Point(), "Null" );
        this.show = new Cursor( Cursor.DEFAULT_CURSOR );
        setCursor( show );

        setLayout( new BorderLayout() );
        motion2DPanel = new Motion2DPanel( this );
        motion2DPanel.setBackground( Color.yellow );
        add( motion2DPanel, BorderLayout.CENTER );
    }

    public Image getBallImage() {
        return ballImage;
    }

    public ConstantDtClock getClock() {
        return clock;
    }
}