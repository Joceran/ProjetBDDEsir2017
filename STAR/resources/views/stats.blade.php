@extends('layouts.app')

@section('content')
<div id="table">
	<table id="star" class="table" width="100%">
		<thead>
	        <tr>
	            <th>Numéro de ligne</th>
	            <th>Départ</th>
	            <th>Arrivée</th>
                <th>Nombre de courses</th>
                <th>Détails</th>
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
        var table = $('#star').DataTable(
        {
            "language": {
                "url": "//cdn.datatables.net/plug-ins/1.10.13/i18n/French.json"
            },
            ajax: '/getLines',
            columns: [
                { data: 'id_ligne' },
                { data: 'start' },
                { data: 'end' },
                { data: 'nb_courses'},
                { data: 'url'},
            ]
        });    
    });
</script>
@endsection