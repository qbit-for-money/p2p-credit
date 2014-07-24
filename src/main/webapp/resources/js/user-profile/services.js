var userProfileModule = angular.module("user-profile");

userProfileModule.factory("userProfileService", function($rootScope, usersProfileResource, currencyResource, categoriesResource, languagesResource) {
	//var allLanguages = ["Abkhaz", "Afar", "Afrikaans", "Akan", "Albanian", "Amharic", "Arabic", "Aragonese", "Armenian", "Assamese", "Avaric", "Avestan", "Aymara", "Azerbaijani", "Bambara", "Bashkir", "Basque", "Belarusian", "Bengali", "Bihari", "Bislama", "Bosnian", "Breton", "Bulgarian", "Burmese", "Catalan; Valencian", "Chamorro", "Chechen", "Chichewa; Chewa; Nyanja", "Chinese", "Chuvash", "Cornish", "Corsican", "Cree", "Croatian", "Czech", "Danish", "Divehi; Dhivehi; Maldivian;", "Dutch", "English", "Esperanto", "Estonian", "Ewe", "Faroese", "Fijian", "Finnish", "French", "Fula; Fulah; Pulaar; Pular", "Galician", "Georgian", "German", "Greek, Modern", "Guaraní", "Gujarati", "Haitian; Haitian Creole", "Hausa", "Hebrew (modern)", "Herero", "Hindi", "Hiri Motu", "Hungarian", "Interlingua", "Indonesian", "Interlingue", "Irish", "Igbo", "Inupiaq", "Ido", "Icelandic", "Italian", "Inuktitut", "Japanese", "Javanese", "Kalaallisut, Greenlandic", "Kannada", "Kanuri", "Kashmiri", "Kazakh", "Khmer", "Kikuyu, Gikuyu", "Kinyarwanda", "Kirghiz, Kyrgyz", "Komi", "Kongo", "Korean", "Kurdish", "Kwanyama, Kuanyama", "Latin", "Luxembourgish, Letzeburgesch", "Luganda", "Limburgish, Limburgan, Limburger", "Lingala", "Lao", "Lithuanian", "Luba-Katanga", "Latvian", "Manx", "Macedonian", "Malagasy", "Malay", "Malayalam", "Maltese", "Māori", "Marathi (Marāṭhī)", "Marshallese", "Mongolian", "Nauru", "Navajo, Navaho", "Norwegian Bokmål", "North Ndebele", "Nepali", "Ndonga", "Norwegian Nynorsk", "Norwegian", "Nuosu", "South Ndebele", "Occitan", "Ojibwe, Ojibwa", "Old Slavonic", "Oromo", "Oriya", "Ossetian, Ossetic", "Panjabi, Punjabi", "Pāli", "Persian", "Polish", "Pashto, Pushto", "Portuguese", "Quechua", "Romansh", "Kirundi", "Romanian, Moldavian, Moldovan", "Russian", "Sanskrit (Saṁskṛta)", "Sardinian", "Sindhi", "Northern Sami", "Samoan", "Sango", "Serbian", "Scottish Gaelic; Gaelic", "Shona", "Sinhala, Sinhalese", "Slovak", "Slovene", "Somali", "Southern Sotho", "Spanish; Castilian", "Sundanese", "Swahili", "Swati", "Swedish", "Tamil", "Telugu", "Tajik", "Thai", "Tigrinya", "Tibetan Standard, Tibetan, Central", "Turkmen", "Tagalog", "Tswana", "Tonga (Tonga Islands)", "Turkish", "Tsonga", "Tatar", "Twi", "Tahitian", "Uighur, Uyghur", "Ukrainian", "Urdu", "Uzbek", "Venda", "Vietnamese", "Volapük", "Walloon", "Welsh", "Wolof", "Western Frisian", "Xhosa", "Yiddish", "Yoruba", "Zhuang, Chuang"];
	function getAllCurrencies(callback) {
		var currenciesResponse = currencyResource.findAll();
		var allCurrencies = [];
		currenciesResponse.$promise.then(function() {
			var currencies = currenciesResponse.currencies;
			if (!currencies) {
				return;
			}
			/*for (var i = 0; i < currencies.length; i++) {
				var currency = currencies[i];
				allCurrencies.push(currency.code);
			}*/
			if (callback) {
				callback(currencies);
			}
		});
	}

	function getAllCategories(callback) {
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
		getAllLanguages: getAllLanguages
	};
});