package edu.colorado.phet.rotation;

import edu.colorado.phet.common.piccolophet.nodes.RulerNode;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Author: Sam Reid
 * Jul 11, 2007, 3:49:48 PM
 * <p/>
 * //todo: coalesce with Optical Tweezers
 */
public class RulerButton extends Box {
    private JCheckBox checkBox;
    private RulerNode rulerNode;

    public RulerButton( RulerNode rulerNode ) {
        super( BoxLayout.X_AXIS );
        this.rulerNode = rulerNode;
        checkBox = new JCheckBox( "Show Ruler" );
//        checkBox.setFont( new PhetDefaultFont( ));
        checkBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleRulerCheckBox();
            }
        } );
        Icon rulerIcon = null;
        try {
            rulerIcon = new ImageIcon( RotationResources.loadBufferedImage( "icons/rulerIcon.png" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        JLabel rulerLabel = new JLabel( rulerIcon );

        add( checkBox );
        add( Box.createHorizontalStrut( 5 ) );
        add( rulerLabel );
    }

    private void handleRulerCheckBox() {
        rulerNode.setVisible( checkBox.isSelected() );
    }
}
