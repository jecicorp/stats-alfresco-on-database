<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="shortcut icon" type="image/x-icon" href="ico/favicon.png" />

    <title>${title} - v.${version}</title>
    

    <!-- Bootstrap core CSS -->
    <link href="css/bootstrap.css" rel="stylesheet" type="text/css" />
 

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="js/html5shiv.js"></script>
      <script src="js/respond.min.js"></script>
    <![endif]-->
  </head>

  <body>

  <div class="container">
    <div class="header">
      <h3 class="text-muted"><#if dir??>-= Directory : ${dir.label} =-<#else>${title}</#if></h3>
    </div><!-- header -->
  
  </div>
  
  <#if dir??>
  <h4>Links : 
  	<#if dir.localSize??><a href="print?nodeid=${dir.parent?c}">Go to Parent<#else>Racine</#if></a>
  	- <a href="/">Go to Home</a>
  </h4>
  <table>
	<tr>
		<td><b>Full Path</b> :</td>
		<td><#if path??>${path}</#if></td>
	</tr>
	<tr>
		<td><b>Node DB Id</b> :</td>
		<td>${dir.nodeid}</td>
	</tr>
	
	<tr>
		<td></td>
		<td></td>
	</tr>
	<tr>
		<td><b>Local Size</b> :</td>
		<td><#if dir.localSizeReadable??>${dir.localSizeReadable}</#if></td>
	</tr>
	<tr>
		<td><b>Aggregate Size</b> :</td>
		<td><#if dir.dirSizeReadable??>${dir.dirSizeReadable}</#if></td>
	</tr>
	<tr>
		<td><b>Full Size</b> :</td>
		<td><#if dir.fullSizeReadable??>${dir.fullSizeReadable}</#if></td>
	</tr>
  </table>
  <hr />
  </#if>
  
  <table>
  <tr>
  	<th>Folder Name</th>
  	<th>Local Size</th>
  	<th>Aggregate Size</th>
  	<th>Full Size</th>
  </tr>
  <#list nodes as node>
  <tr>
  	<td><a href="print?nodeid=${node.nodeid?c}">${node.label}</a></td>
  	<td>${node.localSizeReadable}</td>
  	<td>${node.dirSizeReadable}</td>
  	<td>${node.fullSizeReadable}</td>
  </tr>
  </#list>
  </table>
  

  <#if error??>
  <div><h3>ERROR</h3> <p><${error}</p></div>
  </#if>

    <!-- Bootstrap core JavaScript
    ================================================== -->

	<!-- production -->	
	<script type="text/javascript" src="js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="js/bootstrap.min.js"></script>

    <!-- activate French translation -->
    <script type="text/javascript" src="js/i18n/fr.js"></script>

</script>
</body>
</html>
