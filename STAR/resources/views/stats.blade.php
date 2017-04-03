@extends('layouts.app')

@section('content')
<div id="table">
	<table id="star" class="table" width="100%">
		<thead>
	        <tr>
	            <th>Numéro de ligne</th>
	            <th>Départ</th>
	            <th>Arrivée</th>
	        </tr>
	    </thead>
	    <tbody class="tbbody">
	    	
	    </tbody>
	</table>
</div>
@endsection

@section('scripts')
<link rel="stylesheet" type="text/css" href="//cdn.datatables.net/1.10.13/css/jquery.dataTables.min.css">
<script type="text/javascript" src="//cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="//cdn.datatables.net/1.10.13/js/dataTables.bootstrap.min.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$.ajax({
           type:'GET',
           url:'/getLines',
           data:'_token = <?php echo csrf_token() ?>',
           success:function(data){
              $(".request").html(data.request);
              res = data.msg;
              jQuery.each(res, function()
              {
              	$('#table tr:last').after('<tr>' + this.id_ligne + '</tr><tr>...</tr>');
              	console.log(this);
              })
              $('#table').DataTable().ajax.reload();
              return res;
           }
        });
  		var table = $('#star').DataTable(
    	{
    		"language": {
	            "url": "//cdn.datatables.net/plug-ins/1.10.13/i18n/French.json"
	        },
	        data: jQuery.parseJSON(jQuery.parseJSON())
    	});

        
});
</script>
@endsection