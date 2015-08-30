'use strict';

angular.module('musikApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('story', {
                parent: 'entity',
                url: '/storys',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'musikApp.story.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/story/storys.html',
                        controller: 'StoryController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('story');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('story.detail', {
                parent: 'entity',
                url: '/story/{id}',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'musikApp.story.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/story/story-detail.html',
                        controller: 'StoryDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('story');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Story', function($stateParams, Story) {
                        return Story.get({id : $stateParams.id});
                    }]
                }
            })
            .state('story.new', {
                parent: 'story',
                url: '/new',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/story/story-dialog.html',
                        controller: 'StoryDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {title: null, description: null, text: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('story', null, { reload: true });
                    }, function() {
                        $state.go('story');
                    })
                }]
            })
            .state('story.edit', {
                parent: 'story',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/story/story-dialog.html',
                        controller: 'StoryDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Story', function(Story) {
                                return Story.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('story', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
