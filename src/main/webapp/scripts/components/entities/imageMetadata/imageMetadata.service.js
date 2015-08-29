'use strict';

angular.module('musikApp')
    .factory('ImageMetadata', function ($resource, DateUtils) {
        return $resource('api/imageMetadatas/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
