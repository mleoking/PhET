package phet.paint.gauges;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Scaling implements ActionListener {
    IGauge gauge;
    String name;
    double min;
    double max;

    public Scaling( IGauge gauge, String name, double min, double max ) {
        this.gauge = gauge;
        this.name = name;
        this.min = min;
        this.max = max;
    }

    public String getName() {
        return name;
    }

    public void actionPerformed( ActionEvent ae ) {
        gauge.setMin( min );
        gauge.setMax( max );
        //gauge.setNumMaj(numMaj);
        //gauge.setNumMin(numMin);
    }
}
