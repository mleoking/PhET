package smooth.metal;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.MetalIconFactory;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.io.Serializable;

/**
 * Smooth Icon Factory. These are a bit more difficult to handle, because
 * you can't subclass the factory methods in the MetalIconFactory. Some
 * genius thought they should be made private. Clever thinking dude!
 * This means we're duplicating some code here, which isn't that bad
 * since we want to draw stuff differently anyway.
 */
public class SmoothIconFactory extends MetalIconFactory {
    private static Icon radioButtonIcon;

    public static class RadioButtonIcon implements Icon, UIResource, Serializable {
        public void paintIcon( Component component, Graphics g, int i, int j ) {
            SmoothUtilities.configureGraphics( g );
            JRadioButton jradiobutton = (JRadioButton)component;
            ButtonModel buttonmodel = jradiobutton.getModel();
            boolean flag = buttonmodel.isSelected();
            java.awt.Color color = component.getBackground();
            Object obj = component.getForeground();
            javax.swing.plaf.ColorUIResource coloruiresource = MetalLookAndFeel.getControlShadow();
            javax.swing.plaf.ColorUIResource coloruiresource1 = MetalLookAndFeel.getControlDarkShadow();
            Object obj1 = MetalLookAndFeel.getControlHighlight();
            Object obj2 = MetalLookAndFeel.getControlHighlight();
            Object obj3 = color;

            if( !buttonmodel.isEnabled() ) {
                obj1 = obj2 = color;
                coloruiresource1 = ( (javax.swing.plaf.ColorUIResource)( obj = coloruiresource ) );
            }
            else if( buttonmodel.isPressed() && buttonmodel.isArmed() ) {
                obj1 = obj3 = coloruiresource;
            }

            g.translate( i, j );
            g.setColor( ( (java.awt.Color)( obj3 ) ) );
            g.fillRect( 2, 2, 9, 9 );
//			g.setColor(coloruiresource1);
//			g.drawLine(4, 0, 7, 0);
//			g.drawLine(8, 1, 9, 1);
//			g.drawLine(10, 2, 10, 3);
//			g.drawLine(11, 4, 11, 7);
//			g.drawLine(10, 8, 10, 9);
//			g.drawLine(9, 10, 8, 10);
//			g.drawLine(7, 11, 4, 11);
//			g.drawLine(3, 10, 2, 10);
//			g.drawLine(1, 9, 1, 8);
//			g.drawLine(0, 7, 0, 4);
//			g.drawLine(1, 3, 1, 2);
//			g.drawLine(2, 1, 3, 1);
//			g.setColor(((java.awt.Color)(obj1)));
//			g.drawLine(2, 9, 2, 8);
//			g.drawLine(1, 7, 1, 4);
//			g.drawLine(2, 2, 2, 3);
//			g.drawLine(2, 2, 3, 2);
//			g.drawLine(4, 1, 7, 1);
//			g.drawLine(8, 2, 9, 2);
//			g.setColor(((java.awt.Color)(obj2)));
//			g.drawLine(10, 1, 10, 1);
//			g.drawLine(11, 2, 11, 3);
//			g.drawLine(12, 4, 12, 7);
//			g.drawLine(11, 8, 11, 9);
//			g.drawLine(10, 10, 10, 10);
//			g.drawLine(9, 11, 8, 11);
//			g.drawLine(7, 12, 4, 12);
//			g.drawLine(3, 11, 2, 11);

            g.setColor( coloruiresource1 );
            g.drawOval( 0, 0, 10, 10 );
            g.setColor( (java.awt.Color)obj1 );
            g.drawOval( 1, 1, 10, 10 );

            if( flag ) {
                g.setColor( ( (java.awt.Color)( obj ) ) );
//				g.fillRect(4, 4, 4, 4);
//				g.drawLine(4, 3, 7, 3);
//				g.drawLine(8, 4, 8, 7);
//				g.drawLine(7, 8, 4, 8);
//				g.drawLine(3, 7, 3, 4);
                g.fillOval( 3, 3, 6, 6 );
            }

            g.translate( -i, -j );
        }

        public int getIconWidth() {
            return 13;
        }

        public int getIconHeight() {
            return 13;
        }
    }

    public static Icon getRadioButtonIcon() {
        if( radioButtonIcon == null ) {
            radioButtonIcon = new RadioButtonIcon();
        }

        return radioButtonIcon;
    }

}
