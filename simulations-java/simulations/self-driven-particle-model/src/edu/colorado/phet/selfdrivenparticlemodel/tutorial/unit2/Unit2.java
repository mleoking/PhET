// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit2;

import edu.colorado.phet.selfdrivenparticlemodel.SelfDrivenParticleModelApplication;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.AbstractUnit;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.Page;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.PageMaker;

public class Unit2 implements AbstractUnit {
    private PageMaker[] pageMakers;
    private SelfDrivenParticleModelApplication tutorialApplication;

    public Unit2( SelfDrivenParticleModelApplication tutorialApplication ) {
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
                        return new InitEmergence00( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new OrderParameter90( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new OrderParameter100( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new PlotOrderParameterVsTime( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new DescribeOrderVsRandomness200( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new DescribeOrderVsRandomness210( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new DescribeOrderVsRandomness215( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new PlotOrderParameterVsRandomness( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new DescribePowerLawBehavior220( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new DescribePowerLawBehavior230( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new PlotBeta240( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new ExplainBeta250( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new ExplainBeta260( page );
                    }
                },
                new PageMaker() {
                    public Page createPage() {
                        return new FinishedUnit2( page, tutorialApplication );
                    }
                }
        };
    }
}
