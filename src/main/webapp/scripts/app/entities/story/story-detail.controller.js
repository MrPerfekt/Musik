'use strict';

angular.module('musikApp')
    .controller('StoryDetailController', function ($scope, $rootScope, $stateParams, entity, Story, User, ImageMetadata) {
        $scope.story = entity;
        $scope.load = function (id) {
            Story.get({id: id}, function(result) {
                $scope.story = result;
            });
        };
        $rootScope.$on('musikApp:storyUpdate', function(event, result) {
            $scope.story = result;
        });
    });
