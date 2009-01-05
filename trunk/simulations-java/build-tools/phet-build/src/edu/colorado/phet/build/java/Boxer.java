package edu.colorado.phet.build.java;

import javax.swing.*;

public class Boxer {
    public static JComponent horizontalBox( JComponent a, JComponent b ) {
        return box( a, b, BoxLayout.X_AXIS );
    }

    public static JComponent verticalBox( JComponent a, JComponent b ) {
        return box( a, b, BoxLayout.Y_AXIS );
    }

    private static JComponent box( JComponent a, JComponent b, int axis ) {
        return useBoxLayout( a, b, axis );
    }

    private static JComponent useBoxLayout( JComponent a, JComponent b, int axis ) {
        JPanel panel = new JPanel();
        panel.setLayout( new BoxLayout( panel, axis ) );
        panel.add( a );
        panel.add( b );
        return panel;
    }
}
