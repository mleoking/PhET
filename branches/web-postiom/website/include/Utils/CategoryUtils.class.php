<?php

require_once('include/locale-codes-language.php');
require_once('include/locale-codes-country.php');

class CategoryUtils {

    private $categories;

    // For singleton pattern
    private static $instance;

    private function __construct() {
    }

    private function __clone() {
    }

    public static function inst() {
        if (!isset(self::$instance)) {
            self::$instance = new self;
        }
        return self::$instance;
    }

    public function getAllCategories() {
        if (isset($this->categories)) {
            return $this->categories;
        }

        $rows = db_get_all_rows('category');

        $this->categories = array();
        foreach ($rows as $cat) {
            $encoded = WebUtils::inst()->encodeString($cat['cat_name']);
            $this->categories[$encoded] = $cat;
        }

        return $this->categories;
    }

    public function getCategory($encoding) {
        $categories = $this->getAllCategories();
        
        if (!isset($categories[$encoding])) {
            return null;
        }

        return $categories[$encoding];
    }

    public function getDefaultCategory() {
        // Get the first non-visible category
        $categories = $this->getAllCategories();
        foreach ($categories as $category) {
            if (!$category['cat_is_visible']) {
                continue;
            }

            return $category;
        }

        return null;
    }

    public function getCategoryBaseUrl($encoding) {
        $categories = $this->getAllCategories();
        $site_root = SITE_ROOT;
        return SITE_ROOT."simulations/index.php?cat={$encoding}";
    }
}

?>
