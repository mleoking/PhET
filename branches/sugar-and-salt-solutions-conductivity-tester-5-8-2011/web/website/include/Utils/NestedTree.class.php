<?php

// Adapted from the excellent article about implementing n-level nested trees
// in PHP and PostgreSQL:
//    http://www.phpriot.com/articles/nested-trees-1
// Modified to work in the PhET website environment


class NestedTree {
    /**
     * Constructor. Set the database table name and necessary field names
     *
     * @param   string  $table          Name of the tree database table
     * @param   string  $idField        Name of the primary key ID field
     * @param   string  $parentField    Name of the parent ID field
     * @param   string  $sortField      Name of the field to sort data.
     * @param   string  $nleft          Name of the 'nleft' field.
     * @param   string  $nright         Name of the 'nright' field.
     * @param   string  $nlevel         Name of the 'nlevel' field.
     */
    function __construct($table, $idField, $parentField, $sortField,
                         $nleft = 'nleft', $nright = 'nright', $nlevel = 'nlevel') {
        $this->table = $table;

        $this->fields = array('id'     => $idField,
                              'parent' => $parentField,
                              'sort'   => $sortField,
                              'nleft'  => $nleft,
                              'nright' => $nright,
                              'nlevel' => $nlevel);
    }

    /**
     * A utility function to return an array of the fields
     * that need to be selected in SQL select queries
     *
     * @return  array   An indexed array of fields to select
     */
    function _getFields()
    {
        return array($this->fields['id'], $this->fields['parent'], $this->fields['sort'],
                     $this->fields['nleft'], $this->fields['nright'], $this->fields['nlevel'],
                     'cat_is_visible');
    }

    /**
     * Fetch the node data for the node identified by $id
     *
     * @param   int     $id     The ID of the node to fetch
     * @return  object          An object containing the node's
     *                          data, or null if node not found
     */
    function getNode($id)
    {
        return db_get_row_by_condition($this->table, array('cat_id' => $id));
    }

    /**
     * Fetch the descendants of a node, or if no node is specified, fetch the
     * entire tree. Optionally, only return child data instead of all descendant
     * data.
     *
     * @param   int     $id             The ID of the node to fetch descendant data for.
     *                                  Specify an invalid ID (e.g. 0) to retrieve all data.
     * @param   bool    $includeSelf    Whether or not to include the passed node in the
     *                                  the results. This has no meaning if fetching entire tree.
     * @param   bool    $childrenOnly   True if only returning children data. False if
     *                                  returning all descendant data
     * @return  array                   The descendants of the passed now
     */
    function getDescendants($id = 0, $includeSelf = false, $childrenOnly = false)
    {
        $idField = $this->fields['id'];
        $nleftField = $this->fields['nleft'];
        $nrightField = $this->fields['nright'];

        $node = $this->getNode($id);
        if ($node) {
            $nleft = $node[$nleftField];
            $nright = $node[$nrightField];
            $parent_id = $node[$idField];
        }
        else {
            $nleft = 0;
            $nright = 0;
            $parent_id = 0;
        }

        if ($childrenOnly) {
            if ($includeSelf) {
                $query = sprintf('select %s from %s where %s = %d or %s = %d order by %s',
                                 join(',', $this->_getFields()),
                                 $this->table,
                                 $this->fields['id'],
                                 $parent_id,
                                 $this->fields['parent'],
                                 $parent_id,
                                 $this->fields['nleft']);
            }
            else {
                $query = sprintf('select %s from %s where %s = %d order by %s',
                                 join(',', $this->_getFields()),
                                 $this->table,
                                 $this->fields['parent'],
                                 $parent_id,
                                 $this->fields['nleft']);
            }
        }
        else {
            if ($nleft > 0 && $includeSelf) {
                $query = sprintf('select %s from %s where %s >= %d and %s <= %d order by %s',
                                 join(',', $this->_getFields()),
                                 $this->table,
                                 $this->fields['nleft'],
                                 $nleft,
                                 $this->fields['nright'],
                                 $nright,
                                 $this->fields['nleft']);
            }
            else if ($nleft > 0) {
                $query = sprintf('select %s from %s where %s > %d and %s < %d order by %s',
                                 join(',', $this->_getFields()),
                                 $this->table,
                                 $this->fields['nleft'],
                                 $nleft,
                                 $this->fields['nleft'],
                                 $nright,
                                 $this->fields['nleft']);
            }
            else {
                $query = sprintf('select %s from %s order by %s',
                                 join(',', $this->_getFields()),
                                 $this->table,
                                 $this->fields['nleft']);
            }
        }

        $result = db_exec_query($query);

        $arr = array();
        while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
            $arr[$row[$idField]] = $row;
        }

