'use strict';

angular.module('musikApp')
    //.directive('editableStory', function () {
    .directive('editableStory', function ($http, Story) {
        return {
            restrict: 'E',
            templateUrl: 'scripts/components/editableStory/editableStory.html',
            scope: {
                id: "@"
            },
            link: function (scope) {
                Story.get({id: scope.id}, function (result) {
                    scope.story = result;
                    console.log(result);
                });
                $http.get('api/images/getPathsForStory', {
                    params: {storyId: scope.id},
                    withCredentials: true,
                    transformRequest: angular.identity
                }).success(function (response) {
                    scope.imagePaths = response;
                }).error(function (response) {
                    console.log(response);
                });
                var uploadNewWebName = function (files) {
                    var fd = new FormData();
                    fd.append("file", files[0]);
                    $http.post('api/images/uploadAndSearchWebName', fd, {
                        params: {webNameBase: scope.id},
                        withCredentials: true,
                        headers: {'Content-Type': undefined},
                        transformRequest: angular.identity
                    }).success(function (response) {
                        console.log(response);
                    }).error(function (response) {
                        console.log(response);
                    });
                };
                scope.add = function(){
                    console.log("add");
                    uploadNewWebName([null]);
                }
            }
        };
    });
