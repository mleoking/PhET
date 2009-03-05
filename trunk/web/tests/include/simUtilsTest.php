<?php

require_once('PHPUnit/Extensions/Database/TestCase.php');

// SITE_ROOT - Relative path to website's top directory from the tested file
if (!defined('SITE_ROOT')) define('SITE_ROOT', '../');

// Get the test globals to set everything up
require_once(dirname(dirname(__FILE__)).'/test_global.php');

// Get the file to test
require_once("include/sim-utils.php");

    /*
     Notes:
     Choosing which simulations to make certain errors encountered is tricky.
     Here are some notes to help me remember everything.

     The database holds a minimal set of sims, I add them as I need
     them.  They are in the database as extracted from the real data
     at that time, I don't update them over time (unless there is some
     major change, like the database schema changes.

     Any data I make up I will describe below.

     There are usually some convenience functions to retrieve the sims
     from the database.

     There are 2 sims where everything is "right", 1 for each type.
     They were chosen because they both contain at least 1 fake
     language code that maps to a full locale. (e.g. 'bp' => 'pt_BR')'
       Java: Balloons and Static Electricity, id=81
       Flash: Masses & Springs, id=106

     There was a problem with Flash translations showing up when all
     the information wasn't there.  So that sim with that problem is
     replicated here.
       Flash: Blackbody Spectrum, id=83

    */

class simUtilsTest extends PHPUnit_Extensions_Database_TestCase {

    // Name of the data file that populates the database before each
    // test is run
    const db_data_file = 'simUtils.xml';

    // PDO object pointing to the test database
    protected static $pdo = NULL;

    public function __construct() {
        if (is_null(self::$pdo)) {
            self::$pdo = PHPUnit_Util_PDO::factory("mysql://phet_test@localhost/phet_test");
        }

        // Where to find the files
        $this->data_dir = 
            dirname(dirname(__FILE__)).DIRECTORY_SEPARATOR.
            "_files".DIRECTORY_SEPARATOR.
            'database'.DIRECTORY_SEPARATOR.
            'data'.DIRECTORY_SEPARATOR;
   }

    public function __destruct() {
        self::$pdo = NULL;
    }

    protected function getConnection() {
        return $this->createDefaultDBConnection(self::$pdo, 'phet_test');
    }

    protected function getDataSet() {
        return $this->createXMLDataSet($this->data_dir.self::db_data_file);
    }

    protected function getJavaSimArray() {
        // Balloons and Static Electricity, id=81
        return sim_get_sim_by_id(81);
    }

    protected function getFlashSimArray() {
        // Masses & Springs, id=106
        return sim_get_sim_by_id(106);
    }

    /**
     *
     * Testing sim_get_root()
     *
     */

    public function testSimGetRoot_getWithoutSetReturnsDefinedRoot() {
        $root = sim_get_root();
        $this->assertEquals(SIMS_ROOT, $root);
    }

    /**
     *
     * Testing sim_set_root()
     *
     */

    public function testSimSetRoot_newValueChangesRoot() {
        $new_root = 'harray';
        sim_set_root($new_root);
        $this->assertEquals($new_root, sim_get_root());
    }

    public function testSimSetRoot_emptyOrNontrueRootChangesToDefinedRoot() {
        $new_root = 'harray';
        sim_set_root($new_root);
        $this->assertEquals($new_root, sim_get_root());
        sim_set_root('');
        $this->assertEquals(SIMS_ROOT, sim_get_root() );
    }

    /**
     *
     * Testing get_sorting_name()
     *
     */
    
