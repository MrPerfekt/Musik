'use strict';

angular.module('musikApp')
    .controller('MainController', function ($scope, $http, Principal) {
        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });

        ////$scope.imagePath = "/resources/images/original/testLocation/pairprogramming-0.jpg";
        //$http.get('api/images/getPath',  {
        //    params: {size: "original", webName: "testWebName"},
        //    withCredentials: true,
        //    transformRequest: angular.identity,
        //    //cache: true
        //}).success(function (response) {
        //    $scope.imagePath = response;
        //    return response;
        //}).error(function (response) {
        //    console.log(response);
        //    return response;
        //});
        //
        //$scope.uploadFile = function(files) {
        //    var fd = new FormData();
        //    fd.append("file", files[0]);
        //    $http.post('api/images/upload', fd,  {
        //        params: {location: "testLocation", webName: "testWebName"},
        //        withCredentials: true,
        //        headers: {'Content-Type': undefined },
        //        transformRequest: angular.identity
        //    }).success(function (response) {
        //        console.log(response);
        //        return response;
        //    }).error(function (response) {
        //        console.log(response);
        //        return response;
        //    });
        //
        //};
    });
