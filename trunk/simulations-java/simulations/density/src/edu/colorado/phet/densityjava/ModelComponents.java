package edu.colorado.phet.densityjava;

import edu.colorado.phet.densityjava.common.MyRadioButton;

public class ModelComponents {
    public static class Units extends MyRadioButton.SimpleModel {
        private boolean isMetric = true;

        public boolean isMetric() {
            return isMetric;
        }

        public boolean isEnglish() {
            return !isMetric;
        }

        public void setMetric(boolean b) {
            if (isMetric != b) {
                isMetric = b;
                notifyListeners();
            }
        }

        public void setEnglish(boolean b) {
            setMetric(!b);
        }
    }
}