    public function testGetSortingName_returnsExpected() {
        // Pairs: test sorting name => processed sorting n ame
        $pairs = array(
            // Returns OK
            "Quick Brown Fox" => "Quick Brown Fox",

            // 'The' stripped regardless of leading/trailing space and caps, and NOT
            // stripped when part of a longer word
            "The Quick Brown Fox" => "Quick Brown Fox",
            "  The Quick Brown Fox" => "Quick Brown Fox",
            "  The   Quick Brown Fox" => "Quick Brown Fox",
            "the Quick Brown Fox" => "Quick Brown Fox",
            "  the Quick Brown Fox" => "Quick Brown Fox",
            "  the   Quick Brown Fox" => "Quick Brown Fox",
            "There Is The Quick Brown Fox" => "There Is The Quick Brown Fox",
            "   There Is The Quick Brown Fox" => "There Is The Quick Brown Fox",
            "   There   Is The Quick Brown Fox" => "There   Is The Quick Brown Fox",

            // 'A' stripped regardless of leading/trailing space and caps, and NOT
            // stripped when part of a longer word
            "A Pound of Prevention" => "Pound of Prevention",
            "   A Pound of Prevention" => "Pound of Prevention",
            "   A    Pound of Prevention" => "Pound of Prevention",
            "a Pound of Prevention" => "Pound of Prevention",
            "   a Pound of Prevention" => "Pound of Prevention",
            "   a    Pound of Prevention" => "Pound of Prevention",
            "Add a Pound of Prevention" => "Add a Pound of Prevention",
            "   Add a Pound of Prevention" => "Add a Pound of Prevention",
            "   Add   a Pound of Prevention" => "Add   a Pound of Prevention",

            // 'An' stripped regardless of leading/trailing space and caps
            "An Ounce of Cure" => "Ounce of Cure",
            "   An Ounce of Cure" => "Ounce of Cure",
            "   An    Ounce of Cure" => "Ounce of Cure",
            "an Ounce of Cure" => "Ounce of Cure",
            "   an Ounce of Cure" => "Ounce of Cure",
            "   an    Ounce of Cure" => "Ounce of Cure",
            "Add an Ounce of Cure" => "Add an Ounce of Cure",
            "   Add an Ounce of Cure" => "Add an Ounce of Cure",
            "   Add   an Ounce of Cure" => "Add   an Ounce of Cure",
            );
        
        foreach ($pairs as $input => $expected) {
            $this->assertEquals($expected, get_sorting_name($input));
        }
    }

    /**
     *
     * Testing sim_get_all_sim_translations()
     *
     */

    /**
     *
     * Testing sim_get_all_sim_translations()
     *
     */
    
    public function testSimGetAllSimTranslations_returnsArrayWhenSimsExist() {
        $translations = sim_get_all_sim_translations();
        $this->assertType('array', $translations);
    }
    
    public function testSimGetAllSimTranslations_keysAreLocales() {
        $translations = sim_get_all_sim_translations();
        foreach ($translations as $locale => $sim_list) {
            $this->assertTrue(Locale::inst()->isValid($locale));
        }
    }
    
    public function testSimGetAllSimTranslations_valuesAreArrays() {
        $translations = sim_get_all_sim_translations();
        foreach ($translations as $locale => $sim_list) {
            $this->assertType('array', $sim_list);
        }
    }
    
    public function testSimGetAllSimTranslations_valuesAreSimIds() {
        $translations = sim_get_all_sim_translations();
        foreach ($translations as $locale => $sim_list) {
            foreach ($sim_list as $sim_id) {
                $sim = sim_get_sim_by_id($sim_id);
                $this->assertNotEquals(false, $sim);
            }
        }
    }
    
    public function testSimGetAllSimTranslations_returnsArrayWhenSimsDoNotExist() {
        $result = mysql_query("DELETE FROM simulation");
        $this->assertTrue($result);

        $translations = sim_get_all_sim_translations();
        $this->assertType('array', $translations);
    }

    public function testSimGetAllSimTranslations_returnsEmptyArrayWhenSimsDoNotExist() {
        $result = mysql_query("DELETE FROM simulation");
        $this->assertTrue($result);

        $translations = sim_get_all_sim_translations();
        $this->assertTrue(empty($translations));
    }

    public function testSimGetAllSimTranslations_verifyResults() {
        // TODO: Get original sim dirs and cull out bad stuff
        // FORNOW: skip this test
        return;

        $translations = sim_get_all_sim_translations();
        $sims = sim_get_all_sims();
        $all_files = array();
        $jnlp_files = glob(SIMS_ROOT."*/*.jnlp");
        $flash_files = glob(SIMS_ROOT."*/*.html");
        $all_files = array_merge($jnlp_files, $flash_files);
        sort($all_files);
        $got_files = array();
        foreach ($translations as $locale => $sim_list) {
            foreach ($sim_list as $sim_id) {
                $sim = $sims[$sim_id];
                $ext = ($sim['sim_type'] == SIM_TYPE_JAVA) ? 'jnlp' : 'html';
                // Grab the directory listing
                if ((($locale == 'en') || ($locale == 'en_US')) && ($sim['sim_type'] == SIM_TYPE_JAVA)) {
                    $file = SIMS_ROOT."{$sim['sim_dirname']}/{$sim['sim_flavorname']}.{$ext}";
                }
                else {
                    $file = SIMS_ROOT."{$sim['sim_dirname']}/{$sim['sim_flavorname']}_{$locale}.{$ext}";
                }
                if (!in_array($file, $got_files)) {
                    $got_files[] = $file;
                }
            }
        }
        sort($got_files);
        $this->assertEquals($all_files, $got_files);
    }

