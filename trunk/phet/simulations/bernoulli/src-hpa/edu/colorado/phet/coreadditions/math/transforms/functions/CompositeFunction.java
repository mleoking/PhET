package edu.colorado.phet.coreadditions.math.transforms.functions;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 26, 2002
 * Time: 12:50:46 PM
 * To change this template use Options | File Templates.
 */
public class CompositeFunction implements Function {
    ArrayList f = new ArrayList();

    public void addFunction(Function fn) {
        f.add(fn);
    }

    public Function functionAt(int i) {
        return (Function) f.get(i);
    }

    public double evaluate(double in) {
        for (int i = 0; i < f.size(); i++) {
            in = functionAt(i).evaluate(in);
        }
        return in;
        //return 0;
    }
}
