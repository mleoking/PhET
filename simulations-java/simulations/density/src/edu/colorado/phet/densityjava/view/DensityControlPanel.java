package edu.colorado.phet.densityjava.view;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.densityjava.ModelComponents;
import edu.colorado.phet.densityjava.common.MyRadioButton;

import javax.swing.*;

public class DensityControlPanel extends ControlPanel {
    public DensityControlPanel(ModelComponents.Units units) {
        addControl(new JLabel("Show"));
        addSeparator();
        addControl(new ForceVectorControl());
        addSeparator();
        addControl(new FluidVolumeControl());
        addSeparator();
        addControl(new UnitsControl(units));
        addSeparator();
        addControl(new ObjectLabelControl());
    }

    private class ForceVectorControl extends VerticalLayoutPanel {
        private ForceVectorControl() {
            add(new JCheckBox("Force Vectors"));
            add(new JLabel("gravity"));
            add(new JLabel("buoyancy"));
            add(new JLabel("contact"));
            add(new JLabel("fluid drag"));
        }
    }

    private class FluidVolumeControl extends JCheckBox {
        private FluidVolumeControl() {
            super("Fluid Volume");
        }
    }

    private class UnitsControl extends VerticalLayoutPanel {
        private UnitsControl(final ModelComponents.Units units) {
            add(new MyRadioButton("metric units", units, new MyRadioButton.Getter<Boolean>() {
                public Boolean getValue() {
                    return units.isMetric();
                }
            }, new MyRadioButton.Setter<Boolean>() {
                public void setValue(Boolean aBoolean) {
                    units.setMetric(aBoolean);
                }
            }));
            add(new MyRadioButton("english units", units, new MyRadioButton.Getter<Boolean>() {
                public Boolean getValue() {
                    return units.isEnglish();
                }
            }, new MyRadioButton.Setter<Boolean>() {
                public void setValue(Boolean aBoolean) {
                    units.setEnglish(aBoolean);
                }
            }));
        }
    }

    private class ObjectLabelControl extends VerticalLayoutPanel {
        private ObjectLabelControl() {
            add(new JLabel("Object Labels:"));
            add(new JComboBox(new Object[]{"none"}));
        }
    }
}
