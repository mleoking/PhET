// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.simsharing.StudentID;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class StudentComponent extends PNode {
    public final StudentID studentID;
    private final PImage thumbnail;

    public StudentComponent( final StudentID studentID, final VoidFunction0 watch ) {
        this.studentID = studentID;
        addChild( new PText( studentID.getName() ) );
        final ButtonNode buttonNode = new ButtonNode( "Watch" ) {{
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
    }

    public void setThumbnail( BufferedImage thumbnail ) {
        this.thumbnail.setImage( thumbnail );
    }

    public StudentID getStudentID() {
        return studentID;
    }
}
