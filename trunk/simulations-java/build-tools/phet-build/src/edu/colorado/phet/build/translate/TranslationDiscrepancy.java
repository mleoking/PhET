package edu.colorado.phet.build.translate;

import edu.colorado.phet.build.PhetProject;

import java.util.Set;

public class TranslationDiscrepancy {
    private final Set extraLocal;
    private final Set extraRemote;
    private final PhetProject phetProject;
    private final String flavor;

    public TranslationDiscrepancy(Set extraLocal, Set extraRemote, PhetProject phetProject, String flavor) {
        this.extraLocal  = extraLocal;
        this.extraRemote = extraRemote;
        this.phetProject = phetProject;
        this.flavor      = flavor;
    }

    public Set getExtraLocal() {
        return extraLocal;
    }

    public Set getExtraRemote() {
        return extraRemote;
    }

    public PhetProject getPhetProject() {
        return phetProject;
    }

    public String getFlavor() {
        return flavor;
    }

    public String toString() {
        return "need to be removed from remote jar: " + extraRemote + ", " + "need to be added to remote jar: " + extraLocal + " ";
    }
}
