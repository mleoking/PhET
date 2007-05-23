<?php  

  ////////////////////////////////////////////////////////////////////////////  
  // File   : Spell Check Class
  // Author : Jeff Welch (jeff@jwelch.org)
  // Date   : May 29, 2005
  //
  // This class provides an API for spell checking a given string using pure
  // php without the aid of any compiled php extensions.  The spell checking
  // occurs via the findNext() method which finds the next misspelled word
  // from the string or returns false in the event that no further words have
  // been misspelled.  Suggestions can be implemented via the suggest()
  // method which returns the closest node to the misspelled word that has
  // not yet been returned.  Updating misspelled words can be implemented via
  // the change() and changeAll() methods.  Ignoring functionality can be
  // implemented via the ignore() method.
  //
  // The most basic application of this class would be:
  //
  // $class = SpellCheck($checkme);
  // if($class->findNext())
  //   echo 'The first misspelled word is ' . $class->misspelling . '.';
  // else
  //   echo 'This string has no mispelled words.';
  //
  // All further misspelled words can be found via
  //
  // while($class->findNext())
  //   echo "\nThe next misspelled word is ' . $class->misspelling . '.';
  // echo "\nSpell Check Complete.";
  ////////////////////////////////////////////////////////////////////////////
  
  class SpellCheck
  {
    var $words;            // an arrary of the words in the current traversal
    var $ignore = array(); // an array of all the words to ignore
    var $ignoreString;     // a string containing all the words to ignore
    var $string;           // the string to be checked
    var $adjustedString;   // the string minus what was already checked
    var $displayString;    // the string to be displayed
    var $suggestCount;     // the number of suggestions made
    var $strlen;           // the strlen of the current word
    var $prepStrlen;       // the strlen of the current word mius garbage
    var $strpos;           // the current position in the current string
    var $misspelling;      // the misspelled word
    
    // the array of suggestions
    var $suggestions = array();
    
    // the default constructor, takes an optional strpos, strlen and preplen
    // for tracking your current position in a certain string
    function SpellCheck($string, $strpos = 0, $strlen = 0, $preplen = 0)
    { 
      // prepare the adjustedString depending on the position of 
      // string the current
      $this->adjustedString = ($strpos == 0) ? $string :
                               substr($string, $strpos);
      
      // Initialize all the vars
      $this->strlen = $strlen;
      $this->string = $string;
      $this->strpos = $strpos;
      $this->prepStrlen = $preplen;
      $this->displayString = $string;
      $this->suggestCount = 0;
    }
    
    // Replaces the currently misspelled word with the passed replacement value
    function change($replacement)
    {
      $word = substr($this->string, $this->strpos - $this->strlen, 
                     $this->prepStrlen);
      $replacement = stripslashes($this->adjustFirst($word, $replacement));
      $this->string = substr_replace($this->string, $replacement,
                      $this->strpos - $this->strlen, $this->prepStrlen);
      $this->strpos += strlen($replacement) - $this->strlen;
      $this->adjustedString = substr($this->string, $this->strpos);
    }
    
    // Replaces all words matching the currently mispelled word with the passed
    // replacement values
    function changeAll($replacement)
    {
      $word = substr($this->string, $this->strpos - $this->strlen,
                     $this->prepStrlen);
      $replacement = stripslashes($this->adjustFirst($word, $replacement));
      $adjustedString = substr($this->string, $this->strpos - $this->strlen);
      $this->string = substr($this->string, 0, $this->strpos - $this->strlen);
      preg_match_all("/([\w'\-]*)([^\w'\-]*)/",$adjustedString, $words); 
      $cnt = count($words[1]);
      for($i = 0; $i < $cnt; $i++)
      {
        if($words[1][$i] == $word)
           $words[1][$i] = $replacement;
        $this->string .= $words[1][$i] . $words[2][$i];
      }
      $this->strpos += strlen($replacement) - $this->strlen;
      $this->adjustedString = substr($this->string, $this->strpos);
    }
    
    // If the first letter of the string to be changed is capitalized,
    // then capitalize the first letter of its replacement
    function adjustFirst($string1, $string2)
    {
      if(ord($string1) >= 65 && ord($string1) <= 90)
        $string2{0} = strtoupper($string2{0});
      return $string2; 
    }
    
    // Ignores all the values passed in the list delimitted by spaces
    function ignore($list)
    {
      $this->ignoreString = stripslashes($list);
      $this->ignore = explode(' ', trim($this->ignoreString));
    }
    
    // Adds an item to the ignore list
    function addIgnoreItem($item)
    {
      $item = trim(stripslashes($item));
      $this->ignore[] = $item;
      $this->ignoreString .= $item . ' ';
    }
    
    // Finds if a passed word exists in the dictionary bst
    function find($word, $node)
    {
      $word = strtolower($word);
      if($node['d'] == $word) return true;
      if(!$node['d']) return false;
      $this->suggestions[] = $node['d'];
      return ($word < $node['d']) ? 
        $this->find($word, $node['l']) :
        $this->find($word, $node['r']);
    }
  
    // Chops off garbage from the end of a word
    function prepare($word)
    {
      return preg_replace("/([\w'\-]*)[^\w'\-]/",'$1',$word);
    }
    
    // Finds the next mispelled word that is not in the ignore list or returns
    // false
    function findNext()
    {
      // Makes the dictionary bst global
      global $tree;
      
      // Splits up all the words and put them in an array
      preg_match_all("/([\w'\-]*[^\w'\-]*)/",$this->adjustedString, $words);
      $this->words = $words[1];
      
      // Get rid of the garbage
      array_pop($this->words);
      
      // Itterates through all the words in the string searching for mispelled
      // words
      foreach($this->words as $word)
      {
        $this->strlen = strlen($word);
        $prepared_word = $this->prepare($word);
        if(!$this->find($prepared_word, $tree) && 
           !in_array($prepared_word, $this->ignore) &&
           !preg_match("/^[0-9'-]+$/", $prepared_word) &&
           !preg_match("/^[0-9]{2}[aApP][mM]$/", $prepared_word))
        {
          $this->prepStrlen = strlen($prepared_word);
          $this->displayString = substr_replace($this->string, 
                  "<span id=\"error\">$prepared_word</span>", $this->strpos,
                  $this->prepStrlen);
                  $this->strpos += $this->strlen;
                  $this->misspelling = $prepared_word;
          return true;
        }
        $this->strpos += $this->strlen;
      }
      return false;
    }
    
    // Returns the next suggestion in the suggestion list
    function suggest()
    {
      return ($this->suggestions && ++$this->suggestCount != 7) ?
              array_pop($this->suggestions) : false;
    } 
  }
?>