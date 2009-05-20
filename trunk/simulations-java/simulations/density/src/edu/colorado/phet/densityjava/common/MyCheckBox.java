package edu.colorado.phet.densityjava.common;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyCheckBox extends JCheckBox {

    public MyCheckBox(String name, Model model, final Getter<Boolean> getter, final Setter<Boolean> setter) {
        super(name);
        setSelected(getter.getValue());
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setter.setValue(isSelected());
                setSelected(isSelected());
            }
        });
        model.addListener(new Unit() {
            public void update() {
                setSelected(getter.getValue());
            }
        });
    }
}