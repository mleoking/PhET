/**
 * Class: ToggleButton
 * Class: edu.colorado.phet.coreadditions
 * User: Ron LeMaster
 * Date: Sep 29, 2004
 * Time: 8:29:27 PM
 */
package edu.colorado.phet.coreadditions;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public abstract class ToggleButton extends JButton {
    private boolean isOn;
    private String onString;
    private String offString;

    public abstract void onAction();

    public abstract void offAction();

    public ToggleButton( String onString, String offString ) {
        super( onString );
        this.onString = onString;
        this.offString = offString;
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( !isOn ) {
                    ToggleButton.this.setText( ToggleButton.this.offString );
                    onAction();
                    isOn = true;
                }
                else {
                    ToggleButton.this.setText( ToggleButton.this.onString );
                    offAction();
                    isOn = false;
                }
            }
        } );
    }

    public void setOff() {
        ToggleButton.this.setText( onString );
        offAction();
        isOn = false;
    }
}
