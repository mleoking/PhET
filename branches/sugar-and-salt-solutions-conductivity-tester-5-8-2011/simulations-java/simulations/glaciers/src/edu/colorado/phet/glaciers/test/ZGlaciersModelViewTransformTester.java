// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.glaciers.test;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import junit.framework.TestCase;
import edu.colorado.phet.glaciers.view.GlaciersModelViewTransform;

/**
 * ZModelViewTransformTester is the JUnit test for ModelViewTransform.
 * Tests a representative sample of common transforms, but is in no way complete.
 * Correct answers were calculated manually.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ZGlaciersModelViewTransformTester extends TestCase {
    
    //----------------------------------------------------------------------------
    // Fixtures
    //----------------------------------------------------------------------------
    
    private GlaciersModelViewTransform _mvtIdentity; // identity transform
    private GlaciersModelViewTransform _mvtPositiveScale; // positive scaling
    private GlaciersModelViewTransform _mvtNegativeScale; // negative scaling
    private GlaciersModelViewTransform _mvtPositiveTranslation; // positive translation
    private GlaciersModelViewTransform _mvtNegativeTranslation; // negative translation
    private GlaciersModelViewTransform _mvtReflection; // reflection transform
    private GlaciersModelViewTransform _mvtFlipSign; // flip sign of x,y coordinates
    private GlaciersModelViewTransform _mvtCombination;
    
    //----------------------------------------------------------------------------
    // setup
    //----------------------------------------------------------------------------
    
    public void setUp() {
        _mvtIdentity = new GlaciersModelViewTransform( 1, 1, 0, 0 );
        _mvtPositiveScale = new GlaciersModelViewTransform( 2, 3, 0, 0 );
        _mvtNegativeScale = new GlaciersModelViewTransform( -2, -3, 0, 0 );
        _mvtPositiveTranslation = new GlaciersModelViewTransform( 1, 1, 10, 20 );
        _mvtNegativeTranslation = new GlaciersModelViewTransform( 1, 1, -10, -20 );
        _mvtReflection = new GlaciersModelViewTransform( 1, -1, 0, 0 );
        _mvtFlipSign = new GlaciersModelViewTransform( 1, 1, 10, 20, true, true );
        _mvtCombination = new GlaciersModelViewTransform( 2, -3, 10, 20 );
    }
    
    //----------------------------------------------------------------------------
    // Identity transform
    //----------------------------------------------------------------------------
    
    public void testIdentityTransform_ModelToView_Point() {
        Point2D pModel = new Point2D.Double( 10, 20 );
        Point2D pView = _mvtIdentity.modelToView( pModel );
        assertTrue( pView.equals( pModel ) );
    }
    
    public void testIdentityTransform_ModelToView_Rectangle() {
        Rectangle2D rModel = new Rectangle2D.Double( 100, 200, 300, 400 );
        Rectangle2D rView = _mvtIdentity.modelToView( rModel );
        assertTrue( rView.equals( rModel ) );
    }
    
    public void testIdentityTransform_ViewToModel_Point() {
        Point2D pView = new Point2D.Double( 10, 20 );
        Point2D pModel = _mvtIdentity.viewToModel( pView );
        assertTrue( pModel.equals( pView ) );
    }
    
    public void testIdentityTransform_ViewToModel_Rectangle() {
        Rectangle2D rView = new Rectangle2D.Double( 100, 200, 300, 400 );
        Rectangle2D rModel = _mvtIdentity.viewToModel( rView );
        assertTrue( rModel.equals( rView ) );
    }

    //----------------------------------------------------------------------------
    // Positive scaling
    //----------------------------------------------------------------------------
    
    public void testPositiveScale_ModelToView_Point() {
        Point2D pModel = new Point2D.Double( 10, 20 );
        Point2D pView = _mvtPositiveScale.modelToView( pModel );
        Point2D pCorrect = new Point2D.Double( 20, 60 ); // 2,3
        assertTrue( pView.equals( pCorrect ) );
    }
    
    public void testPositiveScale_ModelToView_Rectangle() {
        Rectangle2D rModel = new Rectangle2D.Double( 100, 200, 300, 400 );
        Rectangle2D rView = _mvtPositiveScale.modelToView( rModel );
        Rectangle2D rCorrect = new Rectangle2D.Double( 200, 600, 600, 1200 ); // 2,3
        assertTrue( rView.equals( rCorrect ) );
    }
    
    public void testPositiveScale_ViewToModel_Point() {
        Point2D pView = new Point2D.Double( 20, 60 );
        Point2D pModel = _mvtPositiveScale.viewToModel( pView );
        Point2D pCorrect = new Point2D.Double( 10, 20 ); // 1/2, 1/3
        assertTrue( pModel.equals( pCorrect ) );
    }
    
    public void testPositiveScale_ViewToModel_Rectangle() {
        Rectangle2D rView = new Rectangle2D.Double( 200, 600, 600, 1200 );
        Rectangle2D rModel = _mvtPositiveScale.viewToModel( rView );
        Rectangle2D rCorrect = new Rectangle2D.Double( 100, 200, 300, 400 ); // 1/2, 1/3
        assertTrue( rModel.equals( rCorrect ) );
    }
    
    //----------------------------------------------------------------------------
    // Negative scaling
    //----------------------------------------------------------------------------
    
    public void testNegativeScale_ModelToView_Point() {
        Point2D pModel = new Point2D.Double( 10, 20 );
        Point2D pView = _mvtNegativeScale.modelToView( pModel );
        Point2D pCorrect = new Point2D.Double( -20, -60 );  // -2,-3
        assertTrue( pView.equals( pCorrect ) );
    }
    
    public void testNegativeScale_ModelToView_Rectangle() {
        Rectangle2D rModel = new Rectangle2D.Double( 100, 200, 300, 400 );
        Rectangle2D rView = _mvtNegativeScale.modelToView( rModel );
        Rectangle2D rCorrect = new Rectangle2D.Double( -800, -1800, 600, 1200 ); // -2,-3
        assertTrue( rView.equals( rCorrect ) );
    }
    
    public void testNegativeScale_ViewToModel_Point() {
        Point2D pView = new Point2D.Double( -20, -60 );
        Point2D pModel = _mvtNegativeScale.viewToModel( pView );
        Point2D pCorrect = new Point2D.Double( 10, 20 ); // -1/2, -1/3
        assertTrue( pModel.equals( pCorrect ) );
    }
    
    public void testNegativeScale_ViewToModel_Rectangle() {
        Rectangle2D rView = new Rectangle2D.Double( -800, -1800, 600, 1200 );
        Rectangle2D rModel = _mvtNegativeScale.viewToModel( rView );
        Rectangle2D rCorrect = new Rectangle2D.Double( 100, 200, 300, 400 ); // -1/2, -1/3
        assertTrue( rModel.equals( rCorrect ) );
    }
    
    //----------------------------------------------------------------------------
    // Positive translation
    //----------------------------------------------------------------------------
    
    public void testPositiveTranslation_ModelToView_Point() {
        Point2D pModel = new Point2D.Double( 10, 20 );
        Point2D pView = _mvtPositiveTranslation.modelToView( pModel );
        Point2D pCorrect = new Point2D.Double( 20, 40 ); // 10,20
        assertTrue( pView.equals( pCorrect ) );
    }
    
    public void testPositiveTranslation_ModelToView_Rectangle() {
        Rectangle2D rModel = new Rectangle2D.Double( 100, 200, 300, 400 );
        Rectangle2D rView = _mvtPositiveTranslation.modelToView( rModel );
        Rectangle2D rCorrect = new Rectangle2D.Double( 110, 220, 300, 400 ); // 10,20
        assertTrue( rView.equals( rCorrect ) );
    }
    
    public void testPositiveTranslation_ViewToModel_Point() {
        Point2D pView = new Point2D.Double( 20, 40 );
        Point2D pModel = _mvtPositiveTranslation.viewToModel( pView );
        Point2D pCorrect = new Point2D.Double( 10, 20 ); // -10,-20
        assertTrue( pModel.equals( pCorrect ) );
    }
    
    public void testPositiveTranslation_ViewToModel_Rectangle() {
        Rectangle2D rView = new Rectangle2D.Double( 110, 220, 300, 400 );
        Rectangle2D rModel = _mvtPositiveTranslation.viewToModel( rView );
        Rectangle2D rCorrect = new Rectangle2D.Double( 100, 200, 300, 400 ); // -10,-20
        assertTrue( rModel.equals( rCorrect ) );
    }
    
    //----------------------------------------------------------------------------
    // Negative translation
    //----------------------------------------------------------------------------
    
    public void testNegativeTranslation_ModelToView_Point() {
        Point2D pModel = new Point2D.Double( 10, 20 );
        Point2D pView = _mvtNegativeTranslation.modelToView( pModel );
        Point2D pCorrect = new Point2D.Double( 0, 0 ); // -10, -20
        assertTrue( pView.equals( pCorrect ) );
    }
    
    public void testNegativeTranslation_ModelToView_Rectangle() {
        Rectangle2D rModel = new Rectangle2D.Double( 100, 200, 300, 400 );
        Rectangle2D rView = _mvtNegativeTranslation.modelToView( rModel );
        Rectangle2D rCorrect = new Rectangle2D.Double( 90, 180, 300, 400 ); // -10,-20
        assertTrue( rView.equals( rCorrect ) );
    }
    
    public void testNegativeTranslation_ViewToModel_Point() {
        Point2D pView = new Point2D.Double( 0, 0 );
        Point2D pModel = _mvtNegativeTranslation.viewToModel( pView );
        Point2D pCorrect = new Point2D.Double( 10, 20 ); // 10,20
        assertTrue( pModel.equals( pCorrect ) );
    }
    
    public void testNegativeTranslation_ViewToModel_Rectangle() {
        Rectangle2D rView = new Rectangle2D.Double( 90, 180, 300, 400 );
        Rectangle2D rModel = _mvtNegativeTranslation.viewToModel( rView );
        Rectangle2D rCorrect = new Rectangle2D.Double( 100, 200, 300, 400 ); // 10.,20
        assertTrue( rModel.equals( rCorrect ) );
    }
    
    //----------------------------------------------------------------------------
    // Reflection about Y axis
    //----------------------------------------------------------------------------
    
    public void testReflection_ModelToView_Point() {
        Point2D pModel = new Point2D.Double( 10, 20 );
        Point2D pView = _mvtReflection.modelToView( pModel );
        Point2D pCorrect = new Point2D.Double( 10, -20 ); // 1,-1,0,0
        assertTrue( pView.equals( pCorrect ) );
    }
    
    public void testReflection_ModelToView_Rectangle() {
        Rectangle2D rModel = new Rectangle2D.Double( 0, 0, 100, 200 );
        Rectangle2D rView = _mvtReflection.modelToView( rModel );
        Rectangle2D rCorrect = new Rectangle2D.Double( 0, -200, 100, 200 ); // 1,-1,0,0
        assertTrue( rView.equals( rCorrect ) );
    }
    
    public void testReflection_ViewToModel_Point() {
        Point2D pView = new Point2D.Double( 10, -20 );
        Point2D pModel = _mvtReflection.viewToModel( pView );
        Point2D pCorrect = new Point2D.Double( 10, 20 ); // 1,-1,0,0
        assertTrue( pModel.equals( pCorrect ) );
    }
    
    public void testReflection_ViewToModel_Rectangle() {
        Rectangle2D rView = new Rectangle2D.Double( 0, -200, 100, 200 );
        Rectangle2D rModel = _mvtReflection.viewToModel( rView );
        Rectangle2D rCorrect = new Rectangle2D.Double( 0, 0, 100, 200 ); // 1,-1,0,0
        assertTrue( rModel.equals( rCorrect ) );
    }
    
    //----------------------------------------------------------------------------
    // Flip sign of x,y coordinates
    //----------------------------------------------------------------------------
    
    public void testFlipSign_ModelToView_Point() {
        Point2D pModel = new Point2D.Double( 10, 20 );
        Point2D pView = _mvtFlipSign.modelToView( pModel );
        Point2D pCorrect = new Point2D.Double( -20, -40 ); // 10,20,true,true
        assertTrue( pView.equals( pCorrect ) );
    }
    
    public void testFlipSign_ModelToView_Rectangle() {
        Rectangle2D rModel = new Rectangle2D.Double( 100, 200, 300, 400 );
        Rectangle2D rView = _mvtFlipSign.modelToView( rModel );
        Rectangle2D rCorrect = new Rectangle2D.Double( -110, -220, 300, 400 ); // 10,20,true,true
        assertTrue( rView.equals( rCorrect ) );
    }
    
    public void testFlipSign_ViewToModel_Point() {
        Point2D pView = new Point2D.Double( -20, -40 );
        Point2D pModel = _mvtFlipSign.viewToModel( pView );
        Point2D pCorrect = new Point2D.Double( 30, 60 ); // -10,-20,true,true
        assertTrue( pModel.equals( pCorrect ) );
    }
    
    public void testFlipSign_ViewToModel_Rectangle() {
        Rectangle2D rView = new Rectangle2D.Double( -110, -220, 300, 400 );
        Rectangle2D rModel = _mvtFlipSign.viewToModel( rView );
        Rectangle2D rCorrect = new Rectangle2D.Double( 120, 240, 300, 400 ); // -10,-20,true,true
        assertTrue( rModel.equals( rCorrect ) );
    }
    
    //----------------------------------------------------------------------------
    // Combined scale and translation
    //----------------------------------------------------------------------------
    
    public void testCombination_ModelToView_Point() {
        Point2D pModel = new Point2D.Double( 10, 20 );
        Point2D pView = _mvtCombination.modelToView( pModel );
        Point2D pCorrect = new Point2D.Double( 40, -120 ); // 2,-3,10,20
        assertTrue( pView.equals( pCorrect ) );
    }
    
    public void testCombination_ModelToView_Rectangle() {
        Rectangle2D rModel = new Rectangle2D.Double( 100, 200, 300, 400 );
        Rectangle2D rView = _mvtCombination.modelToView( rModel );
        Rectangle2D rCorrect = new Rectangle2D.Double( 220, -1860, 600, 1200 ); // 2,-3,10,20
        assertTrue( rView.equals( rCorrect ) );
    }
    
    public void testCombination_ViewToModel_Point() {
        Point2D pView = new Point2D.Double( 40, -120 );
        Point2D pModel = _mvtCombination.viewToModel( pView );
        Point2D pCorrect = new Point2D.Double( 10, 20 ); // 1/2,-1/3,-10,-20
        assertTrue( pModel.equals( pCorrect ) );
    }
    
    public void testCombination_ViewToModel_Rectangle() {
        Rectangle2D rView = new Rectangle2D.Double( 220, -1860, 600, 1200 );
        Rectangle2D rModel = _mvtCombination.viewToModel( rView );
        Rectangle2D rCorrect = new Rectangle2D.Double( 100, 200, 300, 400 ); // 1/2,-1/3,-10,-20
        assertTrue( rModel.equals( rCorrect ) );
    }
}
