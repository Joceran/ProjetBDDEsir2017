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
    	$msg = DB::select($request);
    	return response()->json(array('request' => $request, 'msg'=> $msg), 200);
   }
}
