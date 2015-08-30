'use strict';

angular.module('musikApp')
    .directive('editableImage', function ($http) {
        return {
            restrict: 'AE',
            templateUrl: 'scripts/components/image/image.html',

            scope: {
                name: "@",
                size: "@",
                location: "@"
            },

            link: function (scope) {
                if (!scope.location) {
                    scope.location = '';
                }

                var update = function () {
                    $http.get('api/images/getPath', {
                        params: {size: scope.size, webName: scope.name},
                        withCredentials: true,
                        transformRequest: angular.identity,
                        //cache: true
                    }).success(function (response) {
                        scope.imagePath = response;
                    }).error(function (response) {
                        console.log(response);
                    });
                };

                scope.uploadFile = function (files) {
                    var fd = new FormData();
                    fd.append("file", files[0]);
                    $http.post('api/images/upload', fd, {
                        params: {location: scope.location, webName: scope.name},
                        withCredentials: true,
                        headers: {'Content-Type': undefined},
                        transformRequest: angular.identity
                    }).success(function (response) {
                        update();
                    }).error(function (response) {
                        console.log(response);
                    });
                };

                update();
            }
        }
    });
