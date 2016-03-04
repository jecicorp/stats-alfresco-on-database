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
      <h3 class="text-muted">${title}</h3>
    </div><!-- header -->
  
  </div>
  
  <#if dir??>
  <h3 class="text-muted">Name Directory : ${dir.label}</h3>
  <h4 class="text-muted">Node Id : ${dir.nodeid}</h4>
  <h4>Size : <#if dir.localSize??>${dir.localSize}</#if></h4>
  <h4>Parent Id : <#if dir.localSize??><a href="print?nodeid=${dir.parent?c}">Parent<#else>Racine</#if></a></h4>
  <h4>Path : <#if path??>${path}</#if></h4>
  
  </#if>
  
  
  
  <ul>
  <#list nodes as node>
  <li><a href="print?nodeid=${node.nodeid?c}">${node.label}</a>  ${node.localSizeReadable}</li>
  </#list>
  </ul>
  
  <#list nodes as node>
  ${node.localSize?c} + </li>
  </#list>

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
