<?php

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

Route::get('/', function () {
    return redirect('home');
});

Route::get('home', function()
{
	return view('home');
});

Route::get('stats', function()
{
	return view('stats');
});

Route::get('getLines','AjaxController@index');

Route::get('line/{id}',function($id)
{
	return view('line', ['id' => $id]);
});

Route::get('arrets/{id}','AjaxController@arrets');

Route::get('retards/{id}/{sens}','AjaxController@retards');

Route::get('jours/{id}', 'AjaxController@jours');

