package edu.colorado.phet.common.piccolophet.nodes.mediabuttons;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Created by: Sam
 * Sep 12, 2008 at 12:23:11 AM
 */
public class MediaPlaybackBarNode extends PNode {
    private double width;
    private double height;
    private PPath outerShape;
    private double progress = 0;
    private PhetPPath fillPath;
    private PhetPPath braidPath;

    public MediaPlaybackBarNode( double width, double height ) {
        this.width = width;
        this.height = height;
//        outerShape = new PhetPPath( Color.lightGray, new BasicStroke( 1 ), Color.black );
        outerShape = new PhetPPath( Color.lightGray );
        fillPath = new PhetPPath( Color.gray );
        braidPath = new PhetPPath( new BasicStroke( 2 ), Color.red );

        addChild( outerShape );
        addChild( fillPath );

//        addChild( braidPath );
        update();
    }

    public void setSize( double width, double height ) {
        this.width = width;
        this.height = height;
        update();
    }

    private void update() {
        outerShape.setPathTo( new Rectangle2D.Double( 0, 0, width, height ) );


        int progressWindingNumber = (int) progress;
        double relativeProgress = progress - progressWindingNumber;
        if ( progressWindingNumber % 2 == 0 ) {
            double max = width * relativeProgress;
            max = MathUtil.clamp( 0, max, width );
            fillPath.setPathTo( new Rectangle2D.Double( 0, 0, max, height ) );
        }
        else {
            double max = width * relativeProgress;
            max = MathUtil.clamp( 0, max, width );
            double w = MathUtil.clamp( 0, width, width - max );
            fillPath.setPathTo( new Rectangle2D.Double( max, 0, w, height ) );
        }
//        braidPath.setPathTo( createSineCurve() );
    }

    private Shape createSineCurve() {
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( 0, 0 );
        double dx = width / 1000;
        for ( double x = 0; x <= width * progress; x += dx ) {
            path.lineTo( x, 1 * Math.sin( x * 0.1 - width * progress * 0.05 ) );
        }
        return path.getGeneralPath();
    }

    public static void main( String[] args ) {
        PiccoloTestFrame testFrame = new PiccoloTestFrame( "Button Test" );
        final PlayPauseButton node = new PlayPauseButton( 75 );

        node.setOffset( 100, 200 );
        testFrame.addNode( node );

        final MediaPlaybackBarNode playbackBarNode = new MediaPlaybackBarNode( 400, 3 );

        playbackBarNode.setOffset( 20, 20 );
        testFrame.addNode( playbackBarNode );
        testFrame.setVisible( true );

        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( node.isPlaying() ) {
                    playbackBarNode.setProgress( playbackBarNode.getProgress() + 0.006 );
                }
            }
        } );
        timer.start();
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress( double progress ) {
        this.progress = progress;
        update();
    }
}