        return $arr;
    }

    /**
     * Fetch the children of a node, or if no node is specified, fetch the
     * top level items.
     *
     * @param   int     $id             The ID of the node to fetch child data for.
     * @param   bool    $includeSelf    Whether or not to include the passed node in the
     *                                  the results.
     * @return  array                   The children of the passed node
     */
    function getChildren($id = 0, $includeSelf = false) {
        return $this->getDescendants($id, $includeSelf, true);
    }

    /**
     * Fetch the path to a node. If an invalid node is passed, an empty array is returned.
     * If a top level node is passed, an array containing on that node is included (if
     * 'includeSelf' is set to true, otherwise an empty array)
     *
     * @param   int     $id             The ID of the node to fetch child data for.
     * @param   bool    $includeSelf    Whether or not to include the passed node in the
     *                                  the results.
     * @return  array                   An array of each node to passed node
     */
    function getPath($id = 0, $includeSelf = false) {
        $node = $this->getNode($id);
        if (is_null($node))
            return array();

        $idField = $this->fields['id'];
        $nleftField = $this->fields['nleft'];
        $nrightField = $this->fields['nright'];

        if ($includeSelf) {
            $query = sprintf('select %s from %s where %s <= %d and %s >= %d order by %s',
                             join(',', $this->_getFields()),
                             $this->table,
                             $this->fields['nleft'],
                             $node[$nleftField],
                             $this->fields['nright'],
                             $node[$nrightField],
                             $this->fields['nlevel']);
        }
        else {
            $query = sprintf('select %s from %s where %s < %d and %s > %d order by %s',
                             join(',', $this->_getFields()),
                             $this->table,
                             $this->fields['nleft'],
                             $node[$nleftField],
                             $this->fields['nright'],
                             $node[$nrightField],
                             $this->fields['nlevel']);
        }

        $result = db_exec_query($query);

        $idField = $this->fields['id'];
        $arr = array();
        while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
            $arr[$row[$idField]] = $row;
        }

        return $arr;
    }

    /**
     * Check if one node descends from another node. If either node is not
     * found, then false is returned.
     *
     * @param   int     $descendant_id  The node that potentially descends
     * @param   int     $ancestor_id    The node that is potentially descended from
     * @return  bool                    True if $descendant_id descends from $ancestor_id, false otherwise
     */
    function isDescendantOf($descendant_id, $ancestor_id) {
        $node = $this->getNode($ancestor_id);
        if (is_null($node)) {
            return false;
        }

        $idField = $this->fields['id'];
        $nleftField = $this->fields['nleft'];
        $nrightField = $this->fields['nright'];

        $query = sprintf('select count(*) as is_descendant
                              from %s
                              where %s = %d
                              and %s > %d
                              and %s < %d',
                         $this->table,
                         $this->fields['id'],
                         $descendant_id,
                         $this->fields['nleft'],
                         $node[$nleftField],
                         $this->fields['nright'],
                         $node[$nrightField]);

        $result = db_exec_query($query);

        if ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
            return $row['is_descendant'] > 0;
        }

        return false;
    }

    /**
     * Check if one node is a child of another node. If either node is not
     * found, then false is returned.
     *
     * @param   int     $child_id       The node that is possibly a child
     * @param   int     $parent_id      The node that is possibly a parent
     * @return  bool                    True if $child_id is a child of $parent_id, false otherwise
     */
    function isChildOf($child_id, $parent_id) {
        $query = sprintf('select count(*) as is_child from %s where %s = %d and %s = %d',
                         $this->table,
                         $this->fields['id'],
                         $child_id,
                         $this->fields['parent'],
                         $parent_id);

        $result = db_exec_query($query);

        if ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
            return $row['is_child'] > 0;
        }

        return false;
    }

    /**
     * Find the number of descendants a node has
     *
     * @param   int     $id     The ID of the node to search for. Pass 0 to count all nodes in the tree.
     * @return  int             The number of descendants the node has, or -1 if the node isn't found.
     */
    function numDescendants($id) {
        if ($id == 0) {
            $query = sprintf('select count(*) as num_descendants from %s', $this->table);
            $result = db_exec_query($query);
            if ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
                return (int) $row['num_descendants'];
            }
        }
        else {
            $node = $this->getNode($id);
            $nleftField = $this->fields['nleft'];
            $nrightField = $this->fields['nright'];
            if (!is_null($node)) {
                return ($node[$nrightField] - $node[$nleftField] - 1) / 2;
            }
        }
        return -1;
    }

    /**
     * Find the number of children a node has
     *
     * @param   int     $id     The ID of the node to search for. Pass 0 to count the first level items
     * @return  int             The number of descendants the node has, or -1 if the node isn't found.
     */
    function numChildren($id) {
        $query = sprintf('select count(*) as num_children from %s where %s = %d',
                         $this->table,
                         $this->fields['parent'],
                         $id);
        $result = db_exec_query($query);
        if ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
            return (int) $row['num_children'];
        }
        return -1;
    }

    /**
     * Fetch the tree data, nesting within each node references to the node's children
     *
     * @param   string  $order_key     Key to sort on, or empty if want unsorted
     * @return  array       The tree with the node's child data
     */
    function getTreeWithChildren($order_key = 'sort')
    {
        if ((empty($order_key)) || (!array_key_exists($order_key, $this->fields))) {
            return;
        }

        if (empty($order_key)) {
            $query = sprintf('select %s from %s',
                             join(',', $this->_getFields()),
                             $this->table,
                             $this->fields['sort']);
        }
        else {
            $query = sprintf('select %s from %s order by %s',
                             join(',', $this->_getFields()),
                             $this->table,
                             $this->fields[$order_key]);
        }

        $result = db_exec_query($query);

        $idField = $this->fields['id'];
        $parentField = $this->fields['parent'];

        // create a root node to hold child data about first level items
        $root = array($idField => 0, 'children' => array());
        $arr = array($root);

        // populate the array and create an empty children array
        while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
            $arr[$row[$idField]] = $row;
            $arr[$row[$idField]]['children'] = array();
        }

        // now process the array and build the child data
        foreach ($arr as $id => $row) {
            if (isset($row[$parentField]))
                $arr[$row[$parentField]]['children'][$id] = $id;
        }

        return $arr;
    }

    /**
     * Rebuilds the tree data and saves it to the database
     * 
     * @param   string  $order_key     Key to sort on, or empty if want unsorted
     */
    function rebuild($order_key = 'sort') {
        $data = $this->getTreeWithChildren($order_key);
        if (!$data) return;

        $n = 0; // need a variable to hold the running n tally
        $level = 0; // need a variable to hold the running level tally

        // invoke the recursive function. Start it processing
        // on the fake "root node" generated in getTreeWithChildren().
        // because this node doesn't really exist in the database, we
        // give it an initial nleft value of 0 and an nlevel of 0.
        $this->_generateTreeData($data, 0, 0, $n);

        // at this point the the root node will have nleft of 0, nlevel of 0
        // and nright of (tree size * 2 + 1)

        $nleftField = $this->fields['nleft'];
        $nrightField = $this->fields['nright'];
        $nlevelField = $this->fields['nlevel'];

        foreach ($data as $id => $row) {

            // skip the root node
            if ($id == 0)
                continue;

            $query = sprintf('update %s set %s = %d, %s = %d, %s = %d where %s = %d',
                             $this->table,
                             $this->fields['nlevel'],
                             $row[$nlevelField],
                             $this->fields['nleft'],
                             $row[$nleftField],
                             $this->fields['nright'],
                             $row[$nrightField],
                             $this->fields['id'],
                             $id);
            db_exec_query($query);
        }
    }

    /**
     * Generate the tree data. A single call to this generates the n-values for
     * 1 node in the tree. This function assigns the passed in n value as the
     * node's nleft value. It then processes all the node's children (which
     * in turn recursively processes that node's children and so on), and when
     * it is finally done, it takes the update n-value and assigns it as its
     * nright value. Because it is passed as a reference, the subsequent changes
     * in subrequests are held over to when control is returned so the nright
     * can be assigned.
     *
     * @param   array   &$arr   A reference to the data array, since we need to
     *                          be able to update the data in it
     * @param   int     $id     The ID of the current node to process
     * @param   int     $level  The nlevel to assign to the current node
     * @param   int     &$n     A reference to the running tally for the n-value
     */
    function _generateTreeData(&$arr, $id, $level, &$n) {
        $nleftField = $this->fields['nleft'];
        $nrightField = $this->fields['nright'];
        $nlevelField = $this->fields['nlevel'];

        $arr[$id][$nlevelField] = $level;
        $arr[$id][$nleftField] = $n++;

        // loop over the node's children and process their data
        // before assigning the nright value
        foreach ($arr[$id]['children'] as $child_id) {
            $this->_generateTreeData($arr, $child_id, $level + 1, $n);
        }
        $arr[$id][$nrightField] = $n++;
    }

    /*
     * Change the node's parent to have a new parent.
     * 
     * @param   int     $id             The ID of the node to change the parent of.
     * @param   int     $id             The ID of the new parent.
     *                                  Specify 0 to put the node at the root level.
     */
    function newParent($id, $new_parent_id) {
        // Safety check
        if (($id != 0) && (!$this->getNode($id))) return;

        // Give the node the new specified parent
        $new_values = array($this->fields['parent'] => $new_parent_id);
        db_update_table($this->table, $new_values, $this->fields['id'], $id);

        // Rebuild using the current order of the tree
        $this->rebuild('nleft');
    }

    /*
     * Move a node up: exchange a node with it's elder sibling (if it exists).
     * This does NOT change the levels of any of the nodes.
     * 
     * @param   int     $id             The ID of the node to move up.
     */
    function moveUp($id) {
        $idField = $this->fields['id'];
        $nleftField = $this->fields['nleft'];
        $nrightField = $this->fields['nright'];
        $parentField = $this->fields['parent'];

        // Get the node to move
        $move_node = $this->getNode($id);

        // Loop through the siblings to find an elder
        $siblings = $this->getChildren($move_node[$parentField]);
        foreach ($siblings as $sibling) {
            if (($sibling[$nrightField] == ($move_node[$nleftField] - 1))){
                // Get info about the elder sibling
                $other_row = db_get_row_by_condition($this->table, array($idField => $sibling[$idField]));

                // Swap the nleft and nright numbers of the nodes
                db_update_table($this->table,
                                 array($nleftField => $other_row[$nleftField],
                                       $nrightField => $other_row[$nrightField]),
                                 $idField,
                                 $move_node[$idField]);
                db_update_table($this->table,
                                array($nleftField => $move_node[$nleftField],
                                      $nrightField => $move_node[$nrightField]),
                                $idField,
                                $sibling[$idField]);

                // Rebuild the tree ordered by nlefts
                $this->rebuild('nleft');
                return;
            }
        }
    }

    /*
     * Move a node down: exchange a node with it's younger sibling if it exists.
     * This does NOT change the levels of any of the nodes.
     * 
     * @param   int     $id             The ID of the node to move down.
     */
    function moveDown($id) {
        $idField = $this->fields['id'];
        $nleftField = $this->fields['nleft'];
        $nrightField = $this->fields['nright'];
        $parentField = $this->fields['parent'];

        // Get the node to move
        $move_node = $this->getNode($id);

        // Loop through the siblings to find an younger sibling
        $siblings = $this->getChildren($move_node[$parentField]);
        foreach ($siblings as $sibling) {
            if (($sibling[$nleftField] == ($move_node[$nrightField] + 1))){
                // Get info about the younger sibling
                $other_row = db_get_row_by_condition($this->table, array($idField => $sibling[$idField]));

                // Swap the nleft and nright numbers of the nodes
                db_update_table($this->table,
                                 array($nleftField => $other_row[$nleftField],
                                       $nrightField => $other_row[$nrightField]),
                                 $idField,
                                 $move_node[$idField]);
                db_update_table($this->table,
                                array($nleftField => $move_node[$nleftField],
                                      $nrightField => $move_node[$nrightField]),
                                $idField,
                                $sibling[$idField]);

                // Rebuild the tree ordered by nlefts
                $this->rebuild('nleft');
                return;
            }
        }
    }

    /*
     * Delete a node, does NOT delete a node with children.
     * Move them elsewhere first.
     * 
     * @param   int     $id             The ID of the node to delete.
     */
    function deleteNode($id) {
        if ($this->numChildren($this->getNode($id)) > 0) return;

        db_delete_row($this->table, array($this->fields['id'] => $id));
        $this->rebuild('nleft');
    }
}

?>