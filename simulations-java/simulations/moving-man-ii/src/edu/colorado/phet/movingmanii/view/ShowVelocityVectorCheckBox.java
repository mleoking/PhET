package edu.colorado.phet.movingmanii.view;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.movingmanii.model.MutableBoolean;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Sam Reid
 */
public class ShowVelocityVectorCheckBox extends JCheckBox {
    public ShowVelocityVectorCheckBox(String title, final MutableBoolean visibleProperty) {
        super(title, visibleProperty.getValue());
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                visibleProperty.setValue(isSelected());
            }
        });
        visibleProperty.addObserver(new SimpleObserver() {
            public void update() {
                setSelected(visibleProperty.getValue());
            }
        });
        setOpaque(false);//todo: does this work on mac?
    }
}
