var userProfileModule = angular.module("user-profile");

userProfileModule.factory("userProfileService", function($rootScope, usersProfileResource, currencyResource, categoriesResource, languagesResource) {

	function getAllCurrencies(callback) {
		var currenciesResponse = currencyResource.findAll();
		currenciesResponse.$promise.then(function() {
			var currencies = currenciesResponse.currencies;
			if (!currencies) {
				return;
			}
			if (callback) {
				callback(currencies);
			}
		});
	}

	function getAllCategories(callback) {
		var categoriesResponse = categoriesResource.findAll();
		categoriesResponse.$promise.then(function() {
			var categories = categoriesResponse.categories;
			if (!categories) {
				return;
			}
			if (callback) {
				callback(categories);
			}
		});
	}
	
	function getAllCategoriesTitle(callback) {
		getAllCategories(function(categories) {
			var allCategories = [];
			for (var i in categories) {
				allCategories.push(categories[i].code);
			}
			if (callback) {
				callback(allCategories);
			}
		});
		
		var categoriesResponse = categoriesResource.findAll();
		categoriesResponse.$promise.then(function() {
			var allCategories = [];
			for (var i in categoriesResponse.categories) {
				allCategories.push(categoriesResponse.categories[i].title);
			}
			if (callback) {
				callback(allCategories);
			}
		});
	}

	function getAllLanguages(callback) {
		var languagesResponse = languagesResource.findAll();
		languagesResponse.$promise.then(function() {
			var allLanguages = [];
			for (var i in languagesResponse.languages) {
				allLanguages.push(languagesResponse.languages[i].title);
			}
			if (callback) {				
				callback(allLanguages);
			}
			return allLanguages;
		});
	}



	return {
		getAllCurrencies: getAllCurrencies,
		getAllCategories: getAllCategories,
		getAllCategoriesTitle: getAllCategoriesTitle,
		getAllLanguages: getAllLanguages
	};
});