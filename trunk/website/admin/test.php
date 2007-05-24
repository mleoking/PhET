<?php

    foreach($_REQUEST as $k => $r) {
        print(urldecode("$k => $r"));
    }
    
    print "<br/>";
    
    print_r($_REQUEST['test']);

    print "<br/>";
    
    print <<<EOT
        <form method="post" action="test.php">
            <select name="test[]" multiple="multiple" size="5">
                <option value="Option 1">Option 1</option>
                <option value="Option 2">Option 2</option>            
                <option value="Option 3">Option 3</option> 
                <option value="Option 4">Option 4</option>
                <option value="Option 5">Option 5</option>                                      
                <option value="Option 6">Option 6</option>                                      
            </select>
            
            <input type="submit" name="Submit" />
        </form>
EOT;

    
?>