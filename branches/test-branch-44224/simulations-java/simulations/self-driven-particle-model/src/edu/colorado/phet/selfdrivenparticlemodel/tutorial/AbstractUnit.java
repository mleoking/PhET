package edu.colorado.phet.selfdrivenparticlemodel.tutorial;

/**
 * User: Sam Reid
 * Date: Aug 25, 2005
 * Time: 11:10:42 PM
 * Copyright (c) Aug 25, 2005 by Sam Reid
 */
public interface AbstractUnit {
    void setBasePage( BasicTutorialCanvas basicTutorialCanvas );

    PageMaker[] getPageMakers();
}
