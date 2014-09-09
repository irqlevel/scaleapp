var app = angular.module('app', []);
app.config(function ($routeProvider, $httpProvider) {
    $httpProvider.defaults.withCredentials = true;
});

