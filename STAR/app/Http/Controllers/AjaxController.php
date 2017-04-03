<?php

namespace App\Http\Controllers;
use Illuminate\Http\Request;
use App\Http\Requests;
use App\Http\Controllers\Controller;
use Illuminate\Support\Facades\DB;

class AjaxController extends Controller
{
    public function index(){
    	$request = 'select * from ligne_bus';
    	$res = DB::select($request);
    	foreach ($res as $key => $ligne) {
    		$it = explode(" <> ", $ligne->nom_ligne);
    		$ligne->start = $it[0];
    		$ligne->end = $it[count($it) - 1];
    	}
    	
    	return response()->json(array('request' => $request, 'aaData'=> $res), 200);
   }
}
