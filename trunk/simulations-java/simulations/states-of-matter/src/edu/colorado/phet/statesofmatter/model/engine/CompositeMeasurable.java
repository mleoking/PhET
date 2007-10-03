package edu.colorado.phet.statesofmatter.model.engine;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class CompositeMeasurable implements Measurable {
    private final Collection measurables;

    public CompositeMeasurable(Measurable[] measurables) {
        this(Arrays.asList(measurables));
    }

    public CompositeMeasurable(Collection measurables) {
        this.measurables = measurables;
    }

    public double measure() {
        double total = 0.0;

        for (Iterator iterator = measurables.iterator(); iterator.hasNext();) {
            Measurable measurable = (Measurable)iterator.next();

            total += measurable.measure();
        }

        return total;
    }

    public Collection getMeasurables() {
        return measurables;
    }
}
