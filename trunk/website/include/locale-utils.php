<?php

    // Utils to support locales

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    
    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    require_once("include/PhetException.php");
    require_once("include/locale-codes-country.php");
    require_once("include/locale-codes-language.php");

    // These defines should NOT be used outside this file.  Use the
    // DEFAULT_LOCALE below
    define('DEFAULT_LOCALE_SHORT_FORM', 'en');
    define('DEFAULT_LOCALE_LONG_FORM', 'en_US');

    // Shortcut everything will use.  Idea is that it is short
    // and understandable and may be "upgraded" to the long
    // form in the future.
    //
    // WARNING: if you change this to the long, check where it is
    // used.  For example, the default locale for JNLP files is
    // "sim-name.jnlp" (vs "sim-name_locale.jnlp" for all other
    // locales), compared to the default locale for Flash sims:
    // "sim-name-en.html".  Downloadadable localized jars are also
    // affected.  Also update the default LOCALE command line script
    // in cl_utils/pyphetutil/simutil.py.  Just check everything, yes?
    define('DEFAULT_LOCALE', DEFAULT_LOCALE_SHORT_FORM);

    //
    // Defines if interest to this module
    //

    // Regular expression for parsing locales.  Laungage code is
    // required, country code is optional.
    // Index results:
    //   0  searched string
    //   1  language code
    //   2  underscore with country code
    //   3  country code (without underscore)
    define('LOCALE_REGEX', '^([a-z]{2})(_([A-Z]{2}))?$');
    define('RE_LOCALE', 0);
    define('RE_LANGUAGE', 1);
    define('RE_COUNTRY', 3);

    $language_map = locale_get_language_map();
    $country_map = locale_get_country_map();

    $fake_language_to_locale = array('bp' => 'pt_BR', 'tc' => 'zh_TW');

    /**
     * Until everything supports the long for locale, remap the old combined
     * language codes to the long form locale.  The old combined language
     * code must still apper in the langages table.
     *
     * Validity checking is NOT performed.
     *
     * @param string $locale Old style language code, or short or long form locale
     * @return string New long form locale if argument is old style combined langage code, or the locale that was passed
     */
    function locale_remap_combined_language_code($locale) {
        // Slight hack:
        // This function is used in the service of displaying stuff on the web page
        // (as opposed to using it to find sim files).
        // "Promote" the old combined language codes to a full long form locale
        global $fake_language_to_locale;
        if (array_key_exists($locale, $fake_language_to_locale)) {
            return $fake_language_to_locale[$locale];
        }
        else {
            return $locale;
        }
    }

    /**
     * Determine if the locale is a valid short or long form locale.
     *
     * The code itself must be valid, and the language and coutry code
     * must be supported (they must appear in their respective language
     * or country table).
     *
     * @param string $locale Locale to test
     * @return bool True if valid and supported, false otherwise
     */
    function locale_is_default($locale) {
        return ((0 == strcmp($locale, DEFAULT_LOCALE_SHORT_FORM)) ||
                (0 == strcmp($locale, DEFAULT_LOCALE_LONG_FORM)));
    }

    /**
     * Determine if the language code is supported by the locale utils
     *
     * @param string $language_code Two letter lowercase language code
     * @return bool True if it is valid, false otherwise
     */
    function locale_valid_language_code($language_code) {
        global $language_map;
        if (gettype($language_code) != 'string') {
            return false;
        }

        return array_key_exists($language_code, $language_map);
    }

    /**
     * Determine if the country code is supported by the locale utils
     *
     * @param string $country_code Two letter uppercase language code
     * @return bool True if it is valid, false otherwise
     */
    function locale_valid_country_code($country_code) {
        global $country_map;
        if (gettype($country_code) != 'string') {
            return false;
        }

        return array_key_exists($country_code, $country_map);
    }

    /**
     * Determine if the locale is supported by the locale utils.
     *
     * Two form are supported:
     *     Short form: just the language code (eg 'en')
     *     Long form: language and country code (eg. 'en_US')
     * 
     * @param string $locale Short or long form locale to test
     * @return bool True if locale is valid, false otherwise
     */
    function locale_valid($locale) {
        // Check the type
        if (gettype($locale) != 'string') {
            return false;
        }

        // Match the locale with a regex
        $regs = array();
        $result = ereg(LOCALE_REGEX, $locale, $regs);
        if (false === $result) {
            // Regex failed, not valid
            return false;
        }

        // Check if it is long form (country present)
        if ($regs[RE_COUNTRY]) {
            if (!locale_valid_country_code($regs[RE_COUNTRY])) {
                // Country present and invalid, return false
                return false;
            }
        }

        if ($regs[RE_LANGUAGE]) {
            // Either short form or long form with valid country,
            // return result of language code check
            return locale_valid_language_code($regs[RE_LANGUAGE]);
        }

        // Shouldn't get here, catch all
        return false;
    }

    /**
     * Given a locale, check if the language code is present and valid
     *
     * Note: Validity of country component is ignored but locale must be of proper form
     *
     * @param string $locale Short or long form locale to test
     * @return bool True if language is present and valid, false otherwise
     */
    function locale_has_valid_language_code($locale) {
        if (gettype($locale) != 'string') {
            return false;
        }

        $regs = array();
        $result = ereg(LOCALE_REGEX, $locale, $regs);
        if (false === $result) {
            return false;
        }
        else if (false === $regs[RE_LANGUAGE]) {
            return false;
        }

        return locale_valid_language_code($regs[RE_LANGUAGE]);
    }

    /**
     * Given locale, check if the country code is present and valid
     *
     * Note: Validity of language component is ignored but locale must be of proper form
     *
     * @param string $locale Short or long form locale to test
     * @return bool True if country is present and valid, false otherwise
     */
    function locale_has_valid_country_code($locale) {
        if (gettype($locale) != 'string') {
            return false;
        }

        $regs = array();
        $result = ereg(LOCALE_REGEX, $locale, $regs);
        if (false === $result) {
            return false;
        }
        else if (false === $regs[RE_COUNTRY]) {
            return false;
        }

        return locale_valid_country_code($regs[RE_COUNTRY]);
    }

    /**
     * Extract the two letter language code from the locale
     *
     * @param string $locale Short or long form locale
     * @return string Two letter lowercase language code
     * @exception PhetLocaleException if the locale or language code is invalid
     */
    function locale_extract_language_code($locale) {
        // Check the type
        if (gettype($locale) != 'string') {
            $type = gettype($locale);
            $msg = "Cannot extract language code, bad type '{$type}'";
            throw new PhetLocaleException($msg);
        }

        $regs = array();
        $result = ereg(LOCALE_REGEX, $locale, $regs);
        if (false === $result) {
            $msg = "Cannot extract language code, invalid locale '{$locale}'";
            throw new PhetLocaleException($msg);
        }

        if (!locale_valid_language_code($regs[RE_LANGUAGE])) {
            $msg = "Cannot extract language code, invalid language '{$regs[RE_LANGUAGE]}'";
            throw new PhetLocaleException($msg);
        }

        return $regs[RE_LANGUAGE];
    }

    /**
     * Extract the two letter country code from the locale
     *
     * @param string $locale Long form locale
     * @return string Two letter uppercase country code
     * @exception PhetLocaleException if the locale is short form, invalid, or country code is invalid
     */
    function locale_extract_country_code($locale) {
        // Check the type
        if (gettype($locale) != 'string') {
            $type = gettype($locale);
            $msg = "Cannot extract country code, bad type '{$type}'";
            throw new PhetLocaleException($msg);
        }

        $regs = array();
        $result = ereg(LOCALE_REGEX, $locale, $regs);
        if (false === $result) {
            $msg = "Cannot extract country code, invalid locale '{$locale}'";
            throw new PhetLocaleException($msg);
        }
        else if (false === $regs[RE_COUNTRY]) {
            $msg = "Cannot extract country code, country not present in locale '{$locale}'";
            throw new PhetLocaleException($msg);
        }

        if (!locale_valid_country_code($regs[RE_COUNTRY])) {
            $msg = "Cannot extract country code, invalid country '{$regs[RE_COUNTRY]}'";
            throw new PhetLocaleException($msg);
        }

        return $regs[RE_COUNTRY];
    }

    /**
     * Get the English language name for the given language code
     *
     * @param string $language_code Two letter lowercase language code
     * @return string English name of language
     * @exception PhetLocaleException if the language code is not valid
     */
     function locale_get_language_name($language_code) {
        global $language_map;

        if (!locale_valid_language_code($language_code)) {
            $msg = "Invalid langage code '{$language_code}'";
            throw new PhetLocaleException($msg);
        }

        return $language_map[$language_code];
    }

    /**
     * Get the English country name for the given country code
     *
     * @param string $country_code Two letter uppercase country code
     * @return string English name of country
     * @exception PhetLocaleException if the language code is not valid
     */
    function locale_get_country_name($country_code) {
        global $country_map;

        if (!locale_valid_country_code($country_code)) {
            $msg = "Invalid country code '{$country_code}'";
            throw new PhetLocaleException($msg);
        }

        return $country_map[$country_code];
    }

    /**
     * Get the English language and country name (if present) of the form 'Language' or 'Language, Country'
     *
     * @param string $locale Short or long form locale
     * @return string English language and country name (if present)
     * @exception PhetLocaleException if the locale is invalid
     */
    function locale_get_locale_name($locale) {
        if (!locale_valid($locale)) {
            $msg = "Invalid locale '{$locale}'";
            throw new PhetLocaleException($msg);
        }

        $language_code = locale_extract_language_code($locale);
        $language_name = locale_get_language_name($language_code);

        $country_name = '';
        if (locale_has_valid_country_code($locale)) {
            $country_code = locale_extract_country_code($locale);
            $country_name = ', '.locale_get_country_name($country_code);
        }

        return $language_name.$country_name;
    }

    /**
     * Given two two-letter language codes, sort them in alphabetical order based on the English language name
     *
     * Note: This does NOT sort the codes into alphabetical order
     *
     * @param string $a Two letter lowercase language code
     * @param string $b Two letter lowercase language code
     * @return int Result of comparison: <0, 0, >0
     * @exception PhetLocaleException if either if the codes are invalid
     */
    function locale_language_sort_code_by_name($a, $b) {
        // This top part is temporary until there is support for long form locales everywhere
        $a1 = locale_extract_language_code(locale_remap_combined_language_code($a));
        $b1 = locale_extract_language_code(locale_remap_combined_language_code($b));

        return strcmp(locale_get_language_name($a1), locale_get_language_name($b1));
    }

    /**
     * Given two two-letter country codes, sort them in alphabetical order based on the English country name
     *
     * Note: This does NOT sort the codes into alphabetical order
     *
     * @param string $a Two letter uppercase country code
     * @param string $b Two letter uppercase country code
     * @return int Result of comparison: <0, 0, >0
     * @exception PhetLocaleException if either of the codes are invalid
     */
    function locale_country_sort_code_by_name($a, $b) {
        // This top part is temporary until there is support for long form locales everywhere
        $a2 = locale_remap_combined_language_code($a);
        if ($a != $a2) $a = locale_extract_country_code($a);
        $b2 = locale_remap_combined_language_code($b);
        if ($b != $b2) $a = locale_extract_country_code($a);

        return strcmp(locale_get_country_name($a), locale_get_country_name($b));
    }

    /**
     * Given 2 locales, sort them in alphabetical order based on the English language name, then English country name (if present)
     *
     * @param string $a Short or long form locale code
     * @param string $b Short or long form locale code
     * @return int Result of comparison: <0, 0, >0
     * @exception PhetLocaleException if either of the codes are invalid
     */
    function locale_sort_code_by_name($a, $b) {
        $a1 = locale_remap_combined_language_code($a);
        $b1 = locale_remap_combined_language_code($b);

        // First do languages
        $lang_a = locale_extract_language_code($a1);
        $lang_b = locale_extract_language_code($b1);
        $result = locale_language_sort_code_by_name($lang_a, $lang_b);
        if (0 !== $result) {
            return $result;
        }

        // Language codes are the same, try the countries
        // which may or may not be present
        $valid_country_a = locale_has_valid_country_code($a);
        $valid_country_b = locale_has_valid_country_code($b);
        if ((!$valid_country_a) && (!$valid_country_b)) {
            return 0;
        }
        else if ((!$valid_country_a) && ($valid_country_b)) {
            return -1;
        }
        else if (($valid_country_a) && (!$valid_country_b)) {
            return 1;
        }
        else {
            $country_a = locale_extract_country_code($a);
            $country_b = locale_extract_country_code($b);
            return locale_country_sort_code_by_name($country_a, $country_b);
        }
    }

    /**
     * Return a path to the image representing the language code
     *
     * @param string $language_code Two letter lowercase language code
     * @return string Relative path from SITE_ROOT to the language image file
     * @exception PhetLocaleException if the code is invalid
     */
    function locale_get_language_icon_url($language_code) {
        if (!locale_valid_language_code($language_code)) {
            $msg = "Cannot find path to langaugae icon, invalid language code '{$language_code}'";
            throw new PhetLocaleException($msg);
        }

        $language_name = locale_get_language_name($language_code);
        $icon = strtolower("{$language_name}-{$language_code}.png");
        return SITE_ROOT."images/languages/{$icon}";
    }

    /**
     * Return a path to the image representing the country code
     *
     * @param string $country_code Two letter uppercase country code
     * @return string Relative path from SITE_ROOT to the country image file
     * @exception PhetLocaleException if the code is invalid
     */
    function locale_get_country_icon_url($country_code) {
        if (!locale_valid_country_code($country_code)) {
            $msg = "Cannot find path to langaugae icon, invalid country code '{$country_code}'";
            throw new PhetLocaleException($msg);
        }

        return SITE_ROOT."images/countries/{$country_code}.png";
    }

    /**
     * Get the full img tag for the language
     *
     * @param string $language_code Two letter lowercase country code
     * @return string HTML to render the image
     * @exception PhetLocaleException if the language code is invalid
     */
    function locale_get_language_img_html($language_code) {
        $language_name = locale_get_language_name($language_code);
        $language_icon = locale_get_language_icon_url($language_code);
        return
                "<img ".
                    "class=\"language\" ".
                    "src=\"{$language_icon}\" ".
                    "alt=\"{$language_name}\" ".
                    "title=\"{$language_name}\" ".
                "/>";

    }

    /**
     * Get the full img tag for the country
     *
     * @param string $country_code Two letter lowercase country code
     * @return string HTML to render the image
     * @exception PhetLocaleException if the country code is invalid
     */
    function locale_get_country_img_html($country_code) {
        $country_name = locale_get_country_name($country_code);
        $country_icon = locale_get_country_icon_url($country_code);
        return
                "<img ".
                    "class=\"country\" ".
                    "src=\"{$country_icon}\" ".
                    "alt=\"{$country_name}\" ".
                    "title=\"{$country_name}\" ".
                "/>";

    }

    /**
     * Convenience function, get just about all the info we want with a locale in one easy place
     *
     * @param string $locale Short or long form locale
     * @return mixed False if locale is invalid, else an array of locale info
     */
    function locale_get_full_info($locale) {
        if (!locale_valid($locale)) {
            return false;
        }

        $locale = locale_remap_combined_language_code($locale);

        $info = array();

        $info['locale'] = $locale;
        $info['locale_name'] = locale_get_locale_name($locale);

        $info['language_code'] = locale_extract_language_code($locale);
        $info['language_name'] = locale_get_language_name($info['language_code']);
        $info['language_icon'] = locale_get_language_icon_url($info['language_code']);
        $info['language_img'] = locale_get_language_img_html($info['language_code']);

        if (locale_has_valid_country_code($locale)) {
            $info['country_code'] = locale_extract_country_code($locale);
            $info['country_name'] = locale_get_country_name($info['country_code']);
            $info['country_icon'] = locale_get_country_icon_url($info['country_code']);
            $info['country_img'] = locale_get_country_img_html($info['country_code']);
        }
        else {
            $info['country_code'] = null;
            $info['country_name'] = null;
            $info['country_icon'] = null;
            $info['country_img'] = null;
        }

        return $info;
    }
?>
