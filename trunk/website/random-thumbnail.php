<?php
    $thumbnails = glob("./admin/uploads/*.jpg");
    
    $random_key = array_rand($thumbnails);
    
    $thumbnail = imagecreatefromjpeg($thumbnails[$random_key]);
    
    imagepng($thumbnail);
    imagedestroy($thumbnail); 
?>