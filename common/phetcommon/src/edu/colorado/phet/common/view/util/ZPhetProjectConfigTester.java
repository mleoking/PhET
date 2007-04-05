/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.view.util;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

/**
 * The test suite for the class:
 */
public class ZPhetProjectConfigTester extends MockObjectTestCase {
    private static final String TEST_DIRNAME = "dirname";
    private static final String TEST_FLAVOR  = "flavor";

    private volatile Mock mockStrings;
    private volatile Mock mockLoader;
    private volatile TestProjectConfig config, configF;

    public void setUp() throws Exception {
        mockStrings = mock( PhetPropertySource.class );
        mockLoader  = mock( PhetResourceLoader.class );
        config      = new TestProjectConfig( TEST_DIRNAME, null, Locale.getDefault() );
        configF     = new TestProjectConfig( TEST_DIRNAME, TEST_FLAVOR, Locale.getDefault() );
    }

    public void testSetup() {
        assertFalse( config.isFlavored() );
        assertTrue( configF.isFlavored() );
    }

    public void testGetDataRoot() {
        assertEquals( "dirname/", config.getDataRoot() );
    }

    public void testGetDirname() {
        assertEquals( "dirname", config.getDirname() );
    }

    public void testGetFlavor() {
        assertEquals( "dirname", config.getFlavor() );
        assertEquals( "flavor", configF.getFlavor() );
    }

    public void testGetNameForUnflavored() {
        String projectName = "My Project Name";

        mockStrings.expects( once() ).method( "getString" ).with( eq( "dirname.name" ) ).will( returnValue( projectName ) );

        assertEquals( projectName, config.getName() );
    }

    public void testGetNameForFlavored() {
        String projectName = "My Project Name";

        mockStrings.expects( once() ).method( "getString" ).with( eq( "dirname-flavor.name" ) ).will( returnValue( projectName ) );

        assertEquals( projectName, configF.getName() );
    }

    public void testGetDescriptionForUnflavored() {
        String projectDesc = "My Description";

        mockStrings.expects( once() ).method( "getString" ).with( eq( "dirname.description" ) ).will( returnValue( projectDesc ) );

        assertEquals( projectDesc, config.getDescription() );
    }

    public void testGetDescriptionForFlavored() {
        String projectDesc = "My Description";

        mockStrings.expects( once() ).method( "getString" ).with( eq( "dirname-flavor.description" ) ).will( returnValue( projectDesc ) );

        assertEquals( projectDesc, configF.getDescription() );
    }

    public void testGetProperties() throws Exception {
        Properties properties = new Properties();

        properties.put( "key", "value" );

        expectProperties( properties );

        assertEquals( "value", config.getProperties().get( "key" ) );
    }

    public void testGetVersion() throws Exception {
        Properties properties = new Properties();

        properties.put( "version.major",    "1" );
        properties.put( "version.minor",    "2" );
        properties.put( "version.dev",      "03" );
        properties.put( "version.revision", "1234" );

        expectProperties( properties );

        assertEquals( "1.2.03 (1234)", config.getVersion().toString() );
    }

    public void testGetImage() throws Exception {
        PhetProjectConfig commonConfig = PhetProjectConfig.forProject( "phetcommon" );

        BufferedImage image = commonConfig.getImage( "test-image.jpg" );

        assertEquals( 120, image.getWidth() );
        assertEquals( 50, image.getHeight() );        
    }

    private class TestProjectConfig extends PhetProjectConfig {
        protected TestProjectConfig( String dirname, String flavor, Locale locale ) {
            super( dirname, flavor, locale, (PhetPropertySource)mockStrings.proxy(), (PhetResourceLoader)mockLoader.proxy() );
        }
    }

    private void expectProperties( Properties properties ) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        properties.store( out, "Property test" );

        mockLoader.expects( once() ).method( "getResourceAsStream" ).with( eq( "dirname/dirname.properties" ) ).will( returnValue( new ByteArrayInputStream( out.toByteArray() ) ) );
    }
}