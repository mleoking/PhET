package edu.colorado.phet.rotation.controls;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.rotation.RotationStrings;
import edu.colorado.phet.rotation.util.UnicodeUtil;
import edu.colorado.phet.rotation.view.RotationLookAndFeel;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 10:03:31 PM
 */

public class SymbolKey extends JPanel {
    private GridBagConstraints gridBagConstraints;

    public SymbolKey() {
        setLayout( new GridBagLayout() );
        gridBagConstraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        JLabel label = new JLabel( RotationStrings.getString( "controls.symbol.key" ) );
        label.setFont( RotationLookAndFeel.getControlPanelTitleFont() );
        add( label, gridBagConstraints );

        addItem( UnicodeUtil.THETA, RotationStrings.getString( "variable.angle" ) );
        addItem( UnicodeUtil.OMEGA, RotationStrings.getString( "variable.angular.velocity" ) );
        addItem( UnicodeUtil.ALPHA, RotationStrings.getString( "variable.angular.acceleration" ) );
        addItem( RotationStrings.getString( "variable.x.and.y" ), RotationStrings.getString( "variable.position" ) );
        addItem( RotationStrings.getString( "variable.v" ), RotationStrings.getString( "variable.velocity" ) );
        addItem( RotationStrings.getString( "variable.a" ), RotationStrings.getString( "variable.acceleration" ) );
        setBorder( BorderFactory.createLineBorder( Color.lightGray ) );
    }

    private void addItem( String key, String s ) {
        JLabel label = new JLabel( key + " = " + s );
        label.setFont( RotationLookAndFeel.getLegendItemFont() );
        add( label, gridBagConstraints );
    }
}
