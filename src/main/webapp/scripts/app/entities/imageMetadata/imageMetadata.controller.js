'use strict';

angular.module('musikApp')
    .controller('ImageMetadataController', function ($scope, ImageMetadata, ParseLinks) {
        $scope.imageMetadatas = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            ImageMetadata.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.imageMetadatas.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.imageMetadatas = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            ImageMetadata.get({id: id}, function(result) {
                $scope.imageMetadata = result;
                $('#deleteImageMetadataConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            ImageMetadata.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteImageMetadataConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.imageMetadata = {webName: null, fileName: null, filePath: null, id: null};
        };
    });
