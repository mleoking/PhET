/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.kirkhoff;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.circuit.CircuitObserver;
import edu.colorado.phet.cck.elements.kirkhoff.equations.Equation;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: Sam Reid
 * Date: Sep 3, 2003
 * Time: 3:25:35 AM
 * Copyright (c) Sep 3, 2003 by Sam Reid
 */
public class CircuitSolver implements CircuitObserver {

    RoughDraftKirkhoffSystemGenerator generator;
    KirkhoffSolutionApplicator applicator;
    Logger kirkhoffLogger;

    public CircuitSolver() {
        kirkhoffLogger = Logger.getAnonymousLogger();
        kirkhoffLogger.setLevel(Level.ALL);
        generator = new RoughDraftKirkhoffSystemGenerator(kirkhoffLogger);
        applicator = new RoughDraftApplicator(kirkhoffLogger);
    }

    public void applyKirchoffsLaws(Circuit parent) {
//        O.d("parent=" + parent);
        CircuitGraph cg = new CircuitGraph(parent);
        MatrixTable mt = new MatrixTable(cg);
        Equation[] system = generator.getSystem(cg, mt);
//        KirkhoffEquation.print(system);
        applicator.apply(system, cg, mt);
        Melter m = new Melter();
        m.doMelt(parent);
    }

    public void branchAdded(Circuit circuit2, Branch branch) {
        applyKirchoffsLaws(circuit2);
    }

    public void branchRemoved(Circuit circuit2, Branch branch) {
        applyKirchoffsLaws(circuit2);
    }

    public void connectivityChanged(Circuit circuit2) {
        applyKirchoffsLaws(circuit2);
    }

    public Logger getLogger() {
        return kirkhoffLogger;
    }

    public void setLogging(boolean on) {
        if (on)
            kirkhoffLogger.setLevel(Level.ALL);
        else
            kirkhoffLogger.setLevel(Level.OFF);
    }
}
