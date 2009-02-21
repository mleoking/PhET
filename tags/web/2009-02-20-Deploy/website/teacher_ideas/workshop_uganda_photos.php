<?php


// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("page_templates/SitePage.php");

class UgandaPhotosPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $workshop_materials_location = PHET_DIST_ROOT."workshops/";
        $uganda_photos_location = PHET_DIST_ROOT."workshops/Uganda/photos/";
        $uganda_photos_thumbnail_location = PHET_DIST_ROOT."workshops/Uganda/thumbnails/";

        print <<<EOT
<div class="workshop_uganda_photos">

<table>
<tr>
<td>
<div class="uganda_photo left">
  <a href="{$uganda_photos_location}IMG_0712.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0712.JPG" alt="The entrance to the medical center where the workshops were held" />
    <br />
    <span class="uganda_photo_caption">The entrance to the medical center where the workshops were held</span>
  </a>
</div>
</td>

<td>
<div class="uganda_photo right">
  <a href="{$uganda_photos_location}IMG_0713.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0713.JPG" alt="Patrick and Silas, the Pilgrim IT staff, seeing the simulations for the first time" />
    <br />
    <span class="uganda_photo_caption">Patrick and Silas, the Pilgrim IT staff, seeing the simulations for the first time</span>
  </a>
</div>
</td>
</tr>

<tr>
<td>
<div class="uganda_photo left">
  <a href="{$uganda_photos_location}IMG_0714.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0714.JPG" alt="Setting up the laptops for the workshop" />
    <br />
    <span class="uganda_photo_caption">Setting up the laptops for the workshop</span>
  </a>
</div>
</td>

<td>
<div class="uganda_photo right">
  <a href="{$uganda_photos_location}IMG_0716.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0716.JPG" alt="The room only had 5 power outlets, so we needed a lot of power strips" />
    <br />
    <span class="uganda_photo_caption">The room only had 5 power outlets, so we needed a lot of power strips</span>
  </a>
</div>
</td>
</tr>

<tr>
<td>
<div class="uganda_photo left">
  <a href="{$uganda_photos_location}IMG_0799.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0799.JPG" alt="Two workshop participants exploring the simulations" />
    <br />
    <span class="uganda_photo_caption">Two workshop participants exploring the simulations</span>
  </a>
</div>
</td>

<td>
<div class="uganda_photo right">
  <a href="{$uganda_photos_location}IMG_0801.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0801.JPG" alt="Workshop participants" />
    <br />
    <span class="uganda_photo_caption">Workshop participants</span>
  </a>
</div>
</td>
</tr>

<tr>
<td>
<div class="uganda_photo left">
  <a href="{$uganda_photos_location}IMG_0802.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0802.JPG" alt="Making a circuit with Circuit Construction Kit" />
    <br />
    <span class="uganda_photo_caption">Making a circuit with Circuit Construction Kit</span>
  </a>
</div>
</td>

<td>
<div class="uganda_photo right">
  <a href="{$uganda_photos_location}IMG_0807.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0807.JPG" alt="Sam helping workshop participants" />
    <br />
    <span class="uganda_photo_caption">Sam helping workshop participants</span>
  </a>
</div>
</td>
</tr>

<tr>
<td>
<div class="uganda_photo left">
  <a href="{$uganda_photos_location}IMG_0809.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0809.JPG" alt="Everyone exploring" />
    <br />
    <span class="uganda_photo_caption">Everyone exploring</span>
  </a>
</div>
</td>

<td>
<div class="uganda_photo right">
  <a href="{$uganda_photos_location}IMG_0811.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0811.JPG" alt="A question" />
    <br />
    <span class="uganda_photo_caption">A question</span>
  </a>
</div>
</td>
</tr>

<tr>
<td>
<div class="uganda_photo left">
  <a href="{$uganda_photos_location}IMG_0814.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0814.JPG" alt="Exploring Charges and Fields" />
    <br />
    <span class="uganda_photo_caption">Exploring Charges and Fields</span>
  </a>
</div>
</td>

<td>
<div class="uganda_photo right">
  <a href="{$uganda_photos_location}IMG_0815.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0815.JPG" alt="Questions about Faraday's Electromagnetic Lab" />
    <br />
    <span class="uganda_photo_caption">Questions about Faraday's Electromagnetic Lab</span>
  </a>
</div>
</td>
</tr>

<tr>
<td>
<div class="uganda_photo left">
  <a href="{$uganda_photos_location}IMG_0820.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0820.JPG" alt="Questions about The Photoelectric Effect" />
    <br />
    <span class="uganda_photo_caption">Questions about The Photoelectric Effect</span>
  </a>
</div>
</td>

<td>
<div class="uganda_photo right">
  <a href="{$uganda_photos_location}IMG_0821.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0821.JPG" alt="The diesel generator that kept the computers running when the power went out" />
    <br />
    <span class="uganda_photo_caption">The diesel generator that kept the computers running when the power went out</span>
  </a>
