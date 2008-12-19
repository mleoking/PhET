package edu.colorado.phet.build.java;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;

public class Boxer {
    public static JComponent horizontalBox( JComponent a, JComponent b ) {
        return box( a, b, BoxLayout.X_AXIS );
    }

    public static JComponent verticalBox( JComponent a, JComponent b ) {
        return box( a, b, BoxLayout.Y_AXIS );
    }

    private static JComponent box( JComponent a, JComponent b, int axis ) {
        return useBoxLayout( a, b, axis );
//        return useVFLayout( a, b, axis );
//        return useBorderLayout( a, b, axis );
    }

    private static JComponent useBorderLayout( JComponent a, JComponent b, int axis ) {
        JPanel panel = new JPanel(new BorderLayout( ));
        if ( axis == BoxLayout.Y_AXIS ) {
            panel.add(a,BorderLayout.NORTH);
            panel.add(b,BorderLayout.SOUTH);
        }
        else {
            panel.add(a,BorderLayout.WEST);
            panel.add(b,BorderLayout.EAST);
        }
        return panel;
    }

    private static JComponent useVFLayout( JComponent a, JComponent b, int axis ) {
        JPanel panel = new JPanel();
        if ( axis == BoxLayout.Y_AXIS ) {
            panel = new VerticalLayoutPanel();
        }
        else {
            panel.setLayout( new FlowLayout() );
        }
        panel.add( a );
        panel.add( b );
        return panel;
    }

    private static JComponent useBoxLayout( JComponent a, JComponent b, int axis ) {
        JPanel panel = new JPanel();
        panel.setLayout( new BoxLayout( panel, axis ) );
        panel.add( a );
        panel.add( b );
        return panel;
    }
}
