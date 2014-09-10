var app = angular.module('app', []);

app.factory('authInterceptor', function ($rootScope, $q, $window) {
  return {
    request: function (config) {
      config.headers = config.headers || {};
      if ($window.sessionStorage.token) {
        config.headers['X-SESSION-TOKEN'] = $window.sessionStorage.token;
      }
      return config;
    },
    response: function (response) {
      if (response.status === 401) {
	$window.location.replace('/login');	
        // handle the case where the user is not authenticated
      }
      return response || $q.when(response);
    }
  };
});

app.config(function ($httpProvider) {
  $httpProvider.interceptors.push('authInterceptor');
});

app.controller('joinCtrl',  ['$scope', '$http', function($scope, $http) {
	$scope.joinError = '';
	$scope.joinErrorExists = function() {
		return $scope.joinError != '';
	};

	$scope.joinUser = function() {
		$scope.joinError = '';
		console.log("email=" + $scope.email);
		console.log("password=" + $scope.password);
		$http.put('/user/join', JSON.stringify({username : $scope.email, password : $scope.password})).
		success(
			function (data, status) {
				console.log("data=" + data);
				console.log("status=" + status);
				if (data.error) 
					$scope.joinError = data.errorS;
				else
					window.location.replace('/login');
			}
		).
		error(function (data, status) {
			console.log("error data=" + data);
			console.log("error status=" + status);
			$scope.joinError = "HTTP status " + status;
		});
	};
}]);

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

app.controller('userCtrl',  ['$scope', '$http', function($scope, $http){
	$scope.user = null;

	$http.get('/user/current')
	.success(function (data, status) {
		if (data.error == 0)
			$scope.user = data.user;
		else
			$scope.user = null;		
	})
	.error(function (data, status) {
		$scope.user = null;
	});

	$scope.currUser = function () {
		return $scope.user;
	}

}]);

app.controller('logoutCtrl',  ['$scope', '$http', function($scope, $http) {
	$scope.logoutUser = function() {
		$http.post('/user/logout').
		success(
			function (data, status) {
				delete $window.sessionStorage.token;
				$window.location.replace('/');
			}
		).
		error(function (data, status) {
			delete $window.sessionStorage.token;
			$window.location.replace('/');
		});
	};
}]);


