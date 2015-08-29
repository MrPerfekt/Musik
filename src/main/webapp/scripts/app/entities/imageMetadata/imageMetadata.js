'use strict';

angular.module('musikApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('imageMetadata', {
                parent: 'entity',
                url: '/imageMetadatas',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'musikApp.imageMetadata.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/imageMetadata/imageMetadatas.html',
                        controller: 'ImageMetadataController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('imageMetadata');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('imageMetadata.detail', {
                parent: 'entity',
                url: '/imageMetadata/{id}',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'musikApp.imageMetadata.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/imageMetadata/imageMetadata-detail.html',
                        controller: 'ImageMetadataDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('imageMetadata');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'ImageMetadata', function($stateParams, ImageMetadata) {
                        return ImageMetadata.get({id : $stateParams.id});
                    }]
                }
            })
            .state('imageMetadata.new', {
                parent: 'imageMetadata',
                url: '/new',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/imageMetadata/imageMetadata-dialog.html',
                        controller: 'ImageMetadataDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {webName: null, fileName: null, filePath: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('imageMetadata', null, { reload: true });
                    }, function() {
                        $state.go('imageMetadata');
                    })
                }]
            })
            .state('imageMetadata.edit', {
                parent: 'imageMetadata',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/imageMetadata/imageMetadata-dialog.html',
                        controller: 'ImageMetadataDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['ImageMetadata', function(ImageMetadata) {
                                return ImageMetadata.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('imageMetadata', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