    /**
     *
     * Testing sim_get_translations()
     *
     */

    public function testSimGetTranslations_javaSimReturnsArrayOfExpetedLocales() {
        $sim = $this->getJavaSimArray();

        $expected_locales = array('bp', 'cs', 'de', 'el', 'es', 'et', 'fi', 'fr', 'ga', 'in', 'it', 'iw', 'nl', 'pt', 'ru', 'sk', 'sl', 'tr', 'uk');        
        usort($expected_locales, 'locale_sort_code_by_name');

        $result = sim_get_translations($sim);
        $this->assertEquals($expected_locales, $result);
    }

    public function testSimGetTranslations_javaSimReturnsArrayOfValidLocales() {
        $sim = $this->getJavaSimArray();
        $result = sim_get_translations($sim);
        foreach ($result as $locale) {
            $this->assertTrue(Locale::inst()->isValid($locale));
        }
    }

    public function testSimGetTranslations_flashSimReturnsArrayOfExpetedLocales() {
        $sim = $this->getFlashSimArray();

        $expected_locales = array('es', 'it', 'nl', 'sk', 'tc', 'sl');
        usort($expected_locales, 'locale_sort_code_by_name');
        
        $result = sim_get_translations($sim);
        $this->assertEquals($expected_locales, $result);
    }

    public function testSimGetTranslations_flashSimReturnsArrayOfValidLocales() {
        $sim = $this->getFlashSimArray();
        $result = sim_get_translations($sim);
        foreach ($result as $locale) {
            $this->assertTrue(Locale::inst()->isValid($locale));
        }
    }

    /**
     *
     * Testing sim_get_version()
     *
     */

    function testSimGetVersion_propertiesNotFoundReturnsEmpty() {
        // Process the expected data for easy comparison
        $sim = $this->getJavaSimArray();
        $expected = array(
            'major' => '',
            'minor' => '',
            'dev' => '',
            'revision' => '',
            'timestamp' => '',
            'installer_timestamp' => '1233709400'
            );
        $expected_keys = array_keys($expected);
        sort($expected_keys);
        $expected_values = array_values($expected);
        sort($expected_values);

        // Give the wrong sims directory so the .properties file can't be found
        sim_set_root('./harray');

        // Get the version
        $version = sim_get_version($sim);

        // Process the version data for easy comparison
        $version_keys = array_keys($version);
        sort($version_keys);
        $version_values = array_values($version);
        sort($version_values);

        // Compare the expected vs real
        $this->assertEquals($expected_keys, $version_keys);
        $this->assertEquals($expected_values, $version_values);
    }

    function testSimGetVersion_javaSimRetrunsExpected() {
        // Process the expected data for easy comparison
        $sim = $this->getJavaSimArray();
        $expected = array(
            'major' => '1',
            'minor' => '08',
            'dev' => '00',
            'revision' => '28720',
            'timestamp' => '1235020762',
            'installer_timestamp' => '1233709400'
            );
        $expected_keys = array_keys($expected);
        sort($expected_keys);
        $expected_values = array_values($expected);
        sort($expected_values);

        // Get the version
        $version = sim_get_version($sim);

        // Process the version data for easy comparison
        $version_keys = array_keys($version);
        sort($version_keys);
        $version_values = array_values($version);
        sort($version_values);

        // Compare the expected vs real
        $this->assertEquals($expected_keys, $version_keys);
        $this->assertEquals($expected_values, $version_values);
    }

    function testSimGetVersion_flashSimReturnsEmptyByDefault() {
        // Process the expected data for easy comparison
        $sim = $this->getFlashSimArray();
        $expected = array(
            'major' => '',
            'minor' => '',
            'dev' => '',
            'revision' => '',
            'timestamp' => '',
            'installer_timestamp' => '1233709400'
            );
        $expected_keys = array_keys($expected);
        sort($expected_keys);
        $expected_values = array_values($expected);
        sort($expected_values);

        // Get the version
        $version = sim_get_version($sim);

        // Process the version data for easy comparison
        $version_keys = array_keys($version);
        sort($version_keys);
        $version_values = array_values($version);
        sort($version_values);

        // Compare the expected vs real
        $this->assertEquals($expected_keys, $version_keys);
        $this->assertEquals($expected_values, $version_values);
    }

