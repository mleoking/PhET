package edu.colorado.phet.densityjava.view;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.densityjava.ModelComponents;
import edu.colorado.phet.densityjava.common.Getter;
import edu.colorado.phet.densityjava.common.MyCheckBox;
import edu.colorado.phet.densityjava.common.MyRadioButton;
import edu.colorado.phet.densityjava.common.Setter;

import javax.swing.*;

public class DensityControlPanel extends ControlPanel {
    public DensityControlPanel(ModelComponents.Units units, ModelComponents.DisplayDimensions displayDimensions) {
        addControl(new JLabel("Show"));
        addSeparator();
        addControl(new ForceVectorControl());
        addSeparator();
        addControl(new DisplayDimensionsControl(displayDimensions));
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

    private class DisplayDimensionsControl extends MyCheckBox {
        public DisplayDimensionsControl(final ModelComponents.DisplayDimensions displayDimensions) {
            super("Fluid Dimensions", displayDimensions, new Getter<Boolean>() {
                public Boolean getValue() {
                    return displayDimensions.isDisplay();
                }
            }, new Setter<Boolean>() {
                public void setValue(Boolean aBoolean) {
                    displayDimensions.setDisplay(aBoolean);
                }
            });
        }
    }

    private class UnitsControl extends VerticalLayoutPanel {
        private UnitsControl(final ModelComponents.Units units) {
            add(new MyRadioButton("metric units", units, new Getter<Boolean>() {
                public Boolean getValue() {
                    return units.isMetric();
                }
            }, new Setter<Boolean>() {
                public void setValue(Boolean aBoolean) {
                    units.setMetric(aBoolean);
                }
            }));
            add(new MyRadioButton("english units", units, new Getter<Boolean>() {
                public Boolean getValue() {
                    return units.isEnglish();
                }
            }, new Setter<Boolean>() {
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
