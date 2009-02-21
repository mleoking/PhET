<?php

require_once('PHPUnit/Framework.php');

// SITE_ROOT - Relative path to website's top directory from the tested file
if (!defined('SITE_ROOT')) define('SITE_ROOT', '../');

// Get the test globals to set everything up
require_once(dirname(dirname(__FILE__)).'/test_global.php');

// Get the file to test
require_once("include/locale-utils.php");

class localeUtilsTest extends PHPUnit_Framework_TestCase {
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
     *
     * Testing locale_remap_combined_language_code()
     *
     */

    public function testLocaleRemapCombinedLangageCode_returnsExpetedRemappedCodes() {
        // combined_code => expected_remap
        $combined_codes = array('bp' => 'pt_BR', 'tc' => 'zh_TW');
        foreach ($combined_codes as $old => $new) {
            $result = locale_remap_combined_language_code($old);
            $this->assertEquals($new, $result);
        }
    }

    public function testLocaleRemapCombinedLangageCode_nonRemappedCodeRetursUnchanged() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale = $language_code.'_'.$country_code;

        $result = locale_remap_combined_language_code($locale);
        $this->assertEquals($locale, $result);
    }

    /**
     *
     * Testing locale_is_default()
     *
     */

    public function testLocaleIsDefault_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing locale_valid_language_code()
     *
     */
    
    public function testLocaleValidLanguageCode_validCodeReturnsTrue() {
        $language_code = $this->getValidLanguageCode();

        $result = locale_valid_language_code($language_code);
        $this->assertTrue($result);
    }

    public function testLocaleValidLanguageCode_invalidCodeReturnsFalse() {
        $language_code = $this->getInvalidLanguageCode();

        $result = locale_valid_language_code($language_code);
        $this->assertFalse($result);
    }

    public function testLocaleValidLanguageCode_invalidStringReturnsFalse() {
        $language_code = '_foo';
        $this->assertFalse(array_key_exists($language_code, $this->language_map));

        $result = locale_valid_language_code($language_code);
        $this->assertFalse($result);
    }

    public function testLocaleValidLanguageCode_invalidTypeReturnsFalse() {
        $language_code = array();

        $result = locale_valid_language_code($language_code);
        $this->assertFalse($result);
    }

    /**
     *
     * Testing locale_valid_country_code()
     *
     */

    public function testLocaleValidCountryCode_validCodeReturnsTrue() {
        $country_code = $this->getValidCountryCode();

        $result = locale_valid_country_code($country_code);
        $this->assertTrue($result);
    }

    public function testLocaleValidCountryCode_invalidCodeReturnsFalse() {
        // Find a country that is not supported
        $country_code = $this->getInvalidCountryCode();

        $result = locale_valid_country_code($country_code);
        $this->assertFalse($result);
    }

    public function testLocaleValidCountryCode_invalidStringReturnsFalse() {
        $country_code = '_FOO';
        $this->assertFalse(array_key_exists($country_code, $this->country_map));

        $result = locale_valid_country_code($country_code);
        $this->assertFalse($result);
    }

    public function testLocaleValidCountryCode_invalidTypeReturnsFalse() {
        $country_code = array();

        $result = locale_valid_country_code($country_code);
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

        $result = locale_valid($locale);
        $this->assertTrue($result);
    }

    public function testLocaleValid_validLongFormReturnsTrue() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale = $language_code.'_'.$country_code;
        
        // Test the code
        $result = locale_valid($locale);
        $this->assertTrue($result);
    }

    public function testLocaleValid_longFormInvalidLangugaeReturnsFalse() {
        $language_code = $this->getInvalidLanguageCode();
        $country_code = $this->getValidCountryCode();

        // Create the locale
        $locale = $language_code.'_'.$country_code;
        
        // Test the code
        $result = locale_valid($locale);
        $this->assertFalse($result);
    }

    public function testLocaleValid_longFormInvalidCountryReturnsFalse() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getInvalidCountryCode();

        // Create the locale
        $locale = $language_code.'_'.$country_code;
        
        // Test the code
        $result = locale_valid($locale);
        $this->assertFalse($result);
    }

    public function testLocaleValid_emptyStringReturnsFalse() {
        $result = locale_valid('');
        $this->assertFalse($result);
    }
    
    public function testLocaleValid_badTypeReturnsFalse() {
        $result = locale_valid(array('en', 'US'));
        $this->assertFalse($result);
    }

    public function testLocaleValid_invalidStringReturnsFalse() {
        $result = locale_valid('foo bar');
        $this->assertFalse($result);
    }

    /**
     *
     * Testing locale_has_valid_language_code()
     *
     */

    public function testLocaleHasValidLanguageCode_shortFormValidLanguageReturnsTrue() {
        $language_code = $this->getValidLanguageCode();
        $locale = $language_code;

        $result = locale_has_valid_language_code($locale);
        $this->assertTrue($result);
    }

    public function testLocaleHasValidLanguageCode_shortFormInvalidLanguageReturnsFalse() {
        $language_code = $this->getInvalidLanguageCode();
        $locale = $language_code;

        $result = locale_has_valid_language_code($locale);
        $this->assertFalse($result);
    }

    public function testLocaleHasValidLanguageCode_validLongFormReturnsTrue() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale = $language_code.'_'.$country_code;

        $result = locale_has_valid_language_code($locale);
        $this->assertTrue($result);
    }

    public function testLocaleHasValidLanguageCode_longFormValidLanguageInvalidCountryReturnsTrue() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getInvalidCountryCode();

        $locale = $language_code.'_'.$country_code;
        $result = locale_has_valid_language_code($locale);
        $this->assertTrue($result);
    }

    public function testLocaleHasValidLanguageCode_longFormInvalidLanguageValidCountryReturnsFalse() {
        $language_code = $this->getInvalidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale = $language_code.'_'.$country_code;

        $result = locale_has_valid_language_code($locale);
        $this->assertFalse($result);
    }

    public function testLocaleHasValidLanguageCode_invalidFormReturnsFalse() {
        $locale = 'foo_BAR';

        $result = locale_has_valid_language_code($locale);
        $this->assertFalse($result);
    }

    /**
     *
     * Testing locale_has_valid_country_code()
     *
     */

    public function testLocaleHasValidCountryCode_shortFormReturnsFalse() {
        $language_code = $this->getValidLanguageCode();
        $locale = $language_code;

        $result = locale_has_valid_country_code($locale);
        $this->assertFalse($result);
    }

    public function testLocaleHasValidCountryCode_shortFormInvalidLanguageReturnsFalse() {
        $language_code = $this->getInvalidLanguageCode();
        $locale = $language_code;

        $result = locale_has_valid_language_code($locale);
        $this->assertFalse($result);
    }

    public function testLocaleHasValidCountryCode_validLongFormReturnsTrue() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale = $language_code.'_'.$country_code;

        $result = locale_has_valid_country_code($locale);
        $this->assertTrue($result);
    }

    public function testLocaleHasValidCountryCode_longFormValidLanguageInvalidCountryReturnsFalse() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getInvalidCountryCode();
        $locale = $language_code.'_'.$country_code;

        $result = locale_has_valid_country_code($locale);
        $this->assertFalse($result);
    }

    public function testLocaleHasValidCountryCode_longFormInvalidLanguageValidCountryReturnsTrue() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale = $language_code.'_'.$country_code;

        $result = locale_has_valid_country_code($locale);
        $this->assertTrue($result);
    }

    public function testLocaleHasValidCountryCode_invalidFormReturnsFalse() {
        $locale = 'foo_BAR';

        $result = locale_has_valid_country_code($locale);
        $this->assertFalse($result);
    }

    /**
     *
     * Testing locale_extract_language_code()
     *
     */

    public function testLocaleExtractLanguageCode_shortFormLocaleWithValidLanguageCodeReturnsCode() {
        $language_code = $this->getValidLanguageCode();
        $locale_code = $language_code;

        $result = locale_extract_language_code($locale_code);
        $this->assertEquals($language_code, $result);
    }

    public function testLocaleExtractLanguageCode_shortFormLocaleWithInvalidLanguageCodeRaisesException() {
        // Find a language that is not supported
        $language_code = $this->getInvalidLanguageCode();

        $this->setExpectedException('PhetLocaleException');
        locale_extract_language_code($language_code);
    }

    public function testLocaleExtractLanguageCode_longFormLocaleWithValidLanguageAndValidCountryCodeCodeReturnsCode() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();

        // Create the locale
        $locale_code = $language_code.'_'.$country_code;

        $result = locale_extract_language_code($locale_code);
        $this->assertEquals($language_code, $result);
    }

    public function testLocaleExtractLanguageCode_longFormLocaleWithValidLanguageAndInvalidCountryCodeReturnsCode() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getInvalidCountryCode();

        // Add a country code, the acutal code doesn't matter
        $locale_code = $language_code.'_'.$country_code;

        $result = locale_extract_language_code($locale_code);
        $this->assertEquals($language_code, $result);
    }

    public function testLocaleExtractLanguageCode_invalidTypeRaisesException() {
        $this->setExpectedException('PhetLocaleException');
        locale_extract_language_code(array());
    }

    /**
     *
     * Testing locale_extract_country_code()
     *
     */

    public function testLocaleExtractCountryCode_shortFormLocaleRaisesException() {
        $language_code = $this->getValidLanguageCode();
        $locale_code = $language_code;
        
        $this->setExpectedException('PhetLocaleException');
        locale_extract_country_code($locale_code);
    }

    public function testLocaleExtractCountryCode_longFormLocaleWithValidLangugeAndValidCountryCodeReturnsCode() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale_code = $language_code."_".$country_code;

        $result = locale_extract_country_code($locale_code);
        $this->assertEquals($country_code, $result);
    }

    public function testLocaleExtractCountryCode_longFormLocaleWithInvalidLangugeAndValidCountryCodeReturnsCode() {
        $language_code = $this->getInvalidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale_code = $language_code."_".$country_code;

        $result = locale_extract_country_code($locale_code);
        $this->assertEquals($country_code, $result);
    }

    public function testLocaleExtractCountryCode_localeWithInvalidCountryCodeRaisesException() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getInvalidCountryCode();
        $locale_code = $language_code."_".$country_code;

        $this->setExpectedException('PhetLocaleException');
        locale_extract_country_code($locale_code);
    }

    public function testLocaleExtractCountryCode_invalidTypeRaisesException() {
        $this->setExpectedException('PhetLocaleException');
        locale_extract_country_code(array());
    }

    /**
     *
     * Testing locale_get_language_name()
     *
     */

    public function testLocaleGetLanguageName_validCodeReturnsName() {
        $language_code = $this->getValidLanguageCode();

        $result = locale_get_language_name($language_code);
        $this->assertEquals($this->language_map[$language_code], $result);
    }

    public function testLocaleGetLanguageName_invalidCodeRaisesException() {
        $language_code = $this->getInvalidLanguageCode();

        $this->setExpectedException('PhetLocaleException');
        locale_get_language_name($language_code);
    }

    public function testLocaleGetLanguageName_invalidStringRaisesException() {
        $language_code = '_foo';
        $this->assertFalse(array_key_exists($language_code, $this->language_map));

        $this->setExpectedException('PhetLocaleException');
        locale_get_language_name($language_code);
    }

    public function testLocaleGetLanguageName_invalidTypeRaisesException() {
        $this->setExpectedException('PhetLocaleException');
        locale_get_language_name(array());
    }

    /**
     *
     * Testing locale_get_country_name()
     *
     */

    public function testLocaleGetCountryName_validCodeReturnsName() {
        $country_code = $this->getValidCountryCode();
        $result = locale_get_country_name($country_code);
        $this->assertEquals($this->country_map[$country_code], $result);
    }

    public function testLocaleGetCountryName_invalidCodeRaisesException() {
        // Find a country that is not supported
        $country_code = $this->getInvalidCountryCode();

        $this->setExpectedException('PhetLocaleException');
        locale_get_country_name($country_code);
    }

    public function testLocaleGetCountryName_invalidStringRaisesException() {
        $country_code = '_foo';
        $this->assertFalse(array_key_exists($country_code, $this->country_map));

        $this->setExpectedException('PhetLocaleException');
        locale_get_country_name($country_code);
    }

    public function testLocaleGetCountryName_invalidTypeRaisesException() {
        $this->setExpectedException('PhetLocaleException');
        locale_get_country_name(array());
    }

    /**
     *
     * Testing locale_get_locale_name()
     *
     */

    public function testLocaleGetLocaleName_validShortFormReturnsExpectedString() {
        $language_code = $this->getValidLanguageCode();
        $locale_code = $language_code;

        $expected_result = "{$this->language_map[$language_code]}";

        $result = locale_get_locale_name($locale_code);
        $this->assertEquals($expected_result, $result);
    }
    
    public function testLocaleGetLocaleName_validLongFormReturnsExpectedString() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale_code = $language_code."_".$country_code;

        $expected_result = "{$this->language_map[$language_code]}, {$this->country_map[$country_code]}";

        $result = locale_get_locale_name($locale_code);
        $this->assertEquals($expected_result, $result);
    }
    
    public function testLocaleGetLocaleName_longFormValidLanguageInvalidCountryRaisesException() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getInvalidCountryCode();
        $locale_code = $language_code."_".$country_code;

        $this->setExpectedException('PhetLocaleException');
        locale_get_locale_name($locale_code);
    }
    
    public function testLocaleGetLocaleName_longFormInvalidLanguageValidCountryRaisesException() {
        $language_code = $this->getInvalidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale_code = $language_code."_".$country_code;

        $this->setExpectedException('PhetLocaleException');
        locale_get_locale_name($locale_code);
    }
    
    public function testLocaleGetLocaleName_longFormInvalidLanguageInvalidCountryRaisesException() {
        $language_code = $this->getInvalidLanguageCode();
        $country_code = $this->getInvalidCountryCode();
        $locale_code = $language_code."_".$country_code;

        $this->setExpectedException('PhetLocaleException');
        locale_get_locale_name($locale_code);
    }
    
    public function testLocaleGetLocaleName_invalidLongFormRaisesException() {
        $locale_code = 'foo_BAR';

        $this->setExpectedException('PhetLocaleException');
        locale_get_locale_name($locale_code);
    }
    
    /**
     *
     * Testing locale_language_sort_code_by_name()
     *
     */

    public function testLocaleLanguageSortCodeByName_sortsInCorrectOrderGreaterThan() {
        $result = locale_language_sort_code_by_name('eu', 'sq');
        $this->assertGreaterThan(0, $result);
    }

    public function testLocaleLanguageSortCodeByName_sortsInCorrectOrderLessThan() {
        $result = locale_language_sort_code_by_name('sq', 'eu');
        $this->assertLessThan(0, $result);
    }

    public function testLocaleLanguageSortCodeByName_sortsInCorrectOrderEquals() {
        $result = locale_language_sort_code_by_name('he', 'iw');
        $this->assertEquals(0, $result);
    }

    public function testLocaleLanguageSortCodeByName_sortsInCorrectOrderArray() {
        $codes_to_sort = array('aa', 'an', 'bs', 'eu', 'hy', 'sq', 'bp', 'tc');
        $expected_order = array('aa', 'sq', 'an', 'hy', 'eu', 'bs', 'tc', 'bp');
        usort($codes_to_sort, 'locale_language_sort_code_by_name');
        $this->assertEquals($expected_order, $codes_to_sort);
    }

    /**
     *
     * Testing locale_country_sort_code_by_name()
     *
     */

    public function testLocaleCountrySortCodeByName_sortsInCorrectOrderGreaterThan() {
        $result = locale_country_sort_code_by_name('AS', 'DZ');
        $this->assertGreaterThan(0, $result);
    }

    public function testLocaleCountrySortCodeByName_sortsInCorrectOrderLessThan() {
        $result = locale_country_sort_code_by_name('AU', 'AT');
        $this->assertLessThan(0, $result);
    }

    public function testLocaleCountrySortCodeByName_sortsInCorrectOrderEquals() {
        $result = locale_country_sort_code_by_name('US', 'US');
        $this->assertEquals(0, $result);
    }

    public function testLocaleCountrySortCodeByName_sortsInCorrectOrderArray() {
        $codes_to_sort = array('AS', 'AU', 'AX', 'BN', 'DZ', 'IO');
        $expected_order = array('AX', 'DZ', 'AS', 'AU', 'IO', 'BN');
        usort($codes_to_sort, 'locale_country_sort_code_by_name');
        $this->assertEquals($expected_order, $codes_to_sort);
    }

    /**
     *
     * Testing locale_sort_code_by_name()
     *
     */

    public function testLocaleSortCodeByName_shortFormLanguageSortsInCorrectOrderGreaterThan() {
        $result = locale_sort_code_by_name('en', 'aa');
        $this->assertGreaterThan(0, $result);
    }

    public function testLocaleSortCodeByName_shortFormLanguageSortsInCorrectOrderLessThan() {
        $result = locale_sort_code_by_name('aa', 'en');
        $this->assertLessThan(0, $result);
    }

    public function testLocaleSortCodeByName_shortFormLanguageSortsInCorrectOrderEquals() {
        $result = locale_sort_code_by_name('en', 'en');
        $this->assertEquals(0, $result);
    }

    public function testLocaleSortCodeByName_longFormDifferentLanguageSortsInCorrectOrderGreaterThan() {
        $result = locale_sort_code_by_name('en_GB', 'zh_TW');
        $this->assertGreaterThan(0, $result);
    }

    public function testLocaleSortCodeByName_longFormDifferentLanguageSortsInCorrectOrderLessThan() {
        $result = locale_sort_code_by_name('zh_TW', 'en_GB');
        $this->assertLessThan(0, $result);
    }

    public function testLocaleSortCodeByName_longFormSameLanguageSortsInCorrectOrderEquals() {
        $result = locale_sort_code_by_name('en_GB', 'en_GB');
        $this->assertEquals(0, $result);
    }

    public function testLocaleSortCodeByName_longFormSameLanguageSortsInCorrectOrderGreaterThan() {
        $result = locale_sort_code_by_name('en_US', 'en_GB');
        $this->assertGreaterThan(0, $result);
    }

    public function testLocaleSortCodeByName_longFormSameLanguageSortsInCorrectOrderLessThan() {
        $result = locale_sort_code_by_name('en_GB', 'en_US');
        $this->assertLessThan(0, $result);
    }

    public function testLocaleSortCodeByName_mixedFormDifferentLanguageSortsInCorrectOrderGreaterThan() {
        $result = locale_sort_code_by_name('en_GB', 'zh');
        $this->assertGreaterThan(0, $result);
    }

    public function testLocaleSortCodeByName_mixedFormDifferentLanguageSortsInCorrectOrderLessThan() {
        $result = locale_sort_code_by_name('zh_TW', 'en');
        $this->assertLessThan(0, $result);
    }

    public function testLocaleSortCodeByName_mixedFormSameLanguageSortsInCorrectOrderGreaterThan() {
        $result = locale_sort_code_by_name('en_US', 'en');
        $this->assertGreaterThan(0, $result);
    }

    public function testLocaleSortCodeByName_mixedFormSameLanguageSortsInCorrectOrderLessThan() {
        $result = locale_sort_code_by_name('en', 'en_US');
        $this->assertLessThan(0, $result);
    }

    public function testLocaleSortCodeByName_mixedFormExpectedSortOrder() {
        $locales = array('en_US', 'en_GB', 'en', 'zh_TW', 'zh_CN', 'zh');
        $expected_orders = array('zh', 'zh_CN', 'zh_TW', 'en', 'en_GB', 'en_US');

        $sort_result = usort($locales, 'locale_sort_code_by_name');
        $this->assertEquals($expected_orders, $locales);
    }

    /**
     *
     * Testing locale_get_language_icon_url()
     *
     */

    public function testLocaleGetLanguageIconUrl_validLanguageCodeReturnsExpected() {
        $language_code = $this->getValidLanguageCode();

        $language_name = locale_get_language_name($language_code);
        $exepcted_result = SITE_ROOT."images/languages/".strtolower("{$language_name}-{$language_code}.png");

        $result = locale_get_language_icon_url($language_code);
        $this->assertEquals($exepcted_result, $result);
    }

    public function testLocaleGetLanguageIconUrl_invalidLanguageCodeRaisesException() {
        $language_code = $this->getInvalidLanguageCode();
        $this->setExpectedException('PhetLocaleException');
        locale_get_language_icon_url($language_code);
    }

    public function testLocaleGetLanguageIconUrl_invalidTypeRaisesException() {
        $this->setExpectedException('PhetLocaleException');
        locale_get_language_icon_url(array());
    }

    public function testLocaleGetLanguageIconUrl_invalidStringRaisesException() {
        $this->setExpectedException('PhetLocaleException');
        locale_get_language_icon_url('foo_BAR');
    }

    /**
     *
     * Testing locale_get_country_icon_url()
     *
     */

    public function testLocaleGetCountryIconUrl_validCountryCodeReturnsExpected() {
        $country_code = $this->getValidCountryCode();

        $country_name = locale_get_country_name($country_code);
        $exepcted_result = SITE_ROOT."images/countries/{$country_code}.png";

        $result = locale_get_country_icon_url($country_code);
        $this->assertEquals($exepcted_result, $result);
    }

    public function testLocaleGetCountryIconUrl_invalidCountryCodeRaisesException() {
        $country_code = $this->getInvalidCountryCode();
        $this->setExpectedException('PhetLocaleException');
        locale_get_country_icon_url($country_code);
    }

    public function testLocaleGetCountryIconUrl_invalidTypeRaisesException() {
        $this->setExpectedException('PhetLocaleException');
        locale_get_country_icon_url(array());
    }

    public function testLocaleGetCountryIconUrl_invalidStringRaisesException() {
        $this->setExpectedException('PhetLocaleException');
        locale_get_country_icon_url('foo_BAR');
    }

    /**
     *
     * Testing locale_get_language_img_html()
     *
     */

    public function testLocaleGetLanguageImgHtml_invalidLocaleRaisesException() {
        $language_code = $this->getInvalidLanguageCode();
        $country_code = $this->getInvalidCountryCode();
        $locale = $language_code.'_'.$country_code;
        $this->assertFalse(locale_valid($locale));

        $this->setExpectedException('PhetLocaleException');
        locale_get_language_img_html($locale);        
    }
    
    public function testLocaleGetLanguageImgHtml_validShortFormLocaleRecturnsExpected() {
        $language_code = $this->getValidLanguageCode();
        $locale = $language_code;
        $this->assertTrue(locale_valid($locale));

        $language_name = locale_get_language_name($language_code);
        $language_icon = locale_get_language_icon_url($language_code);
        $expeted_img_html = 
                "<img ".
                    "class=\"language\" ".
                    "src=\"{$language_icon}\" ".
                    "alt=\"{$language_name}\" ".
                    "title=\"{$language_name}\" ".
                "/>";

        $result = locale_get_language_img_html($locale);
        $this->assertEquals($expeted_img_html, $result);
    }
    
    public function testLocaleGetLanguageImgHtml_validLongFormLocaleRecturnsExpected() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale = $language_code.'_'.$country_code;
        $this->assertTrue(locale_valid($locale));

        $language_name = locale_get_language_name($language_code);
        $language_icon = locale_get_language_icon_url($language_code);
        $expeted_img_html = 
                "<img ".
                    "class=\"language\" ".
                    "src=\"{$language_icon}\" ".
                    "alt=\"{$language_name}\" ".
                    "title=\"{$language_name}\" ".
                "/>";

        $result = locale_get_language_img_html($language_code);
        $this->assertEquals($expeted_img_html, $result);
    }
    
    /**
     *
     * Testing locale_get_country_img_html()
     *
     */

    public function testLocaleGetCountryImgHtml_invalidLocaleRaisesException() {
        $language_code = $this->getInvalidLanguageCode();
        $country_code = $this->getInvalidCountryCode();
        $locale = $language_code.'_'.$country_code;
        $this->assertFalse(locale_valid($locale));

        $this->setExpectedException('PhetLocaleException');
        locale_get_country_img_html($locale);        
    }
    
    public function testLocaleGetCountryImgHtml_validShortFormLocaleRaisesException() {
        $language_code = $this->getValidLanguageCode();
        $locale = $language_code;
        $this->assertTrue(locale_valid($locale));

        $this->setExpectedException('PhetLocaleException');
        locale_get_country_img_html($locale);        
    }
    
    public function testLocaleGetCountryImgHtml_validLongFormLocaleRecturnsExpected() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale = $language_code.'_'.$country_code;
        $this->assertTrue(locale_valid($locale));

        $country_name = locale_get_country_name($country_code);
        $country_icon = locale_get_country_icon_url($country_code);
        $expeted_img_html = 
                "<img ".
                    "class=\"country\" ".
                    "src=\"{$country_icon}\" ".
                    "alt=\"{$country_name}\" ".
                    "title=\"{$country_name}\" ".
                "/>";

        $result = locale_get_country_img_html($country_code);
        $this->assertEquals($expeted_img_html, $result);
    }
    
    /**
     *
     * Testing locale_is_default()
     *
     */

    public function testLocaleIsDefault_defaultShortFormReturnsTrue() {
        $locale = DEFAULT_LOCALE_SHORT_FORM;
        $result = locale_is_default($locale);
        $this->assertTrue($result);

        // Throw this one in too
        $locale = DEFAULT_LOCALE;
        $result = locale_is_default($locale);
        $this->assertTrue($result);
    }

    public function testLocaleIsDefault_defaultLongFormReturnsTrue() {
        $locale = DEFAULT_LOCALE_LONG_FORM;
        $result = locale_is_default($locale);
        $this->assertTrue($result);
    }

    public function testLocaleIsDefault_nondefaultShortFormReturnsFalse() {
        $locale = 'pt';
        $this->assertNotEquals(DEFAULT_LOCALE_SHORT_FORM, $locale);

        $result = locale_is_default($locale);
        $this->assertFalse($result);
    }

    public function testLocaleIsDefault_nondefaultLongFormReturnsFalse() {
        $locale = 'pt_BR';
        $this->assertNotEquals(DEFAULT_LOCALE_LONG_FORM, $locale);

        $result = locale_is_default($locale);
        $this->assertFalse($result);
    }

    /**
     *
     * Testing locale_get_full_info()
     *
     */

    public function testLocaleGetFullInfo_invalidLocaleReturnsFalse() {
        $language_code = $this->getInvalidLanguageCode();
        $country_code = $this->getInvalidCountryCode();
        $locale = $language_code.'_'.$country_code;
        $this->assertFalse(locale_valid($locale));

        $result = locale_get_full_info($locale);
        $this->assertFalse($result);
    }

    public function testLocaleGetFullInfo_validShortFromReturnsExpected() {
        $language_code = $this->getValidLanguageCode();
        $locale = $language_code;
        $this->assertTrue(locale_valid($locale));

        $expceted_result = array(
            'locale' => $locale,
            'locale_name' => locale_get_locale_name($locale),
            'language_code' => locale_extract_language_code($locale),
            'language_name' => locale_get_language_name($language_code),
            'language_icon' => locale_get_language_icon_url($language_code),
            'language_img' => locale_get_language_img_html($language_code),
            'country_code' => null,
            'country_name' => null,
            'country_icon' => null,
            'country_img' => null
            );

        $result = locale_get_full_info($locale);
        $this->assertEquals($expceted_result, $result);
    }

    public function testLocaleGetFullInfo_validLongFromReturnsExpected() {
        $language_code = $this->getValidLanguageCode();
        $country_code = $this->getValidCountryCode();
        $locale = $language_code.'_'.$country_code;
        $this->assertTrue(locale_valid($locale));

        $expceted_result = array(
            'locale' => $locale,
            'locale_name' => locale_get_locale_name($locale),
            'language_code' => locale_extract_language_code($locale),
            'language_name' => locale_get_language_name($language_code),
            'language_icon' => locale_get_language_icon_url($language_code),
            'language_img' => locale_get_language_img_html($language_code),
            'country_code' => locale_extract_country_code($locale),
            'country_name' => locale_get_country_name($country_code),
            'country_icon' => locale_get_country_icon_url($country_code),
            'country_img' => locale_get_country_img_html($country_code),
            );

        $result = locale_get_full_info($locale);
        $this->assertEquals($expceted_result, $result);
    }

}

?>
