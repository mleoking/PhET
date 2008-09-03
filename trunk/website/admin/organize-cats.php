<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");
include_once(SITE_ROOT."admin/hierarchical-categories.php");

class OrganizeCategoriesPage extends SitePage {

    function update() {
        // Get the database connection, start it if if this is the first call
        global $connection;
        if (!isset($connection)) {
            connect_to_db();
        }

        $result = parent::update();
        if (!$result) {
            return $result;
        }

        // Compute all category orders:
        $category_rows = mysql_query(SQL_SELECT_ALL_VISIBLE_CATEGORIES, $connection);
        $this->cat_orders    = array();

        while ($category = mysql_fetch_assoc($category_rows)) {
            $this->cat_orders[] = $category['cat_order'];
        }

        // This sorts by values (category order)
        asort($this->cat_orders);

        // Reindex and remove duplicates:
        $this->cat_orders = array_unique(array_values($this->cat_orders));

        $action = isset($_REQUEST["action"]) ? $_REQUEST["action"] : null;
        $cat_order = isset($_REQUEST["cat_order"]) ? $_REQUEST["cat_order"] : null;
        $cat_id = isset($_REQUEST["cat_id"]) ? $_REQUEST["cat_id"] : null;
        $cat_name = isset($_REQUEST["cat_name"]) ? $_REQUEST["cat_name"] : null;

        $this->master_hier = new HierarchicalCategories();

        if (isset($_REQUEST["action"])) {
            if ($action == "delete") {
                $this->do_delete($cat_id);
            }
            else if ($action == "add") {
                $this->do_add($cat_name);
            }
            else if ($action == "move_up") {
                $this->master_hier->move_up($cat_id);
            }
            else if ($action == "move_down") {
                $this->master_hier->move_down($cat_id);
            }
            else if ($action == "rename") {
                $this->do_rename($cat_id, $cat_name);
            }
            else if ($action == "new_parent") {
                $this->master_hier->do_new_parent($cat_id, $_REQUEST['froggie']);
            }
            else {
                // undefined action, ignore
                return;
            }

            cache_clear_simulations();
        }
    }

    function get_previous_order_number($cat_order) {
        $previous_order = null;

        foreach ($this->cat_orders as $order) {
            if ($order < $cat_order) {
                $previous_order = $order;
            }
            else {
                break;
            }
        }

        if ($previous_order == null) return $cat_order - 1;

        return $previous_order;
    }

    function get_next_order_number($cat_order) {
        $next_order = null;

        foreach ($this->cat_orders as $order) {
            if ($order > $cat_order) {
                $next_order = $order;

                break;
            }
        }

        if ($next_order == null) return $cat_order + 1;

        return $next_order;
    }

    function do_delete($cat_id) {
        db_exec_query("DELETE FROM `category` WHERE `cat_id`='$cat_id' ");
    }

    function do_add($cat_name) {
        // Get the database connection, start it if if this is the first call
        global $connection;
        if (!isset($connection)) {
            connect_to_db();
        }

        $cat_order = $this->get_next_order_number(end($this->cat_orders));

        $safe_cat_name = mysql_real_escape_string($cat_name, $connection);

        db_exec_query("INSERT INTO `category` (`cat_name`, `cat_order`) VALUES ('$safe_cat_name', '$cat_order') ");
    }

    function do_rename($cat_id, $cat_name) {
        // Get the database connection, start it if if this is the first call
        global $connection;
        if (!isset($connection)) {
            connect_to_db();
        }

        $safe_cat_name = mysql_real_escape_string($cat_name, $connection);
        $safe_cat_id = mysql_real_escape_string($cat_id, $connection);
        db_exec_query("UPDATE `category` SET `cat_name`='$safe_cat_name' WHERE `cat_id`='$safe_cat_id' ");
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        // Get the database connection, start it if if this is the first call
        global $connection;
        if (!isset($connection)) {
            connect_to_db();
        }

        print <<<EOT
            <p>
                You may use this form to create, edit, and change the order of the categories under which simulations appear.
            </p>

EOT;

        $this->master_hier->walk('print_hier_cat_form', $this->master_hier);
        print <<<EOT
        <form action="organize-cats.php" method="post">
            <div class="simcategory">
                <p>
                    <input type="text" name="cat_name" maxlength="40" />
                    <input type="submit" name="action" value="add" />
                </p>
            </div>
        </form>

EOT;
    }

}

// This function needs to be outside the class so that it can be called
// from HierarchicalCategories::walk(), which is a different function in
// a different class in a different file.  Static class functions don't work.
function print_hier_cat_form($user_var, $data, $depth) {
    $cat_id    = $data['cat_id'];
    $cat_name  = format_string_for_html($data['cat_name']);
    $cat_order = $data['cat_order'];

    $ind = "";
    for ($i = 1; $i < $depth; ++$i) {
        $ind .= "=&gt;";
    }

    $possible_parents = array();
    $user_var->get_possible_parent_names($possible_parents, $cat_id);
    //$this->get_possible_parent_names($possible_parents, $cat_id);
    //$this->get_possible_parent_names($possible_parents, $base_hier_cats, $cat_id);
    // onchange="alert('hello ::' + this.parentNode.childNodes[3].value + '::' + this.parentNode.childNodes[3].name + '::' + this.parentNode.childNodes['action'] + '::');
    $html_parent_list = <<<EOT
            <select name="froggie" style="width: 120px;" onchange="this.parentNode.childNodes[3].value='new_parent';submit();">
                <option value="-2">Select new parent</option>
                <option value="-1">* Move to base level *</option>

EOT;

    foreach ($possible_parents as $parent) {
        
        $html_parent_list .= "<option value=\"{$parent[0]}\">".format_string_for_html($parent[1])."</option>\n";
    }
    $html_parent_list .= "</select>\n";

    print <<<EOT
                <form action="organize-cats.php" method="post">
                    <div class="simcategory">
                        <p>{$ind}
                            <input type="hidden" name="cat_id" value="{$cat_id}" />
                            <input type="hidden" name="action" value="rename"  />

                            <input type="text" name="cat_name" value="{$cat_name}" size="30" />
                            <a href="organize-cats.php?action=move_up&amp;cat_id={$cat_id}&amp;cat_order={$cat_order}">u</a>
                            <a href="organize-cats.php?action=move_down&amp;cat_id={$cat_id}&amp;cat_order={$cat_order}">d</a>

                            <a href="organize-cats.php?action=delete&amp;cat_id={$cat_id}">D</a>
                            {$html_parent_list}
                            <input type="submit" value="rename" />
                        </p>
                    </div>
                </form>

EOT;
}

$page = new OrganizeCategoriesPage("Organize Categories", NAV_ADMIN, null, AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>