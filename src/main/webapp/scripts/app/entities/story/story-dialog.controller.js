'use strict';

angular.module('musikApp').controller('StoryDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Story', 'User', 'ImageMetadata',
        function($scope, $stateParams, $modalInstance, entity, Story, User, ImageMetadata) {

        $scope.story = entity;
        $scope.users = User.query();
        $scope.imagemetadatas = ImageMetadata.query();
        $scope.load = function(id) {
            Story.get({id : id}, function(result) {
                $scope.story = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('musikApp:storyUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.story.id != null) {
                Story.update($scope.story, onSaveFinished);
            } else {
                Story.save($scope.story, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