    function testSimGetVersion_flashSimReturnsExpectedIfRequested() {
        // Process the expected data for easy comparison
        $sim = $this->getFlashSimArray();
        $expected = array(
            'major' => '1',
            'minor' => '00',
            'dev' => '00',
            'revision' => '22498',
            'timestamp' => '',
            'installer_timestamp' => '1233709400'
            );
        $expected_keys = array_keys($expected);
        sort($expected_keys);
        $expected_values = array_values($expected);
        sort($expected_values);

        // Get the version
        $version = sim_get_version($sim, false);

        // Process the version data for easy comparison
        $version_keys = array_keys($version);
        sort($version_keys);
        $version_values = array_values($version);
        sort($version_values);

        // Compare the expected vs real
        $this->assertEquals($expected_keys, $version_keys);
        $this->assertEquals($expected_values, $version_values);
    }

    /**
     *
     * Testing sim_get_encoded_default_category()
     *
     */

    public function testSimGetEncodedDefaultCategory_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_categories()
     *
     */

    public function testSimGetCategories_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_sim_encoding_by_sim_id()
     *
     */

    public function testSimGetSimEncodingBySimId_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_sim_by_sim_encoding()
     *
     */

    public function testSimGetSimBySimEncoding_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_sim_id_by_sim_encoding()
     *
     */

    public function testSimGetSimIdBySimEncoding_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_url_to_sim_page_by_sim_name()
     *
     */

    public function testSimGetUrlToSimPageBySimName_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_url_to_sim_page()
     *
     */

    public function testSimGetUrlToSimPage_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_link_to_sim_page()
     *
     */

    public function testSimGetLinkToSimPage_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_link_to_sim_page_by_name()
     *
     */

    public function testSimGetLinkToSimPageByName_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_visible_categories()
     *
     */

    public function testSimGetVisibleCategories_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_category_names()
     *
     */

    public function testSimGetCategoryNames_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_visible_category_names()
     *
     */

    public function testSimGetVisibleCategoryNames_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_category_link_by_cat_encoding()
     *
     */

    public function testSimGetCategoryLinkByCatEncoding_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_category_url_by_cat_id()
     *
     */

    public function testSimGetCategoryUrlByCatId_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_category_link_by_cat_id()
     *
     */

    public function testSimGetCategoryLinkByCatId_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_sim_by_name()
     *
     */

    public function testSimGetSimByName_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_sim_by_id()
     *
     */

    public function testSimGetSimById_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_sim_by_dirname_flavorname()
     *
     */

    public function testSimGetSimByDirnameFlavorname_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_update_sim()
     *
     */

    public function testSimUpdateSim_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_search_for_sims()
     *
     */

    public function testSimSearchForSims_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_compare_by_sorting_name()
     *
     */

    public function testSimCompareBySortingName_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_all_sims()
     *
     */

    public function testSimGetAllSims_returnsArrayWhenSimsExist() {
        $sims = sim_get_all_sims();
        $this->assertType('array', $sims);
    }
    
    public function testSimGetAllSims_returnsArrayWhenSimsDoNotExist() {
        $result = mysql_query("DELETE FROM `simulation`");
        $this->assertTrue($result);

        $sims = sim_get_all_sims();
        $this->assertType('array', $sims);
    }
    
    public function testSimGetAllSims_keysAreSimIds() {
        $sims = sim_get_all_sims();
        foreach ($sims as $sim_id => $sim) {
            $this->assertEquals($sim_id, $sim['sim_id']);
            $this->assertNotEquals(false, mysql_query("SELECT * FROM `simulation` WHERE `sim_id`={$sim_id}"));
        }
    }
    
    public function testSimGetAllSims_arraySortedBySortingName() {
        $original_sim_order = array();

        // Get the sim sorting names in the order returned
        $sims = sim_get_all_sims();
        foreach ($sims as $sim) {
            $original_sim_order[] = $sim['sim_sorting_name'];
        }

        // Sort them
        $sorted_sim_order = $original_sim_order;
        sort($sorted_sim_order);

        // Compare the sorted results
        $this->assertEquals($sorted_sim_order, $original_sim_order);
    }
    
