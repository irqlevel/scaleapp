var loginApp = angular.module('loginApp', []);
loginApp.controller('loginCtrl',  ['$scope', '$http', function($scope, $http) {
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
				if (data.error) 
					$scope.joinError = data.errorS;
				else
					window.location.replace('/profile');
			}
		).
		error(function (data, status) {
			console.log("error data=" + data);
			console.log("error status=" + status);
			$scope.joinError = "HTTP status " + status;
		});
	};
}]);
