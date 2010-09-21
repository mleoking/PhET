package edu.colorado.phet.forces1d;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Created by: Sam
 * Aug 23, 2008 at 9:09:02 PM
 */
public class ShowTotalForceCheckBox extends JCheckBox {
    public ShowTotalForceCheckBox( final Forces1DModule simpleForceModule ) {
        super( Force1DResources.get( "SimpleControlPanel.show.total.force" ), simpleForceModule.isShowTotalForce() );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                simpleForceModule.setShowTotalForce( isSelected() );
            }
        } );
    }
}