    public function testSimGetAllSims_simNamesAreNotReformatted() {
        $sims = sim_get_all_sims();
        foreach ($sims as $sim) {
            if (false !== strpos($sim['sim_name'], '&')) {
                $this->assertFalse(strpos($sim['sim_name'], '&amp;'));
            }
        }
    }

    /**
     *
     * Testing sim_get_name_to_sim_map()
     *
     */

    public function testSimGetNameToSimMap_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_sims_by_cat_id()
     *
     */

    public function testSimGetSimsByCatId_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_sims_by_cat_id_alphabetically()
     *
     */

    public function testSimGetSimsByCatIdAlphabetically_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_cat_by_cat_encoding()
     *
     */

    public function testSimGetCatByCatEncoding_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_cat_id_by_cat_encoding()
     *
     */

    public function testSimGetCatIdByCatEncoding_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_all_sim_names()
     *
     */

    public function testSimGetAllSimNames_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_auto_calc_sim_size()
     *
     */

    public function testSimAutoCalcSimSize_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_sim_listing()
     *
     */

    public function testSimGetSimListing_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_sim_listings_by_cat_id()
     *
     */

    public function testSimGetSimListingsByCatId_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_download()
     *
     */

    public function testSimGetDownload_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_is_in_category()
     *
     */

    public function testSimIsInCategory_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_launch_url()
     *
     */

    public function testSimGetLaunchUrl_invalidLocaleReturnsEmptyString() {
        $sim = $this->getJavaSimArray();
        $locale = 'foo_BAR';
        $expected_value = '';
        $launch_url = sim_get_launch_url($sim, $locale);
        $this->assertEquals($expected_value, $launch_url);
    }

    public function testSimGetLaunchUrl_javaSimWithDefaultLocaleReturnsLinkToBaseJnlp() {
        $sim = $this->getJavaSimArray();
        $expected_value = SIMS_ROOT."{$sim['sim_dirname']}/{$sim['sim_flavorname']}.jnlp";
        $launch_url = sim_get_launch_url($sim);
        $this->assertEquals($expected_value, $launch_url);
    }

    public function testSimGetLaunchUrl_javaSimWithExistingShortFormLocaleReturnsLinkToLocalizedJnlp() {
        $sim = $this->getJavaSimArray();
        $locale = 'pt_BR';
        $expected_value = SIMS_ROOT."{$sim['sim_dirname']}/{$sim['sim_flavorname']}_{$locale}.jnlp";
        $launch_url = sim_get_launch_url($sim, $locale);
        $this->assertEquals($expected_value, $launch_url);
    }

    public function testSimGetLaunchUrl_javaSimWithExistingLongFormLocaleReturnsLinkToLocalizedJnlp() {
        $sim = $this->getJavaSimArray();
        $locale = 'pt_BR';
        $expected_value = SIMS_ROOT."{$sim['sim_dirname']}/{$sim['sim_flavorname']}_{$locale}.jnlp";
        $launch_url = sim_get_launch_url($sim, $locale);
        $this->assertEquals($expected_value, $launch_url);
    }

    public function testSimGetLaunchUrl_javaSimWithNonexistingLocaleReturnsLinkToLocalizedJnlp() {
        $sim = $this->getJavaSimArray();
        $locale = 'yi';
        $expected_value = SIMS_ROOT."{$sim['sim_dirname']}/{$sim['sim_flavorname']}_{$locale}.jnlp";
        $launch_url = sim_get_launch_url($sim, $locale);
        $this->assertEquals($expected_value, $launch_url);
    }

    public function testSimGetLaunchUrl_javaSimWithNonexistingLocaleAndFileTestingReturnsEmptyString() {
        $sim = $this->getJavaSimArray();
        $locale = 'yi';
        $expected_value = '';
        $launch_url = sim_get_launch_url($sim, $locale, true);
        $this->assertEquals($expected_value, $launch_url);
    }

    public function testSimGetLaunchUrl_flashSimWithDefaultLocaleReturnsLinkToBaseHtml() {
        $sim = $this->getFlashSimArray();
        $expected_value = SIMS_ROOT."{$sim['sim_dirname']}/{$sim['sim_flavorname']}_en.html";
        $launch_url = sim_get_launch_url($sim);
        $this->assertEquals($expected_value, $launch_url);
    }

