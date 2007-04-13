/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *        Liquid Look and Feel                                                   *
 *                                                                              *
 *  Author, Miroslav Lazarevic                                                  *
 *                                                                              *
 *   For licensing information and credits, please refer to the                 *
 *   comment in file com.birosoft.liquid.LiquidLookAndFeel                      *
 *                                                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.birosoft.liquid;

import com.birosoft.liquid.borders.LiquidTextFieldBorder;

import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.JTextComponent;


public class LiquidTextFieldUI extends BasicTextFieldUI {
    static JTextComponent _editor;

    /**
     * Method createUI.
     * @param c
     * @return ComponentUI
     */
    public static ComponentUI createUI(JComponent c) {
        return new LiquidTextFieldUI();
    }

    /**
     * @see javax.swing.plaf.basic.BasicTextFieldUI#installUI(javax.swing.JComponent)
     */
    public void installUI(JComponent c) {
        super.installUI(c);
    }

    protected void paintBackground(Graphics g) {
        JTextComponent editor = getComponent();

        if ((_editor == null) || !_editor.equals(editor)) {
            _editor = editor;

            Insets margin = editor.getMargin();
            Border border = editor.getBorder();

            if ((margin.top > 0) && (margin.left > 0) && (margin.bottom > 0) &&
                    (margin.right > 0) && border instanceof LiquidTextFieldBorder ) {
                ((LiquidTextFieldBorder) border).setInsets(margin);
            }
        }

        if (editor.isEnabled()) {
            g.setColor(editor.getBackground());
        } else {
            g.setColor(UIManager.getDefaults().getColor("TextField.disabledBackground"));
        }

        g.fillRect(0, 0, editor.getWidth(), editor.getHeight());
    }
}
