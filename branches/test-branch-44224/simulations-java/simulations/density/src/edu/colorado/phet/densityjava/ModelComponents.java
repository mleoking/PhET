package edu.colorado.phet.densityjava;

import edu.colorado.phet.densityjava.common.SimpleModel;

public class ModelComponents {
    public static class Units extends SimpleModel {
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

    public static class DisplayDimensions extends SimpleModel {
        private boolean display = false;

        public boolean isDisplay() {
            return display;
        }

        public void setDisplay(boolean display) {
            if (this.display != display) {
                this.display = display;
                notifyListeners();
            }
        }
    }
}
