/*  */
package edu.colorado.phet.quantumwaveinterference.controls;

import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.view.QWIPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Dec 11, 2005
 * Time: 9:19:53 PM
 */

public class InverseSlitsCheckbox extends JCheckBox {
    private QWIPanel QWIPanel;

    public InverseSlitsCheckbox( final QWIPanel QWIPanel ) {
        super( QWIResources.getString( "controls.slits.anti-slits" ) );
        this.QWIPanel = QWIPanel;
        setSelected( QWIPanel.isInverseSlits() );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                QWIPanel.setInverseSlits( isSelected() );
            }
        } );
        QWIPanel.addListener( new QWIPanel.Adapter() {
            public void inverseSlitsChanged() {
                setSelected( QWIPanel.isInverseSlits() );
            }
        } );
    }
}
