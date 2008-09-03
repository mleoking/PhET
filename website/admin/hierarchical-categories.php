<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");

class HierarchicalCategories {
    const MAX_DEPTH = 60;

    private $hier_cats;

    function __construct() {
        $this->init_hier_cats();
    }

    private function hier_add($new_id, $new_data, &$final, $depth = 0) {
        if ($depth > HierarchicalCategories::MAX_DEPTH) {
            throw Exception("hier_add: too much recusion, a loop?");
        }

        foreach ($final as $id => &$data) {
            if ($id == 'data') continue;

            if ($new_data['cat_parent'] == $id) {
                $data[$new_id] = array(); //$new_data;
                $data[$new_id]['data'] = $new_data;
                return true;
            }
            else if (!empty($data)) {
                $result = $this->hier_add($new_id, $new_data, $data, $depth + 1);
                if ($result) {
                    return $result;
                }
            }
        }

        return false;
    }

    private function init_hier_cats() {
        // Get the database connection, start it if if this is the first call
        global $connection;
        if (!isset($connection)) {
            connect_to_db();
        }

        // Compute all category orders:
        $category_rows = mysql_query(SQL_SELECT_ALL_VISIBLE_CATEGORIES, $connection);
        $work = array();

        // First make a dict
        $cats = array();
        while ($category = mysql_fetch_assoc($category_rows)) {
            $cats[$category['cat_id']] = $category;
        }

        $this->hier_cats = array();
        $this->hier_cats[-1] = array();

        $p = -1;
        while (!empty($cats)) {
            $added = array();
            foreach ($cats as $id => $data) {
                $result = $this->hier_add($id, $data, $this->hier_cats);
                if ($result) {
                    $added[] = $id;
                }
            }

            foreach ($added as $id) {
                unset($cats[$id]);
            }
            $p = $p + 1;
        }
    }

    function generate_sql_orders(&$sql, $hier_cats = null, $depth = 0) {
        if ($depth > HierarchicalCategories::MAX_DEPTH) {
            throw Exception("generate_sql_orders: too much recusion, a loop?");
        }

        if (is_null($hier_cats)) {
            $hier_cats = $this->hier_cats;
        }

        $order = 0;
        foreach ($hier_cats as $id => $stuff) {
            if ($id == 'data') continue;

            $sql[] = "UPDATE `category` SET cat_order={$order} WHERE cat_id={$id}";
            $order = $order + 1;
            $this->generate_sql_orders($sql, $stuff, $depth + 1);
        }
    }

    function commit_orders() {
        $sql = array();
        $this->generate_sql_orders($sql);
        foreach ($sql as $s) {
            db_exec_query($s);
        }
    }

    function get_hier_cats() {
        return $this->hier_cats;
    }

    function get_possible_parent_names(&$names, $child_id, $hier_cats = NULL, $depth = 0) {
        if ($depth > HierarchicalCategories::MAX_DEPTH) {
            throw Exception("get_possible_parent_names: too much recusion, a loop?");
        }

        if (is_null($hier_cats)) {
            $hier_cats = $this->hier_cats;
        }

        foreach ($hier_cats as $id => $stuff) {
            if ($id == 'data') continue;

            if ($id == $child_id) continue;

            if (isset($stuff['data'])) {
                $names[] = array($id, $stuff['data']['cat_name']);
            }

            $this->get_possible_parent_names($names, $child_id, $stuff, $depth + 1);
        }
    }

    // $user_fn will be called for each data element
    //    form: $user_fn($user_var, $data, $depth)
    // $user_var is specified by the calling function, and passed unaltered
    function walk($user_fn, $user_var, $hier_cats = null, $depth = 0) {
        if ($depth > HierarchicalCategories::MAX_DEPTH) {
            throw Exception("walk:  too much recusion, a loop?");
        }

        if (is_null($hier_cats)) {
            $hier_cats = $this->hier_cats;
        }

        if (empty($hier_cats)) return;

        foreach ($hier_cats as $id => $stuff) {
            if ($id == 'data') continue;
            if (!empty($stuff)) {
                if (isset($stuff['data'])) {
                    $data = $stuff['data'];
                    $user_fn($user_var, $data, $depth);
                }
            }
            $this->walk($user_fn, $user_var, $stuff, $depth + 1);
        }
    }

