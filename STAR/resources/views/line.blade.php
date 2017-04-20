@extends('layouts.app')

@section('content')
<div id="Chemin1" class="pull-left">
	<h3 id="parc1"></h3>
	<table id="ch1" class="table" width="100%">
		<thead>
	        <tr>
	        	<th>Arrêt</th>
	            <th>Nom</th>
	            <th>Retard moyen (en secondes)</th>
	        </tr>
	    </thead>
	    <tbody class="tbbody">
	    	
	    </tbody>
	</table>
	<!-- <ul id="arrets" class="list-group"></ul> -->
</div>
<div class="col-md-1"></div>
<div id="Chemin2" class="pull-right">
	<h3 id="parc2"></h3>
	<table id="ch2" class="table" width="100%">
		<thead>
	        <tr>
	            <th>Arrêt</th>
	            <th>Nom</th>
	            <th>Retard moyen (en secondes)</th>
	        </tr>
	    </thead>
	    <tbody class="tbbody">
	    	
	    </tbody>
	</table>
	<!-- <ul id="arrets" class="list-group"></ul> -->
</div>
@endsection

@section('scripts')
<link rel="stylesheet" type="text/css" href="//cdn.datatables.net/1.10.13/css/jquery.dataTables.min.css">
<script type="text/javascript" src="//cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="//cdn.datatables.net/1.10.13/js/dataTables.bootstrap.min.js"></script>
<script type="text/javascript">
	$(document).ready(function()
	{
		$.ajax({
			url : '/arrets/{{ $id }}',
			type : 'GET',
			success: function( data ) {
				$('#parc1').html(data[0][0].nom_arret + ' <span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span> ' + data[1][0].nom_arret)
				$('#parc2').html(data[1][0].nom_arret + ' <span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span> ' + data[0][0].nom_arret)
			}
		});
        $('#ch1').DataTable(
        {
        	"bJQueryUI": false,
	        "bAutoWidth": false,
	        "bInfo": false,
            "language": {
                "url": "//cdn.datatables.net/plug-ins/1.10.13/i18n/French.json"
            },
            ajax: '/retards/{{ $id }}/0',
            columns: [
            	{ data: 'id'},
                { data: 'nom_arret' },
                { data: 'moyenne' },
            ]
        });
        $('#ch2').DataTable(
        {
        	"bJQueryUI": false,
	        "bAutoWidth": false,
	        "bInfo": false,
            "language": {
                "url": "//cdn.datatables.net/plug-ins/1.10.13/i18n/French.json"
            },
            ajax: '/retards/{{ $id }}/1',
            columns: [
            	{ data: 'id'},
                { data: 'nom_arret' },
                { data: 'moyenne' },
            ]
        });    
    });
</script>
@endsection