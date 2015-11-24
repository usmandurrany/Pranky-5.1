<?php
if (isset($_GET['model'])&&isset($_GET['gcm_id'])) {
	

$mysqli = new mysqli("localhost", "root", "supernova", "pranky");

/* check connection */
if ($mysqli->connect_errno) {
    printf("Connect failed: %s\n", $mysqli->connect_error);
    exit();
}

$gcm_id = $_GET["gcm_id"];
$model= $_GET["model"];

$app_id = randomizer();

if (auth_devices($model,$gcm_id,$app_id)) {
	json_encode(array("app_id"=>'$app_id'));
}
else
{
	echo "Error";
}




function randomizer($length = 4) {
  $characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
  $charactersLength = strlen($characters);
  $randomString = '';

  for ($i = 0; $i < $length; $i++) {
      $randomString .= $characters[mt_rand(0, $charactersLength - 1)];
  }


$query = "SELECT * FROM  auth_devices WHERE app_id = '$randomString'";

$result = $mysqli->query($query);

if ($result->num_rows >0) {
    /* fetch associative array */
    $row = $result->fetch_assoc() 
 	if ($row['app_id'] == $app_id) {
 	   	$randomString = randomizer();
 	   }   
    
}
    /* free result set */
$result->free();
/* close connection */
$mysqli->close();



  return $randomString;
}



function boolean auth_device($model,$gcm_id,$app_id){
$insert = "INSERT INTO auth_devices (model,gcm_id,app_id) values('$model','$gcm_id','$app_id')";  
return $mysqli->query($insert);
}

}
?>