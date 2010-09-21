package edu.colorado.phet.densityjava.common;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MyRadioButton extends JRadioButton {

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
