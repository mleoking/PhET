<?php

class HierarchicalCategories extends NestedTree {
    function __construct() {
        parent::__construct('category', 'cat_id', 'cat_parent_id', 'cat_name', 'cat_nleft', 'cat_nright', 'cat_nlevel');
    }

    /**
     * Rebuilds the tree data and saves it to the database
     * 
     * @param   string  $order_key     Key to sort on, or empty if want unsorted
     */
    function rebuild($order_key = 'sort') {
        // TODO: Refactor hidden categories: 
        //  "Show Animated Preview on Homepage" 
        //  "Show Static Preview on Homepage"
        // Both are essentially special cases and function differentely than the
        // rest of the categories.  AND they tend to get in the way.

        // HACK: To keep the hidden categories out of the way during a 
        // rebuild on anything other than the 'sort' criteria, give them
        // huge numbers in that will put them at the end.
        // HACK: To keep this simple, just switch on nleft as that is 
        // used in almost all of the cases.
        if ($order_key == 'nleft') {
            $result = db_get_rows_by_condition($this->table, array('cat_is_visible' => 0), false, false);
            foreach ($result as $row) {
                db_update_table($this->table, array('cat_nleft' => $row['cat_nleft'] + 9999), 'cat_id', $row['cat_id']);
            }
            parent::rebuild($order_key);
        }
    }

    // TODO: override deleteNode to also delete all references to the category in
    // table simulation_listing

    function addNode($cat_name, $parent_id = 0) {
        global $connection;
        if (!isset($connection)) {
            connect_to_db();
        }

        $safe_cat_name = mysql_real_escape_string($cat_name, $connection);

        db_exec_query("INSERT INTO `category` (`cat_name`, `cat_parent`, `cat_nleft`) VALUES ('{$safe_cat_name}', '{$parent_id}', 9999) ");
        $this->rebuild('nleft');
    }
}

?>