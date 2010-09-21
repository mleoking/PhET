<?php

  // Dano: changed 'tree.php' to 'tree.php.bigarray' to stop it from
  //   being searched during in multi-file searches.

  ////////////////////////////////////////////////////////////////////////////
  // File   : Dictionary To BST
  // Author : Jeff Welch (jeff@jwelch.org)
  // Date   : May 29, 2005
  //
  // This file allows users to update their dictionary BST.  Basically, when
  // a user adds new words to his/her dictionary, or finds a new dictionary,
  // the user can then run this script which will convert their new dictionary
  // to a BST declaration.  Since it could take some time to run this script
  // on larger dictionaries, it is recommended that you run this script in
  // a terminal.  To do this, simple chmod +x this script and then run it
  // by typing php DictionaryToBST.php in your terminal. The dictionary
  // location and tree destination are both configurable below.
  ////////////////////////////////////////////////////////////////////////////

  ##### CONFIGURATIONS BEGIN HERE #####

  // Location of the dictionary file
  $dictionary_location = 'dictionary.txt';

  // Destination for the tree file
  $tree_destination = 'tree.php.bigarray';

  ##### CONFIGURATIONS END HERE #####

  // Really trimmed down binary tree class.  Basically the point
  // is to insert as many elements as possible as quickly as
  // possible.
  class BinaryTree
  {
    // root of the tree
    var $root;

    // Used to insert a new element into the tree
    function insert($data, &$node)
    {
      if(!$node['d']) return $node['d'] = $data;
        if($data < $node['d']) return $this->insert($data, $node['l']);
      return $this->insert($data, $node['r']);
    }
  }

  // This function is used to traverse an array with any
  // number of deminsions and convert it to an array
  // declaration
  function for_each($array, $handle) {
    foreach($array as $key => $val) {
      if(is_array($val)) {
        fputs($handle, "'$key'=>array(");
        for_each($val, $handle);
        fputs($handle, ')');
        array_shift($array);
        if(count($array) > 0)
          fputs($handle, ',');
      } else {
        fputs($handle, "'$key'=>'$val'");
        array_shift($array);
        if(count($array) > 0)
          fputs($handle, ',');
      }
    }
  }
    // Opens dictionary.txt and shuffles the words.  If the words
  // in the dictionary are in order, the BST wont be balanced
  $handle = fopen($dictionary_location, 'r');
  $array = array();  while (!feof($handle)) {     $word = trim(fgets($handle, 45));
     $array[] = $word;  }  fclose($handle);
  shuffle($array);

  // Moves the shuffled words from dictionary.txt into a BST
  $tree = new BinaryTree;  foreach($array as $word) {
     $tree->insert(str_replace("'", "\'", $word), $tree->root);  }

  // Freeing up some memory
  unset($array);

  // Open tree.php.bigarray and inserts the declaration for the
  // array-based BST $tree
  $handle = fopen($tree_destination, 'w');
  fputs($handle, '<?php $tree=array(');
  fputs($handle, for_each($tree->root, $handle));
  fputs($handle, '); ?>');
  fclose($handle);
?>
