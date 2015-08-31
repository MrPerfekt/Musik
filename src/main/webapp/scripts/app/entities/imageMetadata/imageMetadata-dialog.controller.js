'use strict';

angular.module('musikApp').controller('ImageMetadataDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'ImageMetadata', 'User', 'Story',
        function($scope, $stateParams, $modalInstance, entity, ImageMetadata, User, Story) {

        $scope.imageMetadata = entity;
        $scope.users = User.query();
        $scope.storys = Story.query();
        $scope.load = function(id) {
            ImageMetadata.get({id : id}, function(result) {
                $scope.imageMetadata = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('musikApp:imageMetadataUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.imageMetadata.id != null) {
                ImageMetadata.update($scope.imageMetadata, onSaveFinished);
            } else {
                ImageMetadata.save($scope.imageMetadata, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
