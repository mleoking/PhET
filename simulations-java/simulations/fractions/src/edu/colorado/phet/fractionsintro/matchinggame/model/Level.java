//package edu.colorado.phet.fractionsintro.matchinggame.model;
//
//import fj.F;
//import fj.data.List;
//import lombok.Data;
//
//import java.util.ArrayList;
//
//import edu.colorado.phet.fractionsintro.intro.model.Fraction;
//
///**
// * @author Sam Reid
// */
//public @Data class Level {
//    public final List<Fraction> fractions;
//    public final List<ShapeType> shapeTypes;
//    public final List<FillType> fillTypes;
//
//    public List<RepresentationType> toRepresentationList() {
//        return null;
//    }
//
//    private List<MovableFraction> newInstance( F<Fraction, ArrayList<RepresentationType>> representations, List<Cell> _cells, Fraction[] a ) {
//        for ( int i = 0; i < 100; i++ ) {
//            List<MovableFraction> fractions = createLevelInstance( representations, _cells, a );
//            if ( isOkay( fractions ) ) { return fractions; }
//        }
//        return createLevelInstance( representations, _cells, a );
//    }
//
//    //Validate the specified fractions, making sure there is a mixed batch of numbers/pictures
//    private boolean isOkay( final List<MovableFraction> fractions ) {
//        int numNumeric = fractions.filter( new F<MovableFraction, Boolean>() {
//            @Override public Boolean f( final MovableFraction movableFraction ) {
//                return movableFraction.representationName.equals( "numeric" );
//            }
//        } ).length();
//        boolean problematic = numNumeric == fractions.length() || numNumeric == 0;
//        return !problematic;
//    }
//}