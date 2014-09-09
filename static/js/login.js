var app = angular.module('app', []);
app.controller('loginCtrl',  ['$scope', '$http', function($scope, $http) {
	$scope.loginError = '';
	$scope.loginErrorExists = function() {
		return $scope.loginError != '';
	};

	$scope.loginUser = function() {
		$scope.joinError = '';
		console.log("email=" + $scope.email);
		console.log("password=" + $scope.password);
		$http.post('/user/login', JSON.stringify({username : $scope.email, password : $scope.password})).
		success(
			function (data, status) {
				console.log("data=" + data);
				console.log("status=" + status);
				if (data.error) {
					$scope.joinError = data.errorS;
					delete $window.sessionStorage.token;
				} else {
					$window.sessionStorage.token = data.token;
					$window.location.replace('/profile');
				}
			}
		).
		error(function (data, status) {
			delete $window.sessionStorage.token;
			console.log("error data=" + data);
			console.log("error status=" + status);
			$scope.joinError = "HTTP status " + status;
		});
	};
}]);
