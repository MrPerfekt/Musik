'use strict';

angular.module('musikApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


