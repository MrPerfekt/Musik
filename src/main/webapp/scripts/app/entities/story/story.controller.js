'use strict';

angular.module('musikApp')
    .controller('StoryController', function ($scope, Story, ParseLinks) {
        $scope.storys = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Story.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.storys.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.storys = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Story.get({id: id}, function(result) {
                $scope.story = result;
                $('#deleteStoryConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Story.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteStoryConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.story = {title: null, description: null, text: null, id: null};
        };
    });
