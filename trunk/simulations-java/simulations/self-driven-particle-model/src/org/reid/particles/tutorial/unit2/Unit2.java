/* Copyright 2004, Sam Reid */
package org.reid.particles.tutorial.unit2;

import org.reid.particles.tutorial.*;

/**
 * User: Sam Reid
 * Date: Aug 25, 2005
 * Time: 10:36:44 PM
 * Copyright (c) Aug 25, 2005 by Sam Reid
 */

public class Unit2 implements AbstractUnit {
    private PageMaker[] pageMakers;
    private SelfDrivenParticleApplication tutorialApplication;

    public Unit2( SelfDrivenParticleApplication tutorialApplication ) {
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
//                ,
//                new PageMaker() {
//                    public Page createPage() {
//                        return new ThisPageForRent( page );
//                    }
//                }
        };
    }
}
