package edu.colorado.phet.coreadditions.plafdep;

import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.coreadditions.graphics.RescaleOp2;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PurpleLookAndFeel extends MetalLookAndFeel {
    static final Font lucidaFont = new Font("Lucida Sans", Font.BOLD, 24);
    static final Color purple = new Color(190, 175, 245);
    public static Color LIGHT_PURPLE = new Color(150, 150, 245);

    public TitledBorder createTitledBorder(String title) {
        TitledBorder tb = new TitledBorder(BorderFactory.createLineBorder(Color.black, 7), title);
        tb.setTitleFont(PurpleLookAndFeel.lucidaFont);
        return tb;
    }

    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        BufferedImage selImage = new ImageLoader().loadBufferedImage("images/icons/selected.gif");
        BufferedImage unImage = new ImageLoader().loadBufferedImage("images/icons/unselected.gif");
        int checkBoxIconImageWidth = 50;
        selImage = RescaleOp2.rescaleXMaintainAspectRatio(selImage, checkBoxIconImageWidth);
        unImage = RescaleOp2.rescaleXMaintainAspectRatio(unImage, checkBoxIconImageWidth);
        CheckBoxImageIcon cbii = new CheckBoxImageIcon(selImage, unImage);
        ColorUIResource background = new ColorUIResource(LIGHT_PURPLE);
        ColorUIResource buttonBackground = new ColorUIResource(new Color(190, 160, 245));
        Object[] defaults = {

            "Button.font", new FontUIResource(lucidaFont),
            "Button.background", new ColorUIResource(buttonBackground),
            "Button.foreground", new ColorUIResource(Color.black),
            "Button.margin", new InsetsUIResource(8, 8, 8, 8),
            "MenuItem.font", new FontUIResource(lucidaFont),
            "Menu.font", new FontUIResource(lucidaFont),
            "Panel.background", new ColorUIResource(LIGHT_PURPLE),
            "Panel.font", new FontUIResource(lucidaFont),
            "Dialog.font", new FontUIResource(lucidaFont),

            "CheckBox.font", new FontUIResource(lucidaFont),
            "CheckBox.background", new ColorUIResource(purple),
            "CheckBox.foreground", new ColorUIResource(Color.black), //
            "CheckBox.icon", cbii,
            "CheckBox.border", new BorderUIResource(BorderFactory.createBevelBorder(BevelBorder.RAISED)),

            "RadioButton.icon", cbii,
            "RadioButton.font", new FontUIResource(lucidaFont),
            "RadioButton.background", new ColorUIResource(purple),
            "ComboBox.font", new FontUIResource(lucidaFont),
            "ComboBox.background", new ColorUIResource(purple),

            "Panel.background", background
            , "Menu.background", background
            , "MenuItem.background", background
            , "MenuBar.background", background
            , "Slider.background", background
            , "RadioButton.background", background
            , "CheckBox.background", background
            , "Button.background", buttonBackground
        };
        table.putDefaults(defaults);
    }

    public boolean isSupportedLookAndFeel() {
        return true;
    }

    public static Color getBackgroundColor() {
        return LIGHT_PURPLE;
    }

    public class CheckBoxImageIcon implements Icon {
        private BufferedImage selected;
        private BufferedImage unselected;

        public CheckBoxImageIcon(BufferedImage selected, BufferedImage unselected) {
            this.selected = selected;
            this.unselected = unselected;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            JToggleButton cb = (JToggleButton) c;
            if (cb.isSelected())
                g.drawImage(selected, x, y, c);
            else
                g.drawImage(unselected, x, y, c);
        }

        public int getIconWidth() {
            return selected.getWidth();
        }

        public int getIconHeight() {
            return selected.getHeight();
        }

    }

}