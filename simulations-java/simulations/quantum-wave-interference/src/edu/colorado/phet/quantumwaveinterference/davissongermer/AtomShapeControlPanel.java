/*  */
package edu.colorado.phet.quantumwaveinterference.davissongermer;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.quantumwaveinterference.QWIResources;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Feb 18, 2006
 * Time: 1:23:37 PM
 */
public class AtomShapeControlPanel extends VerticalLayoutPanel {
    public AtomShapeControlPanel( final DGModule dgModule ) {
        setBorder( BorderFactory.createTitledBorder( QWIResources.getString( "atom.shape" ) ) );
        JRadioButton circular = new JRadioButton( QWIResources.getString( "circular" ), dgModule.isAtomShapeCircular() );
        JRadioButton squares = new JRadioButton( QWIResources.getString( "square" ), dgModule.isAtomShapeSquare() );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( circular );
        buttonGroup.add( squares );
        circular.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dgModule.setAtomShapeCircular();
            }
        } );
        squares.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dgModule.setAtomShapeSquare();
            }
        } );
        if( circular.isSelected() ) {
            add( circular );
            add( squares );
        }
        else {
            add( squares );
            add( circular );
        }
    }
}