</div>
</td>
</tr>

<tr>
<td>
<div class="uganda_photo left">
  <a href="{$uganda_photos_location}IMG_0822.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0822.JPG" alt="Lunch" />
    <br />
    <span class="uganda_photo_caption">Lunch</span>
  </a>
</div>
</td>

<td>
<div class="uganda_photo right">
  <a href="{$uganda_photos_location}IMG_0826.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0826.JPG" alt="Working through a lab activity using Circuit Construction Kit" />
    <br />
    <span class="uganda_photo_caption">Working through a lab activity using Circuit Construction Kit</span>
  </a>
</div>
</td>
</tr>

<tr>
<td>
<div class="uganda_photo left">
  <a href="{$uganda_photos_location}IMG_0840.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0840.JPG" alt="Staff trying to get the internet working during a power outage to contact the outreach program at CU" />
    <br />
    <span class="uganda_photo_caption">Staff trying to get the internet working during a power outage to contact the outreach program at CU</span>
  </a>
</div>
</td>

<td>
<div class="uganda_photo right">
  <a href="{$uganda_photos_location}IMG_0845.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0845.JPG" alt="Handing out workshop participation certificates" />
    <br />
    <span class="uganda_photo_caption">Handing out workshop participation certificates</span>
  </a>
</div>
</td>
</tr>

<tr>
<td>
<div class="uganda_photo left">
  <a href="{$uganda_photos_location}IMG_0858.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0858.JPG" alt="A visit to King's College, Budo, one of the most elite secondary schools in Uganda" />
    <br />
    <span class="uganda_photo_caption">A visit to King's College, Budo, one of the most elite secondary schools in Uganda</span>
  </a>
</div>
</td>

<td>
<div class="uganda_photo right">
  <a href="{$uganda_photos_location}IMG_0859.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0859.JPG" alt="The well-manicured campus was a sharp contrast to the conditions of the rural schools where our workshop participants taught" />
    <br />
    <span class="uganda_photo_caption">The well-manicured campus was a sharp contrast to the conditions of the rural schools where our workshop participants taught</span>
  </a>
</div>
</td>
</tr>

<tr>
<td>
<div class="uganda_photo left">
  <a href="{$uganda_photos_location}IMG_0863.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0863.JPG" alt="But there were still chickens wandering through campus" />
    <br />
    <span class="uganda_photo_caption">But there were still chickens wandering through campus</span>
  </a>
</div>
</td>

<td>
<div class="uganda_photo right">
  <a href="{$uganda_photos_location}IMG_0865.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0865.JPG" alt="The physics teachers' lounge" />
    <br />
    <span class="uganda_photo_caption">The physics teachers' lounge</span>
  </a>
</div>
</td>
</tr>

<tr>
<td>
<div class="uganda_photo left">
  <a href="{$uganda_photos_location}IMG_0868.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0868.JPG" alt="Installing the simulations" />
    <br />
    <span class="uganda_photo_caption">Installing the simulations</span>
  </a>
</div>
</td>

<td>
<div class="uganda_photo right">
  <a href="{$uganda_photos_location}IMG_0870.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0870.JPG" alt="This school had plenty of laboratory equipment" />
    <br />
    <span class="uganda_photo_caption">This school had plenty of laboratory equipment</span>
  </a>
</div>
</td>
</tr>

<tr>
<td>
<div class="uganda_photo left">
  <a href="{$uganda_photos_location}IMG_0873.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0873.JPG" alt="The physics teachers were very enthusiastic about the simulations" />
    <br />
    <span class="uganda_photo_caption">The physics teachers were very enthusiastic about the simulations</span>
  </a>
</div>
</td>

<td>
<div class="uganda_photo right">
  <a href="{$uganda_photos_location}IMG_0876.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0876.JPG" alt="The physics laboratory" />
    <br />
    <span class="uganda_photo_caption">The physics laboratory</span>
  </a>
</div>
</td>
</tr>

<tr>
<td>
<div class="uganda_photo left">
  <a href="{$uganda_photos_location}IMG_0879.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0879.JPG" alt="This drum is used the way we use a bell to signal the start of class" />
    <br />
    <span class="uganda_photo_caption">This drum is used the way we use a bell to signal the start of class</span>
  </a>
</div>
</td>

<td>
<div class="uganda_photo right">
  <a href="{$uganda_photos_location}IMG_0888.JPG">
    <img src="{$uganda_photos_thumbnail_location}IMG_0888.JPG" alt="Meeting with the headmaster" />
    <br />
    <span class="uganda_photo_caption">Meeting with the headmaster</span>
  </a>
</div>
</td>
</tr>
</table>

</div>

EOT;
    }
}

$page = new UgandaPhotosPage("Photos of the PhET Uganda Workshop", NAV_TEACHER_IDEAS, null);
$page->add_stylesheet("css/uganda.css");
$page->update();
$page->render();

?>