    function do_new_parent($cat_id, $new_parent_id) {
        $a = array('cat_parent' => $new_parent_id);
        db_update_table('category', $a, 'cat_id', $cat_id);
        $this->init_hier_cats();
        $this->commit_orders();
    }

    function find_stuff_containing_id0(&$answer, $cat_id, &$hier_cats, $depth = 0) {
        if ($depth > 5) {
            throw Exception("find_id: too much recursion, loop?");
        }

        if (isset($hier_cats[$cat_id])) {
            $answer['thing'] =& $hier_cats;
            return true;
        }

        foreach ($hier_cats as $id => &$stuff) {
            if ($id == 'data') continue;

            $result = $this->find_stuff_containing_id($answer, $cat_id, $stuff, $depth + 1);
            if (!is_null($result)) {
                return true;
            }
        }

        return NULL;
    }

    function &find_stuff_containing_id($cat_id, &$hier_cats, $depth = 0) {
        if ($depth > HierarchicalCategories::MAX_DEPTH) {
            throw Exception("find_id: too much recursion, loop?");
        }

        if (isset($hier_cats[$cat_id])) {
            //$answer['thing'] =& $hier_cats;
            return $hier_cats;
        }

        foreach ($hier_cats as $id => &$stuff) {
            if ($id == 'data') continue;

            $result = &$this->find_stuff_containing_id($cat_id, $stuff, $depth + 1);
            if (!is_null($result)) {
                return $result;
            }
        }

        return NULL;
    }

    function move_up($cat_id) {
        $container = &$this->find_stuff_containing_id($cat_id, $this->hier_cats);
        $sorted_array = $this->go_one_up($container, $cat_id, $this->hier_cats);
        $container = $sorted_array;
        $this->commit_orders();
    }

    function move_down($cat_id) {
        $container = &$this->find_stuff_containing_id($cat_id, $this->hier_cats);
        $sorted_array = $this->go_one_down($container, $cat_id, $this->hier_cats);
        $container = $sorted_array;
        $this->commit_orders();
    }

    function go_one_up($a, $to_find) {
        $i = 0;
        $find_idx = -1;
        reset($a);
        while (current($a)) {
            if (key($a) == $to_find) {
                $find_idx = $i;
            }
            next($a);
            $i = $i + 1;
        }

        if ($find_idx == -1) {
            //print "Nothing to to, index not found!\n";
        }
        else if ($find_idx == 0) {
            //print "Nothing to do, already at top!\n";
        }
        else if (count($a) <= 1) {
            //print "nothing to do, array too small!\n";
        }
        else {
            $prev_idx = $find_idx - 1;
            $b1 = array_slice($a, 0, $prev_idx, true);
            $b2 = array_slice($a, $prev_idx, 2, true);
            $b2a = array_reverse($b2, true);
            $b3 = array_slice($a, $find_idx + 1, null, true);
            $fin = $b1 + $b2a + $b3;
            return $fin;
        }
        return $a;
    }

    function go_one_down($a, $to_find) {
        $i = 0;
        $find_idx = -1;
        reset($a);
        while (current($a)) {
            if (key($a) == $to_find) {
                $find_idx = $i;
            }
            next($a);
            $i = $i + 1;
        }

        if ($find_idx == -1) {
            //print "Nothing to do, key not found!\n";
        }
        else if ($find_idx == (count($a) - 1)) {
            //print "Nothing to do, already at bottom!\n";
        }
        else if (count($a) <= 1) {
            //print "nothing to do, array too small!\n";
        }
        else {
            $next_idx = $find_idx + 1;
            $b1 = array_slice($a, 0, $find_idx, true);
            $b2 = array_slice($a, $find_idx, 2, true);
            $b2a = array_reverse($b2, true);
            $b3 = array_slice($a, $next_idx + 1, null, true);
            $fin = $b1 + $b2a + $b3;
            return $fin;
        }
        return $a;
    }
}

?>