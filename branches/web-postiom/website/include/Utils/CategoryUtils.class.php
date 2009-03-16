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

    public function getAllVisibleCategories() {
        //define("SQL_SELECT_ALL_VISIBLE_CATEGORIES",
        //   "SELECT * FROM `category` WHERE `cat_is_visible`='1' ORDER BY `cat_parent`,`cat_order` ASC ");
        $sql = "SELECT * FROM `category` ".
            "WHERE `cat_is_visible`='1' ".
            "ORDER BY `cat_parent`,`cat_order` ASC ";
        return db_get_rows_custom_query($sql);
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

    public function getSimListingOrder($cat_id) {
        $sql = "SELECT * FROM `simulation_listing` ".
            "WHERE `simulation_listing`.`cat_id`='{$cat_id}' ".
            "ORDER BY `simulation_listing`.`simulation_listing_order` ASC";

        return db_get_rows_custom_query($sql);
    }

    /**
     * Hopefully no longer needed, but I'm not yet sure.  Holdover
     * from a previous era where the sim listing orders were not
     * updated properly when other sims were added or deleted.
     *
     *
     */
    public function fixSimListingOrder() {
        foreach($this->getAllCategories() as $category) {
            $cat_id = $category['cat_id'];
            $auto_order = 1;
            foreach ($this->getSimListingOrder($cat_id) as $sim_listing) {
                $simulation_listing_id = $sim_listing["simulation_listing_id"];
                $sql = "UPDATE `simulation_listing` ".
                    "SET `simulation_listing_order`='$auto_order' ".
                    "WHERE `simulation_listing_id`='$simulation_listing_id'";
                db_exec_query($sql);
                ++$auto_order;
            }
        }
    }

    public function isSimInCategory($sim_id, $cat_id) {
        foreach ($this->getSimListingOrder($cat_id) as $sim_listing) {
            if ($sim_listing['sim_id'] == $sim_id) {
                return true;
            }
        }

        return false;
    }
}

?>
