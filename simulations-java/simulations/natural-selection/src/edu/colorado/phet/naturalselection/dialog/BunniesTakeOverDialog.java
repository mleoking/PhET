package edu.colorado.phet.naturalselection.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.module.NaturalSelectionModule;
import edu.umd.cs.piccolo.PNode;

public class BunniesTakeOverDialog extends JDialog {
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
        canvas.setMinimumSize( new Dimension( 480, 481 ) );
        canvas.setPreferredSize( new Dimension( 480, 481 ) );
        canvas.setMaximumSize( new Dimension( 480, 481 ) );
        final PNode root = new PNode();
        canvas.addWorldChild( root );
        //root.addChild( NaturalSelectionResources.getImageNode( NaturalSelectionConstants.IMAGE_EARTH_PLAIN ) );
        root.addChild( NaturalSelectionResources.getImageNode( NaturalSelectionConstants.IMAGE_EARTH ) );
        panel.add( canvas );

        panel.setBackground( Color.BLACK );

        panel.setInsets( new Insets( 10, 10, 10, 10 ) );

        JButton playAgainButton = new JButton( NaturalSelectionStrings.GAME_OVER_PLAY_AGAIN );
        panel.add( playAgainButton );

        playAgainButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                module.reset();
                BunniesTakeOverDialog.this.dispose();
            }
        } );

        setContentPane( panel );

        pack();

        /*
        ( new Thread() {
            @Override
            public void run() {
                for ( int i = 1; i < 15; i++ ) {
                    try {
                        Thread.sleep( 100 );
                        root.addChild( NaturalSelectionResources.getImageNode( "earth-v1-bunnies-" + String.valueOf( i ) + ".png" ) );
                        panel.repaint();
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }
        } ).start();
        */
    }
}
