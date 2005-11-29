/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -----------------------
 * DataXYPackageTests.java
 * -----------------------
 * (C) Copyright 2004, 2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 23-Aug-2004 : Restructured org.jfree.data package (DG);
 * 18-Jan-2005 : Added main() method (DG);
 * 21-Jan-2005 : Added IntervalXYDelegateTests (DG);
 * 29-Apr-2005 : Added DefaultOHLCDatasetTests and OHLCDataItemTests (DG);
 * 06-Oct-2005 : Added CategoryTableXYDatasetTests (DG);
 *
 */

package org.jfree.data.xy.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Some tests for the <code>org.jfree.data.category</code> package that can
 * be run using JUnit.  You can find more information about JUnit at 
 * <a href="http://www.junit.org">http://www.junit.org</a>.
 */
public class DataXYPackageTests extends TestCase {

    /**
     * Returns a test suite to the JUnit test runner.
     *
     * @return The test suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.data.xy");
        suite.addTestSuite(CategoryTableXYDatasetTests.class);
        suite.addTestSuite(DefaultOHLCDatasetTests.class);
        suite.addTestSuite(DefaultTableXYDatasetTests.class);
        suite.addTestSuite(IntervalXYDelegateTests.class);
        suite.addTestSuite(MatrixSeriesTests.class);
        suite.addTestSuite(OHLCDataItemTests.class);
        suite.addTestSuite(TableXYDatasetTests.class);
        suite.addTestSuite(XYDataItemTests.class);
        suite.addTestSuite(XYSeriesCollectionTests.class);
        suite.addTestSuite(XYSeriesTests.class);
        return suite;
    }

    /**
     * Constructs the test suite.
     *
     * @param name  the test suite name.
     */
    public DataXYPackageTests(String name) {
        super(name);
    }
    
    /**
     * Runs the test suite using JUnit's text-based runner.
     * 
     * @param args  ignored.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
