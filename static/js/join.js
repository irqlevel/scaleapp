var joinApp = angular.module('joinApp', []);
joinApp.controller('joinCtrl',  ['$scope', '$http', function($scope, $http) {
	$scope.joinError = '';
	$scope.joinErrorExists = function() {
		return $scope.joinError != '';
	};

	$scope.joinUser = function() {
		console.log("email=" + $scope.email);
		console.log("password=" + $scope.password);
		$http.put('/user/join', JSON.stringify({email : $scope.email, password : $scope.password})).
		success(
			function (data, status) {
				console.log("data=" + data);
				console.log("status=" + status);
			}
		).
		error(function (data, status) {
			console.log("error data=" + data);
			console.log("error status=" + status);
			$scope.joinError = status;
		});
	};
}]);
