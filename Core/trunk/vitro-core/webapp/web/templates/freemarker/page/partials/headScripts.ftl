<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for scripts that must be loaded in the head -->

<!-- script for enabling new HTML5 semantic markup in IE browsers-->
<script type="text/javascript" src="${urls.base}/js/html5.js"></script>
<script type="text/javascript" src="${urls.base}/js/jquery.js"></script>
<script type="text/javascript" src="${urls.base}/js/vitroUtils.js"></script>

${headScripts.tags}

<!--[if lt IE 7]>
<script type="text/javascript" src="${urls.base}/js/jquery_plugins/supersleight.js"></script>
<![endif]-->