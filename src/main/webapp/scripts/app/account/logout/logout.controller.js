'use strict';

angular.module('musikApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
