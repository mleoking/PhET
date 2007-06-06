/* Copyright 2004, Sam Reid */
package org.reid.particles.tutorial;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import org.reid.particles.model.Particle;
import org.reid.particles.model.ParticleModel;
import org.reid.particles.view.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Aug 23, 2005
 * Time: 12:04:40 AM
 * Copyright (c) Aug 23, 2005 by Sam Reid
 */

public class BasicTutorialCanvas extends TutorialCanvas implements IParticleApp {

    private UniverseGraphic universeGraphic;
    private ParticleModel particleModel;
//    private JTextArea textArea;
    private TutorialTextArea textArea;
    private int textBoundsHeight = 150;
    private PButton nextSwing;
    private PActivity modelActivity;
    private ArrayList particleGraphics = new ArrayList();
    private ArrayList influenceGraphics = new ArrayList();
    private double modelDT = 1.0;
    private PageMaker[] pageMakers;
    private ArrayList pages = new ArrayList();
    private int advancedToPageIndex = 0;
    private boolean halosVisible;
    private PButton prevSwing;
    private boolean showedPrevButton = false;
    private int viewingPageIndex = 0;
    private TutorialApplication tutorialApplication;

    public BasicTutorialCanvas( TutorialApplication tutorialApplication, AbstractUnit unit ) {
        this.tutorialApplication = tutorialApplication;
        unit.setBasePage( this );
        particleModel = new ParticleModel( 550, 550 );
        particleModel.setRandomness( 0.0 );
        universeGraphic = new UniverseGraphic( particleModel );
        if( TutorialApplication.isLowResolution() ) {
            universeGraphic.scale( 0.82 );
        }
        textArea = new TutorialTextArea();
        setLayout( null );
        add( textArea );
        nextSwing = new PButton( this, "Next " );

        nextSwing.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                removeChild( nextSwing );
                nextSection();
            }
        } );

        prevSwing = new PButton( this, "Previous" );
        prevSwing.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                previousSection();
            }
        } );

        relayoutChildren();
        modelActivity = new PActivity( -1, 30 ) {
            protected void activityStep( long elapsedTime ) {
                super.activityStep( elapsedTime );
                particleModel.step( modelDT );
            }
        };

        this.pageMakers = unit.getPageMakers();
        this.advancedToPageIndex = 0;
