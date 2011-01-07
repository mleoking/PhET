// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.NaturalSelectionApplication;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.module.NaturalSelectionModule;
import edu.umd.cs.piccolo.PNode;

/**
 * This dialog is shown if more than the maximum number of bunnies goes over a certain threshold. It shows an image
 * of the world covered with bunnies, and says something like "Bunnies have taken over the world"
 *
 * @author Jonathan Olson
 */
public class BunniesTakeOverDialog extends PaintImmediateDialog {
    private static final Dimension CANVAS_DIMENSION = new Dimension( 480, 481 );

    public BunniesTakeOverDialog( Frame frame, final NaturalSelectionModule module ) {
        super( frame );

        setTitle( NaturalSelectionStrings.GAME_OVER_BUNNIES_TAKEN_OVER );

        final VerticalLayoutPanel panel = new VerticalLayoutPanel();

        panel.setInsets( new Insets( 10, 10, 0, 10 ) );

        JLabel label = new JLabel( NaturalSelectionStrings.GAME_OVER_BUNNIES_TAKEN_OVER );
        label.setForeground( Color.WHITE );
        label.setFont( new PhetFont( 20 ) );
        label.setHorizontalAlignment( SwingConstants.CENTER );
        panel.add( label );

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setWorldTransformStrategy( new PhetPCanvas.ConstantTransformStrategy( new AffineTransform() ) );
        canvas.setMinimumSize( CANVAS_DIMENSION );
        canvas.setPreferredSize( CANVAS_DIMENSION );
        canvas.setMaximumSize( CANVAS_DIMENSION );
        final PNode root = new PNode();
        canvas.addWorldChild( root );
        root.addChild( NaturalSelectionResources.getImageNode( NaturalSelectionConstants.IMAGE_EARTH ) );
        panel.add( canvas );

        panel.setBackground( NaturalSelectionApplication.accessibleColor( Color.BLACK ) );

        panel.setInsets( new Insets( 10, 10, 10, 10 ) );

        JButton playAgainButton = new JButton( NaturalSelectionStrings.GAME_OVER_PLAY_AGAIN );
        JPanel container = new JPanel();
        container.setBackground( NaturalSelectionApplication.accessibleColor( Color.BLACK ) );
        container.add( playAgainButton );
        panel.add( container );

        playAgainButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                module.reset();
                BunniesTakeOverDialog.this.dispose();
            }
        } );

        setContentPane( panel );

        pack();

    }
}
