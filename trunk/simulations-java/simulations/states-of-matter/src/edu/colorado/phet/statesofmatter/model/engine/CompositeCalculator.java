package edu.colorado.phet.statesofmatter.model.engine;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

public class CompositeCalculator implements Calculator {
    private final Collection calculators;
    private volatile double[] temp;

    public CompositeCalculator(Collection calculators) {
        this.calculators = calculators;
    }

    public CompositeCalculator(Calculator[] calculators) {
        this(Arrays.asList(calculators));
    }

    public void calculate(StatesOfMatterParticle p, double[] forces) {
        if (calculators.size() == 0) return;
        
        if (temp == null || temp.length != forces.length) {
            temp = new double[forces.length];
        }

        Arrays.fill(forces, 0.0);
        
        for (Iterator iterator = calculators.iterator(); iterator.hasNext();) {
            Calculator calculator = (Calculator)iterator.next();

            calculator.calculate(p, temp);

            for (int i = 0; i < temp.length; i++) {
                forces[i] += temp[i];
            }
        }
    }

    public Collection getCalculators() {
        return calculators;
    }
}
