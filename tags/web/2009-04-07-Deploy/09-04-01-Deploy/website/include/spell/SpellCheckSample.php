<?php

  // Dano: changed 'tree.php' to 'tree.php.bigarray' to stop it from
  //   being searched during in multi-file searches.

  ////////////////////////////////////////////////////////////////////////////
  // File   : Sample Spell Check
  // Author : Jeff Welch (jeff@jwelch.org)
  // Date   : May 29, 2005
  //
  // This is a real quick script I wrote as a demonstration of how to
  // implement my spell check class.  This script is by no means the only
  // or the best way to implement this class.  Fell free to use this
  // script and change anything in it at your own discretion.
  ////////////////////////////////////////////////////////////////////////////

  // This class keeps track of the execution time of the script.
  // Basically I just use it to round my numbers to three decimal
  // places.
  class dtime
  {
    function elapsed ($time_start, $time_end) {
        return substr(($time_end - $time_start), 0, 5);
    }
    function stamp () {
        $time_init = explode(chr(32),microtime());
        return $time_init[1] + $time_init[0];
    }
  }

  // Get the current time
  $st = dtime::stamp();

  // Keeps the current dictionary speed checked
  if($_POST['dictionary'] == 'fast') {
    $fastString = 'checked="checked"';
  } else {
    $slowString = 'checked="checked"';
  }

  if($_POST['spellcheck'] || $_POST['check'])
  {
    // Get the current dictionary speed and pick the
    // appropriate dictionary
    if($_POST['dictionary'] == 'fast') {
      include('tree.php.bigarray');
    } else {
      include('tree.php.bigarray');
    }

    // Where all the magic happens
    include('SpellCheck.class.php');

    // Reacts to whatever button the user pressed
    switch($_POST['check'])
    {
      case 'ignore':
        $check = new SpellCheck(stripslashes($_POST['string']), $_POST['pos']);
        $check->ignore($_POST['ignoreList']);
        break;
      case 'ignore all':
        $check = new SpellCheck(stripslashes($_POST['string']), $_POST['pos']);
        $check->ignore($_POST['ignoreList']);
        $check->addIgnoreItem($_POST['misspelled']);
        break;
      case 'change':
        $check = new SpellCheck(stripslashes($_POST['string']), $_POST['pos'],
                                $_POST['len'], $_POST['preplen']);
        $check->ignore($_POST['ignoreList']);
        $check->change($_POST['correction']);
        break;
      case 'change all':
        $check = new SpellCheck(stripslashes($_POST['string']), $_POST['pos'],
                                $_POST['len'], $_POST['preplen']);
        $check->ignore($_POST['ignoreList']);
        $check->changeAll($_POST['correction']);
        break;
      case 'done':
        break;
      default:
        $check = new SpellCheck(stripslashes(trim($_POST['content'])));
    }

    // If there is another misspelled word, let the user know
    if(is_object($check) && $check->findNext())
    {
      // Format the display string
      $check->displayString = str_replace(array("\n","\r"), '',
                              nl2br($check->displayString));
      // Get suggestions
      $first = $check->suggest();
      $sug = "\n          <option>$first</option>";
        while($suggestion = $check->suggest())
          $sug .= "\n          <option>$suggestion</option>";

      // Get the ending time stamp
      $et = dtime::elapsed($st, dtime::stamp());

      // Build the page
      $body = <<< EOBODY
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Strict//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-US" lang="en-US">
<head>
<title>Spell Check</title>
<link rel="icon" href="/jwelch.org/images/favicon.ico" type="image/x-icon" />
<link rel="shortcut icon" href="/jwelch.org/images/favicon.ico" type="image/x-icon" />
<style type="text/css" media="screen">

body
{
  margin:  0;
  padding: 0;
}

h1
{
  margin: 0 0 0 15px;
}

#container
{
  border:     1px dashed #660000;
  background: #ffeeff;
  color:      #660000;
  padding:    6px;
  margin:     4px 0 0 15px;
  width:      680px;
}

#paragraph{
  float: left;
  width: 350px;
}

#corrections
{
  border:     1px dashed #660000;
  background: #eeeeff;
  color:      #660000;
  margin:     4px;
  padding:    6px;
  float:      right;
  width:      300px;
}

#error
{
  font-weight: bold;
  color:       red;
}

#stamp
{
  margin-top:    0;
  background:    #ffeeff;
  border-bottom: 1px #bbbbbb dotted;
}

.medium
{
  width: 200px;
  float: left;
}

.clear_fix
{
  clear: both;
}

input.button
{
  background-color: pink;
  border:           1px dashed #660000;
  margin:           0 0 10px;
  float:            right;
  width:            80px;
  text-algin:       center;
}

</style>
</head>
<body>

  <!-- Start Time Stamp -->
  <p id="stamp">$et seconds</p>
  <!-- End Time Stamp -->

  <!-- Start Title -->
  <h1>Spell Check</h1>
  <!-- End Title -->

  <!-- Start Spell Check Container -->
  <div id="container">

    <!-- Start Display String -->
    <p id="paragraph">$check->displayString</p>
    <!-- End Display String -->

    <!-- Start Corrections Box -->
    <div id="corrections">
      <form name="spellcheck" method="post" action="">

        <!-- Start Main Suggestions Box -->
        <input class="medium" type="text" name="correction" value="$first" />
        <input class="button" name="check" type="submit" value="change" /><br /><br />
        <select class="medium" name="corrections" size="7" onChange="if (selectedIndex >= 0) document.spellcheck.correction.value=document.spellcheck.corrections.options[selectedIndex].text">$sug
        </select>
        <!-- End Main Suggestions Box -->

        <!-- Start Buttons -->
        <input class="button" name="check" type="submit" value="change all" />
        <input class="button" name="check" type="submit" value="ignore" />
        <input class="button" name="check" type="submit" value="ignore all" />
        <input class="button" name="check" type="submit" value="done" />
        <!-- End Buttons -->

        <!-- Start Clear Fix -->
        <br class="clear_fix" /><br />
        <!-- End Clear Fix -->

        <!-- Start Hidden Variables -->
        <input type="hidden" name="string" value="$check->string" />
        <input type="hidden" name="pos" value="$check->strpos" />
        <input type="hidden" name="len" value="$check->strlen" />
        <input type="hidden" name="preplen" value="$check->prepStrlen" />
        <input type="hidden" name="misspelled" value="$check->misspelling" />
        <input type="hidden" name="ignoreList" value="$check->ignoreString" />
        <!-- End Hidden Variables -->

        <input type="radio" name="dictionary" $slowString value="slow" />Slow (264,000 word dictionary)<br />
        <input type="radio" name="dictionary" $fastString value="fast" />Fast (36,000 word dictionary)
      </form>
    </div>
    <!-- End Corrections Box -->

    <!-- Start Clear Fix -->
    <br class="clear_fix" />
    <!-- End Clear Fix -->

  </div>
  <!-- End Spell Check Container -->

</body>
</html>
EOBODY;

    // Print out the page
    die($body);
    } else {

      // If no more misspellings are found, let the user know that
      // the spell check is complete.
      $msg = '<span> - Complete!</span>';
    }
  }
     // Chose which string to display
     if($_POST['check'] == 'done') {
       $string = $_POST['string'];
     } else {
       $string = $check->string;
     }

     // Get the ending time stamp
     $et = dtime::elapsed($st, dtime::stamp());

     // Build the page
     $body = <<< EOBODY
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Strict//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-US" lang="en-US">
<head>
<title>Spell Check</title>
<link rel="icon" href="/jwelch.org/images/favicon.ico" type="image/x-icon" />
<link rel="shortcut icon" href="/jwelch.org/images/favicon.ico" type="image/x-icon" />
<style type="text/css" media="screen">

body
{
  margin:  0;
  padding: 0;
}

h1, form
{
  margin: 0 0 4px 15px;
}

h1 span
{
  color: red;
}

#stamp
{
  border-bottom: 1px #bbbbbb dotted;
  background:    #ffeeff;
  margin-top:    0;
}

</style>
</head>
<body>

  <!-- Start Time Stamp -->
  <p id="stamp">$et seconds</p>
  <!-- End Time Stamp -->

  <!-- Start Title -->
  <h1>Spell Check$msg</h1>
  <!-- End Title -->

  <!-- Start Spell Check Form -->
  <form method="post" action="">
    <textarea rows="25" cols="80" name="content">$string</textarea><br />
    <input type="submit" name="spellcheck" value="Spell Check" />
    <input type="radio" name="dictionary" $slowString value="slow" />Slow (264,000 word dictionary)
    <input type="radio" name="dictionary" $fastString value="fast" />Fast (36,000 word dictionary)
  </form>
  <!-- End Spell Check Form -->

</body>
</html>
EOBODY;

  // Print out the page
  echo $body;
?>