//        this.advancedToPageIndex = pageMakers.length-2;
        Page currentPage = pageMakers[advancedToPageIndex].createPage();
        pages.add( currentPage );
        addKeyListener( tutorialApplication.getKeyHandler() );
        getKeyListeners();
    }

    public void setModelDT( double modelDT ) {
        this.modelDT = modelDT;
    }

    public void setText( String text ) {
        textArea.setText( text );
    }

    public void createUniverse() {
        addChild( universeGraphic );
        topLeft( universeGraphic );
    }

    public UniverseGraphic getUniverseGraphic() {
        return universeGraphic;
    }

    public void topLeft( PNode node ) {
//        node.setOffset( ( getWidth() - node.getFullBounds().getWidth() ) / 2, text( getHeight() - node.getFullBounds().getHeight() ) / 2+textArea.getHeight()/2 );
        int inset = 2;
        node.setOffset( inset, textArea.getY() + textArea.getHeight() + inset );
    }


    public void append( String finishText ) {
        textArea.setText( textArea.getText() + finishText );
    }

    public void clearParticles() {
        while( getParticleModel().numParticles() > 0 ) {
            removeParticle( getParticleModel().lastParticle() );
        }
    }

    public ParticleModel getParticleModel() {
        return particleModel;
    }

    static final Random random = new Random();

    public void setNumberParticles( int numParticles ) {
        boolean changed = false;
        while( numParticles < particleModel.numParticles() ) {
            removeParticle( particleModel.lastParticle() );
            changed = true;
        }
        while( numParticles > particleModel.numParticles() ) {
            Color color = ParticleGraphic.newRandomColor();
            addParticle( random.nextDouble() * getParticleModel().getBoxWidth(), random.nextDouble() * particleModel.getBoxHeight(), random.nextDouble() * 2 * Math.PI, color );
            changed = false;
        }
    }

    private void removeParticle( Particle particle ) {
        for( int i = 0; i < particleGraphics.size(); i++ ) {
            ParticleGraphicWithTail particleGraphicWithTail = (ParticleGraphicWithTail)particleGraphics.get( i );
            if( particleGraphicWithTail.getParticle() == particle ) {
                getUniverseGraphic().removeParticleGraphic( particleGraphicWithTail );
                particleGraphics.remove( particleGraphicWithTail );
                i--;
            }
        }
        for( int i = 0; i < influenceGraphics.size(); i++ ) {
            ParticleInfluenceGraphic particleInfluenceGraphic = (ParticleInfluenceGraphic)influenceGraphics.get( i );
            if( particleInfluenceGraphic.getParticle() == particle ) {
                getUniverseGraphic().removeInfluenceGraphic( particleInfluenceGraphic );
                influenceGraphics.remove( particleInfluenceGraphic );
                i--;
            }
        }
        getParticleModel().removeParticle( particle );
    }

    public void addHalos() {
        for( int i = 0; i < particleGraphics.size(); i++ ) {
            ParticleGraphicWithTail particleGraphicWithTail = (ParticleGraphicWithTail)particleGraphics.get( i );
            if( !isHaloShowing( particleGraphicWithTail.getParticle() ) ) {
                ParticleInfluenceGraphic particleInfluenceGraphic = new ParticleInfluenceGraphic( getParticleModel(), particleGraphicWithTail.getParticle() );
                universeGraphic.addInfluenceGraphic( particleInfluenceGraphic );
                influenceGraphics.add( particleInfluenceGraphic );
            }
        }
    }

    private boolean isHaloShowing( Particle particle ) {
        for( int i = 0; i < influenceGraphics.size(); i++ ) {
            ParticleInfluenceGraphic particleInfluenceGraphic = (ParticleInfluenceGraphic)influenceGraphics.get( i );
            if( particleInfluenceGraphic.getParticle() == particle ) {
                return true;
            }
        }
        return false;
    }

    public Point2D getNextButtonLocation() {
        if( textArea.getWidth() == 0 ) {
            System.out.println( "0xtextArea = " + textArea );
            System.out.println( "getWidth() = " + getWidth() );
        }
        return new Point2D.Double( textArea.getWidth() - nextSwing.getFullBounds().getWidth() - 5, textArea.getHeight() + 5 );
    }

    public void showNextButton() {
        if( !getLayer().getChildrenReference().contains( nextSwing ) ) {
            addChild( nextSwing );
            nextSwing.setOffset( getNextButtonLocation() );
        }
    }

    public void hideNextButton() {
        while( getLayer().getChildrenReference().contains( nextSwing ) ) {
            removeChild( nextSwing );
        }
    }

    private void previousSection() {
        if (viewingPageIndex==0&&tutorialApplication.isFirstUnit()){
            return;
        }
        else if( viewingPageIndex == 0 ) {
            sectionAt( viewingPageIndex ).teardown();
            tutorialApplication.previousUnit();
            return;
        }
        sectionAt( viewingPageIndex ).teardown();
        viewingPageIndex--;
//        currentPage.teardown();
        sectionAt( viewingPageIndex ).init();

        System.out.println( "viewingPageIndex = " + viewingPageIndex );
//        advancedToPageIndex--;
//        synchronizePage();
        synchronizeFullText();
        showNextButton();
    }

    private void synchronizeFullText() {
        String fullText = pageMakers[viewingPageIndex].createPage().getFullText();
        textArea.setText( fullText );
        showNextButton();
    }

    Page sectionAt( int i ) {
        return (Page)pages.get( i );
    }

    private void synchronizeLastPage() {
        String visibleText = sectionAt( viewingPageIndex ).getVisibleText();
        textArea.setText( visibleText );
        if( sectionAt( viewingPageIndex ).getAdvanced() ) {
            showNextButton();
        }
    }

    private void nextSection() {
        if( viewingPageIndex < advancedToPageIndex ) {
            sectionAt( viewingPageIndex ).teardown();
            viewingPageIndex++;
            sectionAt( viewingPageIndex ).init();
            if( viewingPageIndex == advancedToPageIndex ) {
                synchronizeLastPage();
            }
            else {
                synchronizeFullText();
            }
        }
        else {
            if( advancedToPageIndex + 1 < pageMakers.length ) {
                sectionAt( viewingPageIndex ).teardown();
                //todo check for available in array
                advancedToPageIndex++;
                viewingPageIndex++;
                Page next = pageMakers[advancedToPageIndex].createPage();
                pages.add( next );

                sectionAt( viewingPageIndex ).init();
            }
//            currentPage.teardown();
//            currentPage = next;
//            next.init();
        }
        if( !showedPrevButton ) {
            showedPrevButton = true;
            showPrevButton();
        }
        System.out.println( "viewingPageIndex = " + viewingPageIndex );
        System.out.println( "advancedToPageIndex = " + advancedToPageIndex );
    }

    private void showPrevButton() {
        if( !getLayer().getChildrenReference().contains( prevSwing ) ) {
            addChild( prevSwing );
            relayoutChildren();
        }
    }

    public void addParticle( double x, double y, double angle, Color color ) {
        Particle particle = new Particle( x, y, angle );
        ParticleGraphicWithTail particleGraphic = new ParticleGraphicWithTail( particle );
        particleGraphic.setColor( color );
        particleModel.addParticle( particle );
        particle.setLocation( x, y );//redundant?
        universeGraphic.addParticleGraphic( particleGraphic );
        particleGraphics.add( particleGraphic );
        if( halosVisible ) {
            addHalos();
        }
    }

    public void startModel() {
        getRoot().addActivity( modelActivity );
    }

    public void stopModel() {
        getRoot().getActivityScheduler().removeActivity( modelActivity );
    }

    public void start( TutorialApplication tutorialApplication ) {
        super.start( tutorialApplication );
        sectionAt( viewingPageIndex ).init();
//        currentPage.init();
    }

    protected void relayoutChildren() {

        super.relayoutChildren();
        testTopLeft( universeGraphic );
        if( textArea != null ) {
            textArea.setBounds( 0, 0, getWidth(), textBoundsHeight );
        }
        nextSwing.setOffset( getNextButtonLocation() );
        prevSwing.setOffset( 2 + universeGraphic.getFullBounds().getMaxX(), nextSwing.getFullBounds().getY() );

        if( getCurrentPage() != null ) {
//        if( currentPage != null ) {
            getCurrentPage().relayoutChildren();
        }
    }

    private Page getCurrentPage() {
        if( viewingPageIndex >= 0 && viewingPageIndex < pages.size() ) {
            return sectionAt( viewingPageIndex );
        }
        else {
            return null;
        }
    }

    private void testTopLeft( PNode node ) {
        if( node != null ) {
            topLeft( node );
        }
    }

    public void setHalosVisible( boolean halosVisible ) {
        this.halosVisible = halosVisible;
        if( halosVisible ) {
            addHalos();
        }
        else {
            removeHalos();
        }
    }

    private void removeHalos() {
        for( int i = 0; i < influenceGraphics.size(); i++ ) {
            ParticleInfluenceGraphic particleInfluenceGraphic = (ParticleInfluenceGraphic)influenceGraphics.get( i );
            influenceGraphics.remove( particleInfluenceGraphic );
            getUniverseGraphic().removeInfluenceGraphic( particleInfluenceGraphic );
            i--;
        }
    }

    public boolean isHalosVisible() {
        return halosVisible;
    }

    public PButton getPreviousButton() {
        return prevSwing;
    }

    public void teardown( TutorialApplication tutorialApplication ) {
        super.teardown( tutorialApplication );
//        stopModel();
        System.out.println( "Stopped model in " + this );
    }

    public TutorialApplication getTutorialApplication() {
        return tutorialApplication;
    }

    public void moveRight() {
        nextSection();
    }

    public void moveLeft() {
        previousSection();
    }


}
