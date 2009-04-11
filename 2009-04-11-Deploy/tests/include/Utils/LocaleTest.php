<?php

require_once('PHPUnit/Framework.php');

// SITE_ROOT - Relative path to website's top directory from the tested file
if (!defined('SITE_ROOT')) define('SITE_ROOT', '../');

// Get the test globals to set everything up
require_once(dirname(dirname(dirname(__FILE__))).'/test_global.php');

// Include global.php and the autoloader will take care of the classes
require_once("include/global.php");

// Get the file to test
require_once("include/locale-codes-country.php");
require_once("include/locale-codes-language.php");

class LocaleTest extends PHPUnit_Framework_TestCase {
    private $lower_chars;
    private $upper_chars;

    public function __construct() {
        $this->lower_chars = str_split('abcdefghijklmnopqrstuvwxyz');
        $this->upper_chars = str_split('abcdefghijklmnopqrstuvwxyz');
        $this->language_map = locale_get_language_map();
        $this->country_map = locale_get_country_map();
    }

    public function getValidLanguageCode() {
        // Grab the first offically supported language code
        reset($this->language_map);
        $language_code = key($this->language_map);
        return $language_code;
    }

    public function getValidCountryCode() {
        // Grab the first offically supported country code
        reset($this->country_map);
        $country_code = key($this->country_map);
        return $country_code;
    }

    public function getInvalidLanguageCode() {
        // Find a language that is not supported
        $language_code = 'xx';
        while (array_key_exists($language_code, $this->language_map)) {
            $language_code = $this->lower_chars[array_rand($this->lower_chars)].
                $lower_chars[array_rand($this->lower_chars)];
        }
        $this->assertFalse(array_key_exists($language_code, $this->language_map));
        return $language_code;
    }

    public function getInvalidCountryCode() {
        // Find a country that is not supported
        $country_code = 'XX';
        while (array_key_exists($country_code, $this->country_map)) {
            $country_code = $this->upper_chars[array_rand($this->upper_chars)].
                $upper_chars[array_rand($this->upper_chars)];
        }
        $this->assertFalse(array_key_exists($country_code, $this->country_map));

        return $country_code;
    }

    /**
     * Setup
     */

    public function setUp() {
        parent::setUp();
        $this->fixture = Locale::inst();
    }

    /**
     *
     * Testing locale_is_combined_language_code()
     *
     */

    public function testLocaleIsCombinedLanguageCode_returnsExpeted() {
        // combined_code => expected_remap
        $combined_codes = array('bp' => 'pt_BR', 'tc' => 'zh_TW');
        foreach ($combined_codes as $combined => $full) {
            $result = $this->fixture->isCombinedLanguageCode($combined);
            $this->assertTrue($result);
        }
    }

    public function testLocaleIsCombinedLanguageCode_nonRemappedCodeReturnsFalse() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale = $language_code.'_'.$country_code;

        $result =  $this->fixture->isCombinedLanguageCode($locale);
        
