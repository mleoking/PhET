/* Copyright 2004, Sam Reid */
package edu.colorado.phet.selfdrivenparticlemodel.tutorial;

import edu.colorado.phet.selfdrivenparticlemodel.SelfDrivenParticleModelApplication;

/**
 * User: Sam Reid
 * Date: Aug 28, 2005
 * Time: 8:59:54 PM
 * Copyright (c) Aug 28, 2005 by Sam Reid
 */

public class Unit3 implements AbstractUnit {
    private PageMaker[] pageMakers;
    private SelfDrivenParticleModelApplication tutorialApplication;

    public Unit3( SelfDrivenParticleModelApplication tutorialApplication ) {
        this.tutorialApplication = tutorialApplication;
    }

    public PageMaker[] getPageMakers() {
        return pageMakers;
    }

    public void setBasePage( BasicTutorialCanvas basicPage ) {
        finishInit( basicPage );
    }

    private void finishInit( final BasicTutorialCanvas page ) {
        this.pageMakers = new PageMaker[]{
                new PageMaker() {
                    public Page createPage() {
                        return new IntroFinalUnit300( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new FinalCommentsPage310( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new FullExperimentPage( page );
                    }
                }
//                new PageMaker() {
//                    public Page createPage() {
//                        return new ThisPageForRent( page );
//                    }
//                }
        };
    }

}
