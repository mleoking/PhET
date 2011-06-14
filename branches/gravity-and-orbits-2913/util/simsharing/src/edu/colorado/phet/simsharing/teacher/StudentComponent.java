// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.simsharing.SessionID;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class StudentComponent extends PNode {
    public final SessionID sessionID;
    private final PImage thumbnail;
    private PText text;
    private PText text2;

    public StudentComponent( final SessionID sessionID, final VoidFunction0 watch ) {
        this.sessionID = sessionID;
        final PText namePText = new PText( sessionID.getName() );
        addChild( namePText );
        final HTMLImageButtonNode buttonNode = new HTMLImageButtonNode( "Watch" ) {{
            setOffset( 100, 0 );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    watch.apply();
                }
            } );
        }};
        addChild( buttonNode );
        final int width = 200;
        double aspectRatio = 1024.0 / 768;
        final int imageHeight = (int) ( width / aspectRatio );
        thumbnail = new PImage( new BufferedImage( width, imageHeight, BufferedImage.TYPE_INT_ARGB_PRE ) {{
            Graphics2D g2 = createGraphics();
            g2.setPaint( Color.black );
            g2.fill( new Rectangle( 0, 0, getWidth(), getHeight() ) );
            g2.dispose();
        }} ) {{
            setOffset( buttonNode.getFullBounds().getMaxX() + 10, 0 );
        }};
        addChild( thumbnail );
        text = new PText( "-" );
        text.setOffset( 0, 50 );

        text2 = new PText( "-" );
        text2.setOffset( 0, 100 );
        addChild( text );
        addChild( text2 );
    }

    public void setThumbnail( BufferedImage thumbnail ) {
        this.thumbnail.setImage( thumbnail );
    }

    public void setUpTime( long upTime ) {
        text.setText( "uptime = " + toSeconds( upTime ) + " sec" );
    }

    private String toSeconds( long upTime ) {
        return new DecimalFormat( "0.0" ).format( upTime / 1000.0 );
    }

    public SessionID getSessionID() {
        return sessionID;
    }

    public void setTimeSinceLastEvent( long timeSinceLastEvent ) {
//        text2.setText( "last event " + toSeconds( timeSinceLastEvent ) + " sec ago" );
        text2.setText( "last event " + timeSinceLastEvent + " ms ago" );
    }
}