    public function testSimGetLaunchUrl_flashSimWithExistingShortFormLocaleReturnsLinkToLocalizedHtml() {
        $sim = $this->getFlashSimArray();
        $locale = 'zh_TW';
        $expected_value = SIMS_ROOT."{$sim['sim_dirname']}/{$sim['sim_flavorname']}_{$locale}.html";
        $launch_url = sim_get_launch_url($sim, $locale);
        $this->assertEquals($expected_value, $launch_url);
    }

    public function testSimGetLaunchUrl_flashSimWithExistingLongFormLocaleReturnsLinkToLocalizedHtml() {
        $sim = $this->getFlashSimArray();
        $locale = 'zh_TW';
        $expected_value = SIMS_ROOT."{$sim['sim_dirname']}/{$sim['sim_flavorname']}_{$locale}.html";
        $launch_url = sim_get_launch_url($sim, $locale);
        $this->assertEquals($expected_value, $launch_url);
    }

    public function testSimGetLaunchUrl_flashSimWithNonexistingLocaleReturnsLinkToLocalizedHtml() {
        $sim = $this->getFlashSimArray();
        $locale = 'yi';
        $expected_value = SIMS_ROOT."{$sim['sim_dirname']}/{$sim['sim_flavorname']}_{$locale}.html";
        $launch_url = sim_get_launch_url($sim, $locale);
        $this->assertEquals($expected_value, $launch_url);
    }

    public function testSimGetLaunchUrl_flashSimWithNonexistingLocaleAndFileTestingReturnsEmptyString() {
        $sim = $this->getFlashSimArray();
        $locale = 'yi';
        $expected_value = '';
        $launch_url = sim_get_launch_url($sim, $locale, true);
        $this->assertEquals($expected_value, $launch_url);
    }

    /**
     *
     * Testing sim_get_download_url()
     *
     */

    public function testSimGetDownloadUrl_invalidLocaleReturnsDefaultLocale() {
        $sim = $this->getJavaSimArray();
        $locale = 'foo_BAR';
        $expected_value = '';
        $expected_value = SITE_ROOT."admin/get-run-offline.php?sim_id={$sim['sim_id']}&amp;locale=".DEFAULT_LOCALE;
        $download_url = sim_get_download_url($sim, $locale);
        $this->assertEquals($expected_value, $download_url);
    }

    public function testSimGetDownloadUrl_javaSimWithDefaultLocaleReturnsLinkToGetRunOffline() {
        $locale = DEFAULT_LOCALE;
        $sim = $this->getJavaSimArray();
        $expected_value = SITE_ROOT."admin/get-run-offline.php?sim_id={$sim['sim_id']}&amp;locale={$locale}";
        $download_url = sim_get_download_url($sim);
        $this->assertEquals($expected_value, $download_url);
    }

    public function testSimGetDownloadUrl_javaSimWithExistingShortFormLocaleReturnsLinkToGetRunOffline() {
        $sim = $this->getJavaSimArray();
        $locale = 'pt_BR';
        $expected_value = SITE_ROOT."admin/get-run-offline.php?sim_id={$sim['sim_id']}&amp;locale={$locale}";
        $download_url = sim_get_download_url($sim, $locale);
        $this->assertEquals($expected_value, $download_url);
    }

    public function testSimGetDownloadUrl_javaSimWithExistingLongFormLocaleReturnsLinkToGetRunOffline() {
        $sim = $this->getJavaSimArray();
        $locale = 'pt_BR';
        $expected_value = SITE_ROOT."admin/get-run-offline.php?sim_id={$sim['sim_id']}&amp;locale={$locale}";
        $download_url = sim_get_download_url($sim, $locale);
        $this->assertEquals($expected_value, $download_url);
    }

    public function testSimGetDownloadUrl_javaSimWithNonexistingLocaleReturnsLinkToGetRunOffline() {
        $sim = $this->getJavaSimArray();
        $locale = 'yi';
        $expected_value = SITE_ROOT."admin/get-run-offline.php?sim_id={$sim['sim_id']}&amp;locale={$locale}";
        $download_url = sim_get_download_url($sim, $locale);
        $this->assertEquals($expected_value, $download_url);
    }

