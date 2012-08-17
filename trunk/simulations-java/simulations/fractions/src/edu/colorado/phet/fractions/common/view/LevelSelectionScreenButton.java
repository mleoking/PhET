// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.common.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;
import edu.umd.cs.piccolo.PNode;

/**
 * Button that allows the user to navigate back to the level selection screen.
 *
 * @author Sam Reid
 */
public class LevelSelectionScreenButton extends PNode {
    public LevelSelectionScreenButton( final VoidFunction0 pressed, BufferedImage image ) {
        addChild( new HTMLImageButtonNode( RefreshButtonNode.copyWithPadding( image, 1, 1 ) ) {{
            setBackground( RefreshButtonNode.BUTTON_COLOR );
            setUserComponent( Components.levelSelectionScreenButton );
            addActionListener( new ActionListener() {
                @Override public void actionPerformed( final ActionEvent e ) {
                    pressed.apply();
                }
            } );
        }} );
    }

    private PNode path() {
        return new PNode() {{
            //Add a faint shadow to make them look more like buttons
            addChild( new PhetPPath( new Rectangle2D.Double( 1.75, 1.75, 6, 10 ), Color.lightGray ) );
            addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, 6, 10 ), Color.darkGray ) );
        }};
    }
}