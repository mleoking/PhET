<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

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

        $action = isset($_REQUEST["action"]) ? $_REQUEST["action"] : null;
        $cat_id = isset($_REQUEST["cat_id"]) ? $_REQUEST["cat_id"] : null;
        $cat_name = isset($_REQUEST["cat_name"]) ? $_REQUEST["cat_name"] : null;

        $this->hierarchical_categories = new HierarchicalCategories();

        if (isset($_REQUEST["action"])) {
            if ($action == "delete") {
                $this->hierarchical_categories->deleteNode($cat_id);
            }
            else if ($action == "add") {
                $this->hierarchical_categories->addNode($cat_name);
            }
            else if ($action == "move_up") {
                $this->hierarchical_categories->moveUp($cat_id);
            }
            else if ($action == "move_down") {
                $this->hierarchical_categories->moveDown($cat_id);
            }
            else if ($action == "rename") {
                $this->do_rename($cat_id, $cat_name);
            }
            else if ($action == "new_parent") {
                if (($_REQUEST['select_new_parent'] == 0) || ($this->hierarchical_categories->getNode($_REQUEST['select_new_parent']))) {
                    $this->hierarchical_categories->newParent($cat_id, $_REQUEST['select_new_parent']);
                }
            }
            else {
                // undefined action, ignore
                return;
            }

            cache_clear_simulations();
        }
    }

    function do_rename($cat_id, $cat_name) {
        // This should be pushed in to the heirarcharical category class

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

        $hier_cats = $this->hierarchical_categories->getDescendants();
        foreach ($hier_cats as $cat) {
            if (!$cat['cat_is_visible']) {
                continue;
            }

            $cat_id    = $cat['cat_id'];
            $cat_name  = format_string_for_html($cat['cat_name']);

            $ind = "";
            $depth = $cat['cat_nlevel'];
            for ($i = 1; $i < $depth; ++$i) {
                $ind .= "=&gt;";
            }

            $possible_parents = $hier_cats;
            $html_parent_list = <<<EOT
            <select name="select_new_parent" style="width: 120px;" onchange="this.parentNode.getElementsByTagName('input')[1].value='new_parent';submit();">
                <option value="-2">Select new parent</option>
                <option value="0">* Move to base level *</option>

EOT;

            foreach ($hier_cats as $parent) {
                if ($parent['cat_id'] == $cat_id) continue;
                if (!$parent['cat_is_visible']) continue;
                $html_parent_list .= "<option value=\"{$parent['cat_id']}\">".format_string_for_html($parent['cat_name'])."</option>\n";
            }
            $html_parent_list .= "</select>\n";

            // Don't delete parents
            if ($this->hierarchical_categories->numChildren($cat['cat_id']) > 0) {
                $del = "<a href=\"#\" onclick=\"alert('Cannot delete a parent, please remove all children first');\" style=\"color: black;background-color: #dddddd;\">Del</a>";
            }
            else {
                $del = "<a href=\"organize-cats.php?action=delete&amp;cat_id={$cat_id}\">Del</a>";
            }

            print <<<EOT
                <form action="organize-cats.php" method="post">
                    <div class="simcategory">
                        <p>{$ind}
                            <input type="hidden" name="cat_id" value="{$cat_id}" />
                            <input type="hidden" name="action" value="rename"  />

                            <input type="text" name="cat_name" value="{$cat_name}" size="20" />
                            <a href="organize-cats.php?action=move_up&amp;cat_id={$cat_id}">Up</a>
                            <a href="organize-cats.php?action=move_down&amp;cat_id={$cat_id}">Down</a>

                            {$del}
                            {$html_parent_list}
                            <input type="submit" value="rename" />
                        </p>
                    </div>
                </form>

EOT;
        }

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

$page = new OrganizeCategoriesPage("Organize Categories", NavBar::NAV_ADMIN, null, SitePage::AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>