    public function testSimGetDownloadUrl_javaSimWithNonexistingLocaleAndFileTestingReturnsEmptyString() {
        $sim = $this->getJavaSimArray();
        $locale = 'yi';
        $expected_value = '';
        $download_url = sim_get_download_url($sim, $locale, true);
        $this->assertEquals($expected_value, $download_url);
    }

    public function testSimGetDownloadUrl_flashSimWithDefaultLocaleReturnsLinkToGetRunOffline() {
        $locale = DEFAULT_LOCALE;
        $sim = $this->getFlashSimArray();
        $expected_value = SIMS_ROOT."{$sim['sim_dirname']}/{$sim['sim_flavorname']}_en.jar";
        $expected_value = SITE_ROOT."admin/get-run-offline.php?sim_id={$sim['sim_id']}&amp;locale={$locale}";
        $download_url = sim_get_download_url($sim);
        $this->assertEquals($expected_value, $download_url);
    }

    public function testSimGetDownloadUrl_flashSimWithExistingShortFormLocaleReturnsLinkToGetRunOffline() {
        $sim = $this->getFlashSimArray();
        $locale = 'zh_TW';
        $expected_value = SIMS_ROOT."{$sim['sim_dirname']}/{$sim['sim_flavorname']}_{$locale}.jar";
        $expected_value = SITE_ROOT."admin/get-run-offline.php?sim_id={$sim['sim_id']}&amp;locale={$locale}";
        $download_url = sim_get_download_url($sim, $locale);
        $this->assertEquals($expected_value, $download_url);
    }

    public function testSimGetDownloadUrl_flashSimWithExistingLongFormLocaleReturnsLinkToGetRunOffline() {
        $sim = $this->getFlashSimArray();
        $locale = 'zh_TW';
        //$expected_value = SIMS_ROOT."{$sim['sim_dirname']}/{$sim['sim_flavorname']}_{$locale}.jar";
        $expected_value = SITE_ROOT."admin/get-run-offline.php?sim_id={$sim['sim_id']}&amp;locale={$locale}";
        $download_url = sim_get_download_url($sim, $locale);
        $this->assertEquals($expected_value, $download_url);
    }

    public function testSimGetDownloadUrl_flashSimWithNonexistingLocaleReturnsLinkToGetRunOffline() {
        $sim = $this->getFlashSimArray();
        $locale = 'yi';
        //        $expected_value = SIMS_ROOT."{$sim['sim_dirname']}/{$sim['sim_flavorname']}_{$locale}.jar";
        $expected_value = SITE_ROOT."admin/get-run-offline.php?sim_id={$sim['sim_id']}&amp;locale={$locale}";
        $download_url = sim_get_download_url($sim, $locale);
        $this->assertEquals($expected_value, $download_url);
    }

    public function testSimGetDownloadUrl_flashSimWithNonexistingLocaleAndFileTestingReturnsEmptyString() {
        $sim = $this->getFlashSimArray();
        $locale = 'yi';
        $expected_value = '';
        $download_url = sim_get_download_url($sim, $locale, true);
        $this->assertEquals($expected_value, $download_url);
    }

    /**
     *
     * Testing sim_get_screenshot()
     *
     */

    public function testSimGetScreenshot_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_animated_screenshot()
     *
     */

    public function testSimGetAnimatedScreenshot_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_select_sims_by_category_statement()
     *
     */

    public function testSimGetSelectSimsByCategoryStatement_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_select_sims_by_category_statement_order_alphabetically()
     *
     */

    public function testSimGetSelectSimsByCategoryStatementOrderAlphabetically_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_file_contents()
     *
     */

    public function testSimGetFileContents_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_image_previews()
     *
     */

    public function testSimGetImagePreviews_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_animated_previews()
     *
     */

    public function testSimGetAnimatedPreviews_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_static_previews()
     *
     */

    public function testSimGetStaticPreviews_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_resource_name_for_image()
     *
     */

    public function testSimGetResourceNameForImage_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_thumbnail()
     *
     */

    public function testSimGetThumbnail_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_remove_teachers_guide()
     *
     */

    public function testSimRemoveTeachersGuide_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_set_teachers_guide()
     *
     */

    public function testSimSetTeachersGuide_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_teachers_guide_by_sim_id()
     *
     */

    public function testSimGetTeachersGuideBySimId_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing sim_get_teachers_guide()
     *
     */

    public function testSimGetTeachersGuide_testCase() {
        $this->markTestIncomplete();
    }

}

?>
