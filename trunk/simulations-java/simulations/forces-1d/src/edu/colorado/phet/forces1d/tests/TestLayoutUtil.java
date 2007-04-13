package edu.colorado.phet.forces1d.tests;

import edu.colorado.phet.forces1d.common.LayoutUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 29, 2004
 * Time: 2:35:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestLayoutUtil {
    public static void main( String[] args ) {

        test1();
        test2();
        test3();
    }

    private static void test3() {
        System.out.println( "Test3" );
        LayoutUtil layoutUtil = new LayoutUtil( 0, 10, 2 );
        LayoutUtil.LayoutElement[] elm = new LayoutUtil.LayoutElement[]{new LayoutUtil.Dynamic(), new LayoutUtil.Dynamic()};
        layoutUtil.layout( elm );
        for( int i = 0; i < elm.length; i++ ) {
            LayoutUtil.LayoutElement layoutElement = elm[i];
            System.out.println( "layoutElement = " + layoutElement );
        }
    }

    private static void test2() {
        System.out.println( "Test2" );
        LayoutUtil layoutUtil = new LayoutUtil( -10, 10, 1 );
        LayoutUtil.LayoutElement[] elements = new LayoutUtil.LayoutElement[]{new LayoutUtil.Dynamic(), new LayoutUtil.Fixed( 5 ), new LayoutUtil.Dynamic()};
        layoutUtil.layout( elements );
        for( int i = 0; i < elements.length; i++ ) {
            LayoutUtil.LayoutElement element = elements[i];
            System.out.println( "element = " + element );
        }
    }

    private static void test1() {
        System.out.println( "Test1" );
        LayoutUtil layoutUtil = new LayoutUtil( 0, 10, 1 );//spacing of 1.0, 11 slots, one fills 3
        LayoutUtil.LayoutElement[] elements = new LayoutUtil.LayoutElement[]{new LayoutUtil.Fixed( 3 ), new LayoutUtil.Dynamic()};
        layoutUtil.layout( elements );
        for( int i = 0; i < elements.length; i++ ) {
            LayoutUtil.LayoutElement element = elements[i];
            System.out.println( "element = " + element );
        }
    }
}
