package edu.colorado.phet.densityjava.view;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.densityjava.common.MyRadioButton;
import edu.colorado.phet.densityjava.common.Getter;
import edu.colorado.phet.densityjava.common.Setter;
import edu.colorado.phet.densityjava.model.MassVolumeModel;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: May 16, 2009
 * Time: 1:34:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class MassVolumeChooser extends VerticalLayoutPanel {
    public MassVolumeChooser(final MassVolumeModel massVolumeModel) {
        JRadioButton sameMass = new MyRadioButton("Objects of same mass", massVolumeModel, new Getter<Boolean>() {
            public Boolean getValue() {
                return massVolumeModel.isSameMass();
            }
        }, new Setter<Boolean>() {
            public void setValue(Boolean aBoolean) {
                massVolumeModel.setSameMass(aBoolean);
            }
        });
        JRadioButton sameVolume = new MyRadioButton("Objects of same volume", massVolumeModel, new Getter<Boolean>() {
            public Boolean getValue() {
                return massVolumeModel.isSameVolume();
            }
        }, new Setter<Boolean>() {
            public void setValue(Boolean aBoolean) {
                massVolumeModel.setSameVolume(aBoolean);
            }
        });
        add(sameMass);
        add(sameVolume);
    }
}
