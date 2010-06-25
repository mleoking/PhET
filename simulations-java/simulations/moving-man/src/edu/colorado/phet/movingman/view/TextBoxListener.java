package edu.colorado.phet.movingman.view;

import edu.colorado.phet.common.motion.charts.TextBox;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.movingman.model.MovingMan;
import edu.colorado.phet.movingman.model.MovingManModel;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * @author Sam Reid
 */
public interface TextBoxListener {
    final DefaultDecimalFormat decimalFormat = new DefaultDecimalFormat("0.00");

    void addListeners(final TextBox textBox);

    public static class Position implements TextBoxListener {
        MovingManModel model;

        public Position(MovingManModel model) {
            this.model = model;
        }
//Some TextBoxes have labels, some do not, so factor out the listener code

        public void addListeners(final TextBox textBox) {
            final MovingMan.Listener listener = new MovingMan.Listener() {
                public void changed() {
                    textBox.setText(decimalFormat.format(model.getMovingMan().getPosition()));
                }
            };
            listener.changed();//synchronize state on initialization
            model.getMovingMan().addListener(listener);

            textBox.addListener(new TextBox.Listener() {
                public void changed() {
                    String text = textBox.getText();//have to store it since the next line modifies it
                    model.getMovingMan().setPositionDriven();
                    model.setMousePosition(Double.parseDouble(text));
                }
            });
            textBox.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    model.getMovingMan().setPositionDriven();
                }
            });
        }
    }

    public static class Velocity implements TextBoxListener {
        private final MovingManModel model;

        public Velocity(MovingManModel model) {
            this.model = model;
        }

        public void addListeners(final TextBox textBox) {
            final MovingMan.Listener listener = new MovingMan.Listener() {
                public void changed() {
                    textBox.setText(decimalFormat.format(model.getMovingMan().getVelocity()));
                }
            };
            model.getMovingMan().addListener(listener);
            listener.changed();
            textBox.addListener(new TextBox.Listener() {
                public void changed() {
                    String text = textBox.getText();//have to store it since the next line modifies it
                    model.getMovingMan().setVelocityDriven();
                    model.getMovingMan().setVelocity(Double.parseDouble(text));
                }
            });
            textBox.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    model.getMovingMan().setVelocityDriven();
                }
            });
        }

    }

    public static class Acceleration implements TextBoxListener {
        private final MovingManModel model;

        public Acceleration(MovingManModel model) {
            this.model = model;
        }

        public void addListeners(final TextBox textBox) {
            final MovingMan.Listener listener = new MovingMan.Listener() {
                public void changed() {
                    textBox.setText(decimalFormat.format(model.getMovingMan().getAcceleration()));
                }
            };
            model.getMovingMan().addListener(listener);
            listener.changed();
            textBox.addListener(new TextBox.Listener() {
                public void changed() {
                    String text = textBox.getText();//have to store it since the next line modifies it
                    model.getMovingMan().setAccelerationDriven();
                    model.getMovingMan().setAcceleration(Double.parseDouble(text));
                }
            });
            textBox.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    model.getMovingMan().setAccelerationDriven();
                }
            });
        }
    }
}