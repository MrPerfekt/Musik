/* globals $ */
'use strict';

angular.module('musikApp')
    .directive('musikAppPagination', function() {
        return {
            templateUrl: 'scripts/components/form/pagination.html'
        };
    });