        $this->assertFalse($result);
    }

    public function testLocaleHasCombinedLanguageCodeMap_returnsExpeted() {
        // combined_code => expected_remap
        $full_locales = array('pt_BR' => 'bp', 'zh_TW' => 'tc');
        foreach ($full_locales as $full => $combined) {
            $result = $this->fixture->hasCombinedLanguageCodeMap($full);
            $this->assertTrue($result);
        }
    }

    public function testLocaleHasCombinedLanguageCodeMap_nonRemappedCodeReturnsFalse() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale = $language_code.'_'.$country_code;

        $result =  $this->fixture->hasCombinedLanguageCodeMap($locale);
        
        $this->assertFalse($result);
    }

    /**
     *
     * Testing $this->fixture->combinedLanguageCodeToFullLocale()
     *
     */

    public function testLocaleCombinedLanguageCodeToFullLocale_returnsExpetedRemappedCodes() {
        // combined_code => expected_remap
        $combined_codes = array('bp' => 'pt_BR', 'tc' => 'zh_TW');
        foreach ($combined_codes as $combined => $full) {
            $result =  $this->fixture->combinedLanguageCodeToFullLocale($combined);
            $this->assertEquals($full, $result);
        }
    }

    public function testLocaleCombinedLanguageCodeToFullLocale_nonRemappedCodeRetursUnchanged() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale = $language_code.'_'.$country_code;

        $result =  $this->fixture->combinedLanguageCodeToFullLocale($locale);
        $this->assertEquals($locale, $result);
    }

    /**
     *
     * Testing locale_full_locale_to_combined_language_code()
     *
     * These tests are from when locale support was a set of functions
     * rather than a class.  These are now private methods, and cannot
     * be tested directly.  I'm keeping them around in case they every
     * need public visibility.  If the member function does get
     * promoted, uncomment and retest.
     *
     */

    /*
     public function testLocaleFullLocaleToCombinedLanguageCode_returnsExpetedRemappedCodes() {
        // combined_code => expected_remap
        $full_locales = array('pt_BR' => 'bp', 'zh_TW' => 'tc');
        foreach ($full_locales as $full => $combined) {
            $result =  $this->fixture->fullLocaleToCombinedLanguageCode($full);
            $this->assertEquals($combined, $result);
        }
    }

    public function testLocaleFullLocaleToCombinedLanguageCode_nonRemappedCodeRetursUnchanged() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale = $language_code.'_'.$country_code;

        $result =  $this->fixture->fullLocaleToCombinedLanguageCode($locale);
        $this->assertEquals($locale, $result);
    }
    */

    /**
     *
     * Testing locale_is_default()
     *
     */

    public function testLocaleIsDefault_defaultShortFormLocaleRetrunsTrue() {
        $this->assertTrue($this->fixture->isDefault('en'));
    }

    public function testLocaleIsDefault_defaultLongFormLocaleRetrunsTrue() {
        $this->assertTrue($this->fixture->isDefault('en_US'));
    }

    public function testLocaleIsDefault_nondefaultShortFormLocaleRetrunsFalse() {
        $language_code = $this->getValidLanguageCode();
        $locale = $language_code;
        $this->assertFalse($this->fixture->isDefault($locale));
    }

    public function testLocaleIsDefault_nondefaultLongFormLocaleRetrunsFalse() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale = $language_code.'_'.$country_code;
        $this->assertFalse($this->fixture->isDefault($locale));
    }

    /**
     *
     * Testing locale_valid_language_code()
     *
     */
    
    public function testLocaleValidLanguageCode_validCodeReturnsTrue() {
        $language_code = $this->getValidLanguageCode();

        $result = $this->fixture->isValidLanguageCode($language_code);
        $this->assertTrue($result);
    }

    public function testLocaleValidLanguageCode_invalidCodeReturnsFalse() {
        $language_code = $this->getInvalidLanguageCode();

        $result = $this->fixture->isValidLanguageCode($language_code);
        $this->assertFalse($result);
    }

    public function testLocaleValidLanguageCode_invalidStringReturnsFalse() {
        $language_code = '_foo';
        $this->assertFalse(array_key_exists($language_code, $this->language_map));

        $result = $this->fixture->isValidLanguageCode($language_code);
        $this->assertFalse($result);
    }

    public function testLocaleValidLanguageCode_invalidTypeReturnsFalse() {
        $language_code = array();

        $result = $this->fixture->isValidLanguageCode($language_code);
        $this->assertFalse($result);
    }

    /**
     *
     * Testing locale_valid_country_code()
     *
     */

    public function testLocaleValidCountryCode_validCodeReturnsTrue() {
        $country_code = $this->getValidCountryCode();

        $result = $this->fixture->isValidCountryCode($country_code);
        $this->assertTrue($result);
    }

    public function testLocaleValidCountryCode_invalidCodeReturnsFalse() {
        // Find a country that is not supported
        $country_code = $this->getInvalidCountryCode();

        $result = $this->fixture->isValidCountryCode($country_code);
        $this->assertFalse($result);
    }

    public function testLocaleValidCountryCode_invalidStringReturnsFalse() {
        $country_code = '_FOO';
        $this->assertFalse(array_key_exists($country_code, $this->country_map));

        $result = $this->fixture->isValidCountryCode($country_code);
        $this->assertFalse($result);
    }

    public function testLocaleValidCountryCode_invalidTypeReturnsFalse() {
        $country_code = array();

        $result = $this->fixture->isValidCountryCode($country_code);
        $this->assertFalse($result);
    }

    /**
     *
     * Testing locale_valid()
     *
     */

    public function testLocaleValid_validShortFormReturnsTrue() {
        $language_code = $this->getValidLanguageCode();

        // Create the locale
        $locale = $language_code;

        $result = $this->fixture->isValid($locale);
        $this->assertTrue($result);
    }

    public function testLocaleValid_validLongFormReturnsTrue() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale = $language_code.'_'.$country_code;
        
        // Test the code
        $result = $this->fixture->isValid($locale);
        $this->assertTrue($result);
    }

    public function testLocaleValid_longFormInvalidLangugaeReturnsFalse() {
        $language_code = $this->getInvalidLanguageCode();
        $country_code = $this->getValidCountryCode();

        // Create the locale
        $locale = $language_code.'_'.$country_code;
        
        // Test the code
        $result = $this->fixture->isValid($locale);
        $this->assertFalse($result);
    }

    public function testLocaleValid_longFormInvalidCountryReturnsFalse() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getInvalidCountryCode();

        // Create the locale
        $locale = $language_code.'_'.$country_code;
        
        // Test the code
        $result = $this->fixture->isValid($locale);
        $this->assertFalse($result);
    }

    public function testLocaleValid_emptyStringReturnsFalse() {
        $result = $this->fixture->isValid('');
        $this->assertFalse($result);
    }
    
    public function testLocaleValid_badTypeReturnsFalse() {
        $result = $this->fixture->isValid(array('en', 'US'));
        $this->assertFalse($result);
    }

    public function testLocaleValid_invalidStringReturnsFalse() {
        $result = $this->fixture->isValid('foo bar');
        $this->assertFalse($result);
    }

    /**
     *
     * Testing locale_has_valid_language_code()
     *
     * These tests are from when locale support was a set of functions
     * rather than a class.  These are now private methods, and cannot
     * be tested directly.  I'm keeping them around in case they every
     * need public visibility.  If the member function does get
     * promoted, uncomment and retest.
     *
     */

    /*
    public function testLocaleHasValidLanguageCode_shortFormValidLanguageReturnsTrue() {
        $language_code = $this->getValidLanguageCode();
        $locale = $language_code;

        $result = $this->fixture->hasValidLanguageCode($locale);
        $this->assertTrue($result);
    }

    public function testLocaleHasValidLanguageCode_shortFormInvalidLanguageReturnsFalse() {
        $language_code = $this->getInvalidLanguageCode();
        $locale = $language_code;

        $result = $this->fixture->hasValidLanguageCode($locale);
        $this->assertFalse($result);
    }

    public function testLocaleHasValidLanguageCode_validLongFormReturnsTrue() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale = $language_code.'_'.$country_code;

        $result = $this->fixture->hasValidLanguageCode($locale);
        $this->assertTrue($result);
    }

    public function testLocaleHasValidLanguageCode_longFormValidLanguageInvalidCountryReturnsTrue() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getInvalidCountryCode();

        $locale = $language_code.'_'.$country_code;
        $result = $this->fixture->hasValidLanguageCode($locale);
        $this->assertTrue($result);
    }

    public function testLocaleHasValidLanguageCode_longFormInvalidLanguageValidCountryReturnsFalse() {
        $language_code = $this->getInvalidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale = $language_code.'_'.$country_code;

        $result = $this->fixture->hasValidLanguageCode($locale);
        $this->assertFalse($result);
    }

    public function testLocaleHasValidLanguageCode_invalidFormReturnsFalse() {
        $locale = 'foo_BAR';

        $result = $this->fixture->hasValidLanguageCode($locale);
        $this->assertFalse($result);
    }
    */

    /**
     *
     * Testing locale_has_valid_country_code()
     *
     * These tests are from when locale support was a set of functions
     * rather than a class.  These are now private methods, and cannot
     * be tested directly.  I'm keeping them around in case they every
     * need public visibility.  If the member function does get
     * promoted, uncomment and retest.
     *
     */

    /*
    public function testLocaleHasValidCountryCode_shortFormReturnsFalse() {
        $language_code = $this->getValidLanguageCode();
        $locale = $language_code;

        $result = $this->fixture->hasValidCountryCode($locale);
        $this->assertFalse($result);
    }

    public function testLocaleHasValidCountryCode_shortFormInvalidLanguageReturnsFalse() {
        $language_code = $this->getInvalidLanguageCode();
        $locale = $language_code;

        $result = $this->fixture->hasValidLanguageCode($locale);
        $this->assertFalse($result);
    }

    public function testLocaleHasValidCountryCode_validLongFormReturnsTrue() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale = $language_code.'_'.$country_code;

        $result = $this->fixture->hasValidCountryCode($locale);
        $this->assertTrue($result);
    }

    public function testLocaleHasValidCountryCode_longFormValidLanguageInvalidCountryReturnsFalse() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getInvalidCountryCode();
        $locale = $language_code.'_'.$country_code;

        $result = $this->fixture->hasValidCountryCode($locale);
        $this->assertFalse($result);
    }

    public function testLocaleHasValidCountryCode_longFormInvalidLanguageValidCountryReturnsTrue() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale = $language_code.'_'.$country_code;

        $result = $this->fixture->hasValidCountryCode($locale);
        $this->assertTrue($result);
    }

    public function testLocaleHasValidCountryCode_invalidFormReturnsFalse() {
        $locale = 'foo_BAR';

        $result = $this->fixture->hasValidCountryCode($locale);
        $this->assertFalse($result);
    }
    */

    /**
     *
     * Testing locale_extract_language_code()
     *
     * These tests are from when locale support was a set of functions
     * rather than a class.  These are now private methods, and cannot
     * be tested directly.  I'm keeping them around in case they every
     * need public visibility.  If the member function does get
     * promoted, uncomment and retest.
     *
     */

    /*
    public function testLocaleExtractLanguageCode_shortFormLocaleWithValidLanguageCodeReturnsCode() {
        $language_code = $this->getValidLanguageCode();
        $locale_code = $language_code;

        $result = $this->fixture->extractLanguageCode($locale_code);
        $this->assertEquals($language_code, $result);
    }

    public function testLocaleExtractLanguageCode_shortFormLocaleWithInvalidLanguageCodeRaisesException() {
        // Find a language that is not supported
        $language_code = $this->getInvalidLanguageCode();

        $this->setExpectedException('PhetLocaleException');
        $this->fixture->extractLanguageCode($language_code);
    }

    public function testLocaleExtractLanguageCode_longFormLocaleWithValidLanguageAndValidCountryCodeCodeReturnsCode() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();

        // Create the locale
        $locale_code = $language_code.'_'.$country_code;

        $result = $this->fixture->extractLanguageCode($locale_code);
        $this->assertEquals($language_code, $result);
    }

    public function testLocaleExtractLanguageCode_longFormLocaleWithValidLanguageAndInvalidCountryCodeReturnsCode() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getInvalidCountryCode();

        // Add a country code, the acutal code doesn't matter
        $locale_code = $language_code.'_'.$country_code;

        $result = $this->fixture->extractLanguageCode($locale_code);
        $this->assertEquals($language_code, $result);
    }

    public function testLocaleExtractLanguageCode_invalidTypeRaisesException() {
        $this->setExpectedException('PhetLocaleException');
        $this->fixture->extractLanguageCode(array());
    }
    */

    /**
     *
     * Testing locale_extract_country_code()
     *
     * These tests are from when locale support was a set of functions
     * rather than a class.  These are now private methods, and cannot
     * be tested directly.  I'm keeping them around in case they every
     * need public visibility.  If the member function does get
     * promoted, uncomment and retest.
     *
     */

    /*
    public function testLocaleExtractCountryCode_shortFormLocaleRaisesException() {
        $language_code = $this->getValidLanguageCode();
        $locale_code = $language_code;
        
        $this->setExpectedException('PhetLocaleException');
        $this->fixture->extractCountryCode($locale_code);
    }

    public function testLocaleExtractCountryCode_longFormLocaleWithValidLangugeAndValidCountryCodeReturnsCode() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale_code = $language_code."_".$country_code;

        $result = $this->fixture->extractCountryCode($locale_code);
        $this->assertEquals($country_code, $result);
    }

    public function testLocaleExtractCountryCode_longFormLocaleWithInvalidLangugeAndValidCountryCodeReturnsCode() {
        $language_code = $this->getInvalidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale_code = $language_code."_".$country_code;

        $result = $this->fixture->extractCountryCode($locale_code);
        $this->assertEquals($country_code, $result);
    }

    public function testLocaleExtractCountryCode_localeWithInvalidCountryCodeRaisesException() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getInvalidCountryCode();
        $locale_code = $language_code."_".$country_code;

        $this->setExpectedException('PhetLocaleException');
        $this->fixture->extractCountryCode($locale_code);
    }

    public function testLocaleExtractCountryCode_invalidTypeRaisesException() {
        $this->setExpectedException('PhetLocaleException');
        $this->fixture->extractCountryCode(array());
    }
    */

    /**
     *
     * Testing locale_get_language_name()
     *
     * These tests are from when locale support was a set of functions
     * rather than a class.  These are now private methods, and cannot
     * be tested directly.  I'm keeping them around in case they every
     * need public visibility.  If the member function does get
     * promoted, uncomment and retest.
     *
     */

    /*
    public function testLocaleGetLanguageName_validCodeReturnsName() {
        $language_code = $this->getValidLanguageCode();

        $result = $this->fixture->getLanguageName($language_code);
        $this->assertEquals($this->language_map[$language_code], $result);
    }

    public function testLocaleGetLanguageName_invalidCodeRaisesException() {
        $language_code = $this->getInvalidLanguageCode();

        $this->setExpectedException('PhetLocaleException');
        $this->fixture->getLanguageName($language_code);
    }

    public function testLocaleGetLanguageName_invalidStringRaisesException() {
        $language_code = '_foo';
        $this->assertFalse(array_key_exists($language_code, $this->language_map));

        $this->setExpectedException('PhetLocaleException');
        $this->fixture->getLanguageName($language_code);
    }

    public function testLocaleGetLanguageName_invalidTypeRaisesException() {
        $this->setExpectedException('PhetLocaleException');
        $this->fixture->getLanguageName(array());
    }
    */

    /**
     *
     * Testing locale_get_country_name()
     *
     * These tests are from when locale support was a set of functions
     * rather than a class.  These are now private methods, and cannot
     * be tested directly.  I'm keeping them around in case they every
     * need public visibility.  If the member function does get
     * promoted, uncomment and retest.
     *
     */

    /*
    public function testLocaleGetCountryName_validCodeReturnsName() {
        $country_code = $this->getValidCountryCode();
        $result = $this->fixture->getCountryName($country_code);
        $this->assertEquals($this->country_map[$country_code], $result);
    }

    public function testLocaleGetCountryName_invalidCodeRaisesException() {
        // Find a country that is not supported
        $country_code = $this->getInvalidCountryCode();

        $this->setExpectedException('PhetLocaleException');
        $this->fixture->getCountryName($country_code);
    }

    public function testLocaleGetCountryName_invalidStringRaisesException() {
        $country_code = '_foo';
        $this->assertFalse(array_key_exists($country_code, $this->country_map));

        $this->setExpectedException('PhetLocaleException');
        $this->fixture->getCountryName($country_code);
    }

    public function testLocaleGetCountryName_invalidTypeRaisesException() {
        $this->setExpectedException('PhetLocaleException');
        $this->fixture->getCountryName(array());
    }
    */

    /**
     *
     * Testing locale_get_locale_name()
     *
     * These tests are from when locale support was a set of functions
     * rather than a class.  These are now private methods, and cannot
     * be tested directly.  I'm keeping them around in case they every
     * need public visibility.  If the member function does get
     * promoted, uncomment and retest.
     *
     */

    /*
     public function testLocaleGetLocaleName_validShortFormReturnsExpectedString() {
        $language_code = $this->getValidLanguageCode();
        $locale_code = $language_code;

        $expected_result = "{$this->language_map[$language_code]}";

        $result = $this->fixture->getLocaleName($locale_code);
        $this->assertEquals($expected_result, $result);
    }
    
    public function testLocaleGetLocaleName_validLongFormReturnsExpectedString() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale_code = $language_code."_".$country_code;

        $expected_result = "{$this->language_map[$language_code]}, {$this->country_map[$country_code]}";

        $result = $this->fixture->getLocaleName($locale_code);
        $this->assertEquals($expected_result, $result);
    }
    
    public function testLocaleGetLocaleName_longFormValidLanguageInvalidCountryRaisesException() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getInvalidCountryCode();
        $locale_code = $language_code."_".$country_code;

        $this->setExpectedException('PhetLocaleException');
        $this->fixture->getLocaleName($locale_code);
    }
    
    public function testLocaleGetLocaleName_longFormInvalidLanguageValidCountryRaisesException() {
        $language_code = $this->getInvalidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale_code = $language_code."_".$country_code;

        $this->setExpectedException('PhetLocaleException');
        $this->fixture->getLocaleName($locale_code);
    }
    
    public function testLocaleGetLocaleName_longFormInvalidLanguageInvalidCountryRaisesException() {
        $language_code = $this->getInvalidLanguageCode();
        $country_code = $this->getInvalidCountryCode();
        $locale_code = $language_code."_".$country_code;

        $this->setExpectedException('PhetLocaleException');
        $this->fixture->getLocaleName($locale_code);
    }
    
    public function testLocaleGetLocaleName_invalidLongFormRaisesException() {
        $locale_code = 'foo_BAR';

        $this->setExpectedException('PhetLocaleException');
        $this->fixture->getLocaleName($locale_code);
    }
    */

    /**
     *
     * Testing locale_language_sort_code_by_name()
     *
     * These tests are from when locale support was a set of functions
     * rather than a class.  These are now private methods, and cannot
     * be tested directly.  I'm keeping them around in case they every
     * need public visibility.  If the member function does get
     * promoted, uncomment and retest.
     *
     */

    /*
     public function testLocaleLanguageSortCodeByName_sortsInCorrectOrderGreaterThan() {
        $result = $this->fixture->languageSortCodeByName('eu', 'sq');
        $this->assertGreaterThan(0, $result);
    }

    public function testLocaleLanguageSortCodeByName_sortsInCorrectOrderLessThan() {
        $result = $this->fixture->languageSortCodeByName('sq', 'eu');
        $this->assertLessThan(0, $result);
    }

    public function testLocaleLanguageSortCodeByName_sortsInCorrectOrderEquals() {
        $result = $this->fixture->languageSortCodeByName('he', 'iw');
        $this->assertEquals(0, $result);
    }

    public function testLocaleLanguageSortCodeByName_sortsInCorrectOrderArray() {
        $codes_to_sort = array('aa', 'an', 'bs', 'eu', 'hy', 'sq', 'bp', 'tc');
        $expected_order = array('aa', 'sq', 'an', 'hy', 'eu', 'bs', 'tc', 'bp');
        usort($codes_to_sort, array($this->fixture, 'languageSortCodeByName'));
        $this->assertEquals($expected_order, $codes_to_sort);
    }
    */

    /**
     *
     * Testing locale_country_sort_code_by_name()
     *
     * These tests are from when locale support was a set of functions
     * rather than a class.  These are now private methods, and cannot
     * be tested directly.  I'm keeping them around in case they every
     * need public visibility.  If the member function does get
     * promoted, uncomment and retest.
     *
     */

    /*
     public function testLocaleCountrySortCodeByName_sortsInCorrectOrderGreaterThan() {
        $result = $this->fixture->countrySortCodeByName('AS', 'DZ');
        $this->assertGreaterThan(0, $result);
    }

    public function testLocaleCountrySortCodeByName_sortsInCorrectOrderLessThan() {
        $result = $this->fixture->countrySortCodeByName('AU', 'AT');
        $this->assertLessThan(0, $result);
    }

    public function testLocaleCountrySortCodeByName_sortsInCorrectOrderEquals() {
        $result = $this->fixture->countrySortCodeByName('US', 'US');
        $this->assertEquals(0, $result);
    }

    public function testLocaleCountrySortCodeByName_sortsInCorrectOrderArray() {
        $codes_to_sort = array('AS', 'AU', 'AX', 'BN', 'DZ', 'IO');
        $expected_order = array('AX', 'DZ', 'AS', 'AU', 'IO', 'BN');
        usort($codes_to_sort, array($this->fixture, 'countrySortCodeByName'));
        $this->assertEquals($expected_order, $codes_to_sort);
    }
    */

    /**
     *
     * Testing locale_sort_code_by_name()
     *
     */

    public function testLocaleSortCodeByName_shortFormLanguageSortsInCorrectOrderGreaterThan() {
        $result = $this->fixture->sortCodeByNameCmp('en', 'aa');
        $this->assertGreaterThan(0, $result);
    }

    public function testLocaleSortCodeByName_shortFormLanguageSortsInCorrectOrderLessThan() {
        $result = $this->fixture->sortCodeByNameCmp('aa', 'en');
        $this->assertLessThan(0, $result);
    }

    public function testLocaleSortCodeByName_shortFormLanguageSortsInCorrectOrderEquals() {
        $result = $this->fixture->sortCodeByNameCmp('en', 'en');
        $this->assertEquals(0, $result);
    }

    public function testLocaleSortCodeByName_longFormDifferentLanguageSortsInCorrectOrderGreaterThan() {
        $result = $this->fixture->sortCodeByNameCmp('en_GB', 'zh_TW');
        $this->assertGreaterThan(0, $result);
    }

    public function testLocaleSortCodeByName_longFormDifferentLanguageSortsInCorrectOrderLessThan() {
        $result = $this->fixture->sortCodeByNameCmp('zh_TW', 'en_GB');
        $this->assertLessThan(0, $result);
    }

    public function testLocaleSortCodeByName_longFormSameLanguageSortsInCorrectOrderEquals() {
        $result = $this->fixture->sortCodeByNameCmp('en_GB', 'en_GB');
        $this->assertEquals(0, $result);
    }

    public function testLocaleSortCodeByName_longFormSameLanguageSortsInCorrectOrderGreaterThan() {
        $result = $this->fixture->sortCodeByNameCmp('en_US', 'en_GB');
        $this->assertGreaterThan(0, $result);
    }

    public function testLocaleSortCodeByName_longFormSameLanguageSortsInCorrectOrderLessThan() {
        $result = $this->fixture->sortCodeByNameCmp('en_GB', 'en_US');
        $this->assertLessThan(0, $result);
    }

    public function testLocaleSortCodeByName_mixedFormDifferentLanguageSortsInCorrectOrderGreaterThan() {
        $result = $this->fixture->sortCodeByNameCmp('en_GB', 'zh');
        $this->assertGreaterThan(0, $result);
    }

    public function testLocaleSortCodeByName_mixedFormDifferentLanguageSortsInCorrectOrderLessThan() {
        $result = $this->fixture->sortCodeByNameCmp('zh_TW', 'en');
        $this->assertLessThan(0, $result);
    }

    public function testLocaleSortCodeByName_mixedFormSameLanguageSortsInCorrectOrderGreaterThan() {
        $result = $this->fixture->sortCodeByNameCmp('en_US', 'en');
        $this->assertGreaterThan(0, $result);
    }

    public function testLocaleSortCodeByName_mixedFormSameLanguageSortsInCorrectOrderLessThan() {
        $result = $this->fixture->sortCodeByNameCmp('en', 'en_US');
        $this->assertLessThan(0, $result);
    }

    public function testLocaleSortCodeByName_mixedFormExpectedSortOrder() {
        $locales = array('en_US', 'en_GB', 'en', 'zh_TW', 'zh_CN', 'zh', 'pt', 'bp');
        $expected_orders = array('zh', 'zh_CN', 'zh_TW', 'en', 'en_GB', 'en_US', 'pt', 'bp');

        $sort_result = usort($locales, array($this->fixture, 'sortCodeByNameCmp'));
        $this->assertEquals($expected_orders, $locales);
    }

    /**
     *
     * Testing locale_get_language_icon_url()
     *
     * These tests are from when locale support was a set of functions
     * rather than a class.  These are now private methods, and cannot
     * be tested directly.  I'm keeping them around in case they every
     * need public visibility.  If the member function does get
     * promoted, uncomment and retest.
     *
     */

    /*
     public function testLocaleGetLanguageIconUrl_validLanguageCodeReturnsExpected() {
        $language_code = $this->getValidLanguageCode();

        $language_name = $this->fixture->getLanguageName($language_code);
        $exepcted_result = SITE_ROOT."images/languages/".strtolower("{$language_name}-{$language_code}.png");

        $result = $this->fixture->getLanguageIconUrl($language_code);
        $this->assertEquals($exepcted_result, $result);
    }

    public function testLocaleGetLanguageIconUrl_invalidLanguageCodeRaisesException() {
        $language_code = $this->getInvalidLanguageCode();
        $this->setExpectedException('PhetLocaleException');
        $this->fixture->getLanguageIconUrl($language_code);
    }

    public function testLocaleGetLanguageIconUrl_invalidTypeRaisesException() {
        $this->setExpectedException('PhetLocaleException');
        $this->fixture->getLanguageIconUrl(array());
    }

    public function testLocaleGetLanguageIconUrl_invalidStringRaisesException() {
        $this->setExpectedException('PhetLocaleException');
        $this->fixture->getLanguageIconUrl('foo_BAR');
    }
    */

    /**
     *
     * Testing locale_get_country_icon_url()
     *
     * These tests are from when locale support was a set of functions
     * rather than a class.  These are now private methods, and cannot
     * be tested directly.  I'm keeping them around in case they every
     * need public visibility.  If the member function does get
     * promoted, uncomment and retest.
     *
     */

    /*
     public function testLocaleGetCountryIconUrl_validCountryCodeReturnsExpected() {
        $country_code = $this->getValidCountryCode();

        $country_name = $this->fixture->getCountryName($country_code);
        $exepcted_result = SITE_ROOT."images/countries/{$country_code}.png";

        $result = $this->fixture->getCountryIconUrl($country_code);
        $this->assertEquals($exepcted_result, $result);
    }

    public function testLocaleGetCountryIconUrl_invalidCountryCodeRaisesException() {
        $country_code = $this->getInvalidCountryCode();
        $this->setExpectedException('PhetLocaleException');
        $this->fixture->getCountryIconUrl($country_code);
    }

    public function testLocaleGetCountryIconUrl_invalidTypeRaisesException() {
        $this->setExpectedException('PhetLocaleException');
        $this->fixture->getCountryIconUrl(array());
    }

    public function testLocaleGetCountryIconUrl_invalidStringRaisesException() {
        $this->setExpectedException('PhetLocaleException');
        $this->fixture->getCountryIconUrl('foo_BAR');
    }

    /**
     *
     * Testing locale_get_language_img_html()
     *
     * These tests are from when locale support was a set of functions
     * rather than a class.  These are now private methods, and cannot
     * be tested directly.  I'm keeping them around in case they every
     * need public visibility.  If the member function does get
     * promoted, uncomment and retest.
     *
     */

    /*
     public function testLocaleGetLanguageImgHtml_invalidLocaleRaisesException() {
        $language_code = $this->getInvalidLanguageCode();
        $country_code = $this->getInvalidCountryCode();
        $locale = $language_code.'_'.$country_code;
        $this->assertFalse($this->fixture->isValid($locale));

        $this->setExpectedException('PhetLocaleException');
        $this->fixture->getLanguageImgHtml($locale);        
    }
    
    public function testLocaleGetLanguageImgHtml_validShortFormLocaleRecturnsExpected() {
        $language_code = $this->getValidLanguageCode();
        $locale = $language_code;
        $this->assertTrue($this->fixture->isValid($locale));

        $language_name = $this->fixture->getLanguageName($language_code);
        $language_icon = $this->fixture->getLanguageIconUrl($language_code);
        $expeted_img_html = 
                "<img ".
                    "class=\"language\" ".
                    "src=\"{$language_icon}\" ".
                    "alt=\"{$language_name}\" ".
                    "title=\"{$language_name}\" ".
                "/>";

        $result = $this->fixture->getLanguageImgHtml($locale);
        $this->assertEquals($expeted_img_html, $result);
    }
    
    public function testLocaleGetLanguageImgHtml_validLongFormLocaleRecturnsExpected() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale = $language_code.'_'.$country_code;
        $this->assertTrue($this->fixture->isValid($locale));

        $language_name = $this->fixture->getLanguageName($language_code);
        $language_icon = $this->fixture->getLanguageIconUrl($language_code);
        $expeted_img_html = 
                "<img ".
                    "class=\"language\" ".
                    "src=\"{$language_icon}\" ".
                    "alt=\"{$language_name}\" ".
                    "title=\"{$language_name}\" ".
                "/>";

        $result = $this->fixture->getLanguageImgHtml($language_code);
        $this->assertEquals($expeted_img_html, $result);
    }
    
    /**
     *
     * Testing locale_get_country_img_html()
     *
     * These tests are from when locale support was a set of functions
     * rather than a class.  These are now private methods, and cannot
     * be tested directly.  I'm keeping them around in case they every
     * need public visibility.  If the member function does get
     * promoted, uncomment and retest.
     *
     */

    /*
     public function testLocaleGetCountryImgHtml_invalidLocaleRaisesException() {
        $language_code = $this->getInvalidLanguageCode();
        $country_code = $this->getInvalidCountryCode();
        $locale = $language_code.'_'.$country_code;
        $this->assertFalse($this->fixture->isValid($locale));

        $this->setExpectedException('PhetLocaleException');
        $this->fixture->getCountryImgHtml($locale);        
    }
    
    public function testLocaleGetCountryImgHtml_validShortFormLocaleRaisesException() {
        $language_code = $this->getValidLanguageCode();
        $locale = $language_code;
        $this->assertTrue($this->fixture->isValid($locale));

        $this->setExpectedException('PhetLocaleException');
        $this->fixture->getCountryImgHtml($locale);        
    }
    
    public function testLocaleGetCountryImgHtml_validLongFormLocaleRecturnsExpected() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale = $language_code.'_'.$country_code;
        $this->assertTrue($this->fixture->isValid($locale));

        $country_name = $this->fixture->getCountryName($country_code);
        $country_icon = $this->fixture->getCountryIconUrl($country_code);
        $expeted_img_html = 
                "<img ".
                    "class=\"country\" ".
                    "src=\"{$country_icon}\" ".
                    "alt=\"{$country_name}\" ".
                    "title=\"{$country_name}\" ".
                "/>";

        $result = $this->fixture->getCountryImgHtml($country_code);
        $this->assertEquals($expeted_img_html, $result);
    }
    */

    /**
     *
     * Testing locale_get_full_info()
     *
     */

    public function testLocaleGetFullInfo_invalidLocaleReturnsFalse() {
        $language_code = $this->getInvalidLanguageCode();
        $country_code = $this->getInvalidCountryCode();
        $locale = $language_code.'_'.$country_code;
        $this->assertFalse($this->fixture->isValid($locale));

        $result = $this->fixture->getFullInfo($locale);
        $this->assertFalse($result);
    }

    public function testLocaleGetFullInfo_validShortFromReturnsExpected() {
        $language_code = $this->getValidLanguageCode();
        $locale = $language_code;
        $this->assertTrue($this->fixture->isValid($locale));

        $language_name = 'Abkhazian';
        $language_icon = SITE_ROOT.'images/languages/abkhazian-ab.png';
        $expceted_result = array(
            'locale' => $locale,
            'locale_name' => $language_name,
            'language_code' => 'ab',
            'language_name' => $language_name,
            'language_icon' => $language_icon,
            'language_img' => "<img ".
                    "class=\"language\" ".
                    "src=\"{$language_icon}\" ".
                    "alt=\"{$language_name}\" ".
                    "title=\"{$language_name}\" ".
            "/>",
            'country_code' => null,
            'country_name' => null,
            'country_icon' => null,
            'country_img' => null
            );

        $result = $this->fixture->getFullInfo($locale);
        $this->assertEquals($expceted_result, $result);
    }

    public function testLocaleGetFullInfo_validLongFromReturnsExpected() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale = $language_code.'_'.$country_code;
        $this->assertTrue($this->fixture->isValid($locale));

        $language_name = 'Abkhazian';
        $language_icon = SITE_ROOT.'images/languages/abkhazian-ab.png';
        $country_name = 'Aaland Islands';
        $country_icon = SITE_ROOT.'images/countries/AX.png';
        $expceted_result = array(
            'locale' => $locale,
            'locale_name' => "{$language_name}, {$country_name}",
            'language_code' => 'ab',
            'language_name' => $language_name,
            'language_icon' => $language_icon,
            'language_img' => "<img ".
                    "class=\"language\" ".
                    "src=\"{$language_icon}\" ".
                    "alt=\"{$language_name}\" ".
                    "title=\"{$language_name}\" ".
            "/>",
            'country_code' => 'AX',
            'country_name' => $country_name,
            'country_icon' => $country_icon,
            'country_img' =>  "<img ".
                    "class=\"country\" ".
                    "src=\"{$country_icon}\" ".
                    "alt=\"{$country_name}\" ".
                    "title=\"{$country_name}\" ".
                "/>"
            /*
            'language_code' => $this->fixture->extractLanguageCode($locale),
            'language_name' => $this->fixture->getLanguageName($language_code),
            'language_icon' => $this->fixture->getLanguageIconUrl($language_code),
            'language_img' => $this->fixture->getLanguageImgHtml($language_code),
            'country_code' => $this->fixture->extractCountryCode($locale),
            'country_name' => $this->fixture->getCountryName($country_code),
            'country_icon' => $this->fixture->getCountryIconUrl($country_code),
            'country_img' => $this->fixture->getCountryImgHtml($country_code),
            */
            );

        $result = $this->fixture->getFullInfo($locale);
        $this->assertEquals($expceted_result, $result);
    }

}

?>
