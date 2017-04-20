<?php

namespace App\Http\Controllers;
use Illuminate\Http\Request;
use App\Http\Requests;
use App\Http\Controllers\Controller;
use Illuminate\Support\Facades\DB;

class AjaxController extends Controller
{
    public function index(){
        $request = 'SELECT ligne_bus.id_ligne, nom_ligne, count(id_course) AS nb_courses FROM passage, ligne_bus WHERE passage.id_ligne = ligne_bus.id_ligne GROUP BY id_ligne';
    	$res = DB::select($request);
    	foreach ($res as $key => $ligne) {
    		$it = explode(" <> ", $ligne->nom_ligne);
    		$ligne->start = $it[0];
    		$ligne->end = $it[count($it) - 1];
            $ligne->url = "<a class=\"btn btn-primary\" href=\"line/$ligne->id_ligne\">Détails</a>";
    	}
    	
    	return response()->json(array('request' => $request, 'aaData'=> $res), 200);
    }

    public function arrets($id){
        $request = 'SELECT nom_arret FROM parcours, arret_bus WHERE parcours.id_arret = arret_bus.id_arret AND sens = 0 AND id_ligne = ' . $id . ' ORDER BY ordre LIMIT 1';
        $start = DB::select($request);
        $request = 'SELECT nom_arret FROM parcours, arret_bus WHERE parcours.id_arret = arret_bus.id_arret AND sens = 1 AND id_ligne = ' . $id . ' ORDER BY ordre LIMIT 1';
        $end = DB::select($request);
        return response()->json(array($start, $end), 200);
    }

    public function retards($id, $sens)
    {
        $request = 'SELECT nom_arret, moyenne, ordre FROM (SELECT arret_bus.id_arret, nom_arret, AVG(ecart_depart) AS moyenne FROM passage, arret_bus WHERE passage.id_point_arret = arret_bus.id_arret AND code_sens = '.$sens.' AND passage.id_ligne = ' . $id . ' GROUP BY arret_bus.id_arret) moy, parcours WHERE moy.id_arret = parcours.id_arret AND parcours.sens = '.$sens.' AND parcours.id_ligne = '.$id.' ORDER BY ordre';
        $res = DB::select($request);
        $i = 1;
        foreach ($res as $key => $arret) {
            $arret->id = $i++;
            $arret->moyenne = round($arret->moyenne);
        }
        return response()->json(array('request' => $request, 'aaData'=> $res), 200);
    }

    public function jours($id)
    {
        $request = 'SELECT count(id_course) as nb_courses FROM (SELECT DISTINCT id_course FROM passage WHERE passage.id_ligne = '.$id.') courses';
        $res = DB::select($request);
        return response()->json(array('request' => $request, 'aaData'=> $res), 200);
    }
}
