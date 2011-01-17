// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit1;

import edu.colorado.phet.selfdrivenparticlemodel.SelfDrivenParticleModelApplication;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.AbstractUnit;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.Page;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.PageMaker;

public class Unit1 implements AbstractUnit {
    private PageMaker[] pageMakers;
    private SelfDrivenParticleModelApplication tutorialApplication;

    public Unit1( SelfDrivenParticleModelApplication tutorialApplication ) {
        this.tutorialApplication = tutorialApplication;
    }

    public PageMaker[] getPageMakers() {
        return pageMakers;
    }

    public void setBasePage( BasicTutorialCanvas basicPage ) {
        finishInit( basicPage );
    }

    private void finishInit( final BasicTutorialCanvas page ) {

        this.pageMakers = new PageMaker[] {
                new PageMaker() {
                    public Page createPage() {
                        return new InitSection00( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new InitSection05( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new InitSection07( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new CreateUniverseSection10( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new CreateParticleSection20( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new CollisionSection30( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new ExplainCollisionsSection40( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new AddingRandomness50( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new AddRemoveParticles60( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new ReduceRandomnessForManyParticles65( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new OptionalHalos70( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new FullFeatures80( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new EndIntroduction( page, tutorialApplication );
                    }
                },
//                new PageMaker() {
//                    public Page createPage() {
//                        return new OrderParameter90( page );
//                    }
//                },
//                new PageMaker() {
//                    public Page createPage() {
//                        return new OrderParameter100( page );
//                    }
//                },
//                new PageMaker() {
//                    public Page createPage() {
//                        return new ThisPageForRent( page );
//                    }
//                }
        };
    }
}
