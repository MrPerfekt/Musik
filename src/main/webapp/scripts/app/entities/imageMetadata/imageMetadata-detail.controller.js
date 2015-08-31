'use strict';

angular.module('musikApp')
    .controller('ImageMetadataDetailController', function ($scope, $rootScope, $stateParams, entity, ImageMetadata, User, Story) {
        $scope.imageMetadata = entity;
        $scope.load = function (id) {
            ImageMetadata.get({id: id}, function(result) {
                $scope.imageMetadata = result;
            });
        };
        $rootScope.$on('musikApp:imageMetadataUpdate', function(event, result) {
            $scope.imageMetadata = result;
        });
    });
