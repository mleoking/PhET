// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.movingman.view;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Sam Reid
 */
public class ShowVectorCheckBox extends JCheckBox {
    public ShowVectorCheckBox(String title, final BooleanProperty visibleProperty) {
        super(title, visibleProperty.get());
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                visibleProperty.set( isSelected() );
            }
        });
        visibleProperty.addObserver(new SimpleObserver() {
            public void update() {
                setSelected(visibleProperty.get());
            }
        });
        setOpaque(false);//todo: does this work on mac?
    }
}
