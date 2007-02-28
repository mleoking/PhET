/**
 * Class: MessageMonitor
 * Package: edu.colorado.phet.common.examples.hellophet.viewX
 * Author: Another Guy
 * Date: May 28, 2003
 */
package edu.colorado.phet.common.examples.hellophet.view;

import edu.colorado.phet.common.examples.hellophet.model.HelloPhetModel;
import edu.colorado.phet.common.model.BaseModel;

import javax.swing.*;
import java.util.Observable;
import java.util.Observer;

public class MessageMonitor extends JPanel implements Observer {
    private JTextField numTF;
    private BaseModel model;

    public MessageMonitor(BaseModel model) {
        this.model = model;
        numTF = new JTextField("0", 15);
        this.add(numTF);
        model.addObserver(this);
    }

    public void update(Observable o, Object arg) {
        HelloPhetModel model = (HelloPhetModel) o;
        int num = model.numModelElements();
        if (num != Integer.parseInt(numTF.getText())) {
            numTF.setText("" + model.numModelElements());
        }
    }
}
