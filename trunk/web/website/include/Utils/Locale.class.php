<?php

require_once('include/locale-codes-language.php');
require_once('include/locale-codes-country.php');

class Locale {
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
    const DEFAULT_LOCALE = 'en';

    const DEFAULT_LOCALE_SHORT_FORM = 'en';
    const DEFAULT_LOCALE_LONG_FORM = 'en_US';
    const DEFAULT_LOCALE_LANGUAGE = 'en';
    const DEFAULT_LOCALE_COUNTRY = 'US';

    // Regular expression for parsing locales.  Laungage code is
    // required, country code is optional.
    // Index results:
    //   0  searched string
    //   1  language code
    //   2  underscore with country code
    //   3  country code (without underscore)
    private static $LOCALE_REGEX = '^([a-z]{2})(_([A-Z]{2}))?$';
    private static $RE_LOCALE = 0;
    private static $RE_LANGUAGE = 1;
    private static $RE_COUNTRY = 3;

    // For singleton pattern
    private static $instance;

    private function __construct() {
    }

    private function __clone() {
    }

    public static function inst() {
        if (!isset(self::$instance)) {
            $class = __CLASS__;
            self::$instance = new $class;
        }
        return self::$instance;
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
    public function isDefault($locale) {
        return ((0 == strcmp($locale, self::DEFAULT_LOCALE_SHORT_FORM)) ||
                (0 == strcmp($locale, self::DEFAULT_LOCALE_LONG_FORM)));
    }

    /**
     * Determine if the language code is supported by the locale utils
     *
     * @param string $language_code Two letter lowercase language code
     * @return bool True if it is valid, false otherwise
     */
    public function isValidLanguageCode($language_code) {
        $language_map = locale_get_language_map();
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
    public function isValidCountryCode($country_code) {
        $country_map = locale_get_country_map();
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
    public function isValid($locale) {
        // Check the type
        if (gettype($locale) != 'string') {
            return false;
        }

        // Match the locale with a regex
        $regs = array();
        $result = ereg(self::$LOCALE_REGEX, $locale, $regs);
        if (false === $result) {
            // Regex failed, not valid
            return false;
        }

        // Check if it is long form (country present)
        if ($regs[self::$RE_COUNTRY]) {
            if (!$this->isValidCountryCode($regs[self::$RE_COUNTRY])) {
                // Country present and invalid, return false
                return false;
            }
        }

        if ($regs[self::$RE_LANGUAGE]) {
            // Either short form or long form with valid country,
            // return result of language code check
            return $this->isValidLanguageCode($regs[self::$RE_LANGUAGE]);
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
    private function hasValidLanguageCode($locale) {
        if (gettype($locale) != 'string') {
            return false;
        }

        $regs = array();
        $result = ereg(self::$LOCALE_REGEX, $locale, $regs);
        if (false === $result) {
            return false;
        }
        else if (false === $regs[self::$RE_LANGUAGE]) {
            return false;
        }

        return $this->isValidLanguageCode($regs[self::$RE_LANGUAGE]);
    }

    /**
     * Given locale, check if the country code is present and valid
     *
     * Note: Validity of language component is ignored but locale must be of proper form
     *
     * @param string $locale Short or long form locale to test
     * @return bool True if country is present and valid, false otherwise
     */
    private function hasValidCountryCode($locale) {
        if (gettype($locale) != 'string') {
            return false;
        }

        $regs = array();
        $result = ereg(self::$LOCALE_REGEX, $locale, $regs);
        if (false === $result) {
            return false;
        }
        else if (false === $regs[self::$RE_COUNTRY]) {
            return false;
        }

        return $this->isValidCountryCode($regs[self::$RE_COUNTRY]);
    }

    /**
     * Extract the two letter language code from the locale
     *
     * @param string $locale Short or long form locale
     * @return string Two letter lowercase language code
     * @exception PhetLocaleException if the locale or language code is invalid
     */
    private function extractLanguageCode($locale) {
        // Check the type
        if (gettype($locale) != 'string') {
            $type = gettype($locale);
            $msg = "Cannot extract language code, bad type '{$type}'";
            throw new PhetLocaleException($msg);
        }

        $regs = array();
        $result = ereg(self::$LOCALE_REGEX, $locale, $regs);
        if (false === $result) {
            $msg = "Cannot extract language code, invalid locale '{$locale}'";
            throw new PhetLocaleException($msg);
        }

        if (!$this->isValidLanguageCode($regs[self::$RE_LANGUAGE])) {
            $msg = "Cannot extract language code, invalid language '{$regs[self::$RE_LANGUAGE]}'";
            throw new PhetLocaleException($msg);
        }

        return $regs[self::$RE_LANGUAGE];
    }

    /**
     * Extract the two letter country code from the locale
     *
     * @param string $locale Long form locale
     * @return string Two letter uppercase country code
     * @exception PhetLocaleException if the locale is short form, invalid, or country code is invalid
     */
    private function extractCountryCode($locale) {
        // Check the type
        if (gettype($locale) != 'string') {
            $type = gettype($locale);
            $msg = "Cannot extract country code, bad type '{$type}'";
            throw new PhetLocaleException($msg);
        }

        $regs = array();
        $result = ereg(self::$LOCALE_REGEX, $locale, $regs);
        if (false === $result) {
            $msg = "Cannot extract country code, invalid locale '{$locale}'";
            throw new PhetLocaleException($msg);
        }
        else if (false === $regs[self::$RE_COUNTRY]) {
            $msg = "Cannot extract country code, country not present in locale '{$locale}'";
            throw new PhetLocaleException($msg);
        }

        if (!$this->isValidCountryCode($regs[self::$RE_COUNTRY])) {
            $msg = "Cannot extract country code, invalid country '{$regs[self::$RE_COUNTRY]}'";
            throw new PhetLocaleException($msg);
        }

        return $regs[self::$RE_COUNTRY];
    }

    /**
     * Get the English language name for the given language code
     *
     * @param string $language_code Two letter lowercase language code
     * @return string English name of language
     * @exception PhetLocaleException if the language code is not valid
     */
     private function getLanguageName($language_code) {
        $language_map = locale_get_language_map();

        if (!$this->isValidLanguageCode($language_code)) {
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
    private function getCountryName($country_code) {
        $country_map = locale_get_country_map();;

        if (!$this->isValidCountryCode($country_code)) {
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
    private function getLocaleName($locale) {
        if (!$this->isValid($locale)) {
            $msg = "Invalid locale '{$locale}'";
            throw new PhetLocaleException($msg);
        }

        $language_code = $this->extractLanguageCode($locale);
        $language_name = $this->getLanguageName($language_code);

        $country_name = '';
        if ($this->hasValidCountryCode($locale)) {
            $country_code = $this->extractCountryCode($locale);
            $country_name = ', '.$this->getCountryName($country_code);
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
    private function languageSortCodeByNameCmp($a, $b) {
        // This top part is temporary until there is support for long form locales everywhere
        $a1 = $this->extractLanguageCode($a);
        $b1 = $this->extractLanguageCode($b);

        return strcmp($this->getLanguageName($a1), $this->getLanguageName($b1));
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
    private function countrySortCodeByNameCmp($a, $b) {
        return strcmp($this->getCountryName($a), $this->getCountryName($b));
    }

    /**
     * Given 2 locales, sort them in alphabetical order based on the English language name, then English country name (if present)
     *
     * @param string $a Short or long form locale code
     * @param string $b Short or long form locale code
     * @return int Result of comparison: <0, 0, >0
     * @exception PhetLocaleException if either of the codes are invalid
     */
    public function sortCodeByNameCmp($a, $b) {
        // First do languages
        $lang_a = $this->extractLanguageCode($a);
        $lang_b = $this->extractLanguageCode($b);
        $result = $this->languageSortCodeByNameCmp($lang_a, $lang_b);
        if (0 !== $result) {
            return $result;
        }

        // Language codes are the same, try the countries
        // which may or may not be present
        $valid_country_a = $this->hasValidCountryCode($a);
        $valid_country_b = $this->hasValidCountryCode($b);
        if ((!$valid_country_a) && (!$valid_country_b)) {
            // Countries are the same
            return 0;
        }
        else if ((!$valid_country_a) && ($valid_country_b)) {
            // B has a Country, A does not
            return -1;
        }
        else if (($valid_country_a) && (!$valid_country_b)) {
            // A has a Country, B does not
            return 1;
        }
        else {
            $country_a = $this->extractCountryCode($a);
            $country_b = $this->extractCountryCode($b);
            return $this->countrySortCodeByNameCmp($country_a, $country_b);
        }
    }

    /**
     * Return a path to the image representing the language code
     *
     * @param string $language_code Two letter lowercase language code
     * @return string Relative path from SITE_ROOT to the language image file
     * @exception PhetLocaleException if the code is invalid
     */
    private function getLanguageIconUrl($language_code) {
        if (!$this->isValidLanguageCode($language_code)) {
            $msg = "Cannot find path to langaugae icon, invalid language code '{$language_code}'";
            throw new PhetLocaleException($msg);
        }

        $language_name = $this->getLanguageName($language_code);
        $language_name = preg_replace('/ /', '-', $language_name);
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
    private function getCountryIconUrl($country_code) {
        if (!$this->isValidCountryCode($country_code)) {
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
    private function getLanguageImgHtml($language_code) {
        $language_name = $this->getLanguageName($language_code);
        $language_icon = $this->getLanguageIconUrl($language_code);
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
    private function getCountryImgHtml($country_code) {
        $country_name = $this->getCountryName($country_code);
        $country_icon = $this->getCountryIconUrl($country_code);
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
    public function getFullInfo($locale) {
        if (!$this->isValid($locale)) {
            return false;
        }

        $info = array();

        $info['locale'] = $locale;
        $info['locale_name'] = $this->getLocaleName($locale);

        $info['language_code'] = $this->extractLanguageCode($locale);
        $info['language_name'] = $this->getLanguageName($info['language_code']);
        $info['language_icon'] = $this->getLanguageIconUrl($info['language_code']);
        $info['language_img'] = $this->getLanguageImgHtml($info['language_code']);

        if ($this->hasValidCountryCode($locale)) {
            $info['country_code'] = $this->extractCountryCode($locale);
            $info['country_name'] = $this->getCountryName($info['country_code']);
            $info['country_icon'] = $this->getCountryIconUrl($info['country_code']);
            $info['country_img'] = $this->getCountryImgHtml($info['country_code']);
        }
        else {
            $info['country_code'] = null;
            $info['country_name'] = null;
            $info['country_icon'] = null;
            $info['country_img'] = null;
        }

        return $info;
    }
}

?>
