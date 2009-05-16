package edu.colorado.phet.densityjava.common;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: May 16, 2009
 * Time: 1:35:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyRadioButton extends JRadioButton {
    public static interface Getter<T> {
        T getValue();
    }

    public static interface Setter<T> {
        void setValue(T t);
    }

    public static interface Unit {
        void update();
    }

    public static interface Model {
        void addListener(Unit unit);
    }

    public MyRadioButton(String name, Model model, final Getter<Boolean> getter, final Setter<Boolean> setter) {
        super(name);
        setSelected(getter.getValue());
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setter.setValue(true);
                setSelected(getter.getValue());
            }
        });
        model.addListener(new Unit() {
            public void update() {
                setSelected(getter.getValue());
            }
        });
    }
}
