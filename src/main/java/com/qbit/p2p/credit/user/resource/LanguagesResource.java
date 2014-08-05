package com.qbit.p2p.credit.user.resource;

import com.qbit.p2p.credit.user.dao.LanguageDAO;
import com.qbit.p2p.credit.user.model.Language;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Alexander_Sergeev
 */
@Path("languages")
@Singleton
public class LanguagesResource {

	@Inject
	private LanguageDAO languageDAO;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public LanguagesWrapper getAll() {
		return new LanguagesWrapper(languageDAO.findAll());
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Language create(Language language) {
		return languageDAO.create(language.getCode());
	}

	@GET
	@Path("create_language")
	public void createLanguages() {
		String[] languages = {"Abkhaz", "Afar", "Afrikaans", "Akan", "Albanian", "Amharic", "Arabic", "Aragonese", "Armenian", "Assamese", "Avaric", "Avestan", "Aymara", "Azerbaijani", "Bambara", "Bashkir", "Basque", "Belarusian", "Bengali", "Bihari", "Bislama", "Bosnian", "Breton", "Bulgarian", "Burmese", "Catalan; Valencian", "Chamorro", "Chechen", "Chichewa; Chewa; Nyanja", "Chinese", "Chuvash", "Cornish", "Corsican", "Cree", "Croatian", "Czech", "Danish", "Divehi; Dhivehi; Maldivian;", "Dutch", "English", "Esperanto", "Estonian", "Ewe", "Faroese", "Fijian", "Finnish", "French", "Fula; Fulah; Pulaar; Pular", "Galician", "Georgian", "German", "Greek, Modern", "Guaraní", "Gujarati", "Haitian; Haitian Creole", "Hausa", "Hebrew (modern)", "Herero", "Hindi", "Hiri Motu", "Hungarian", "Interlingua", "Indonesian", "Interlingue", "Irish", "Igbo", "Inupiaq", "Ido", "Icelandic", "Italian", "Inuktitut", "Japanese", "Javanese", "Kalaallisut, Greenlandic", "Kannada", "Kanuri", "Kashmiri", "Kazakh", "Khmer", "Kikuyu, Gikuyu", "Kinyarwanda", "Kirghiz, Kyrgyz", "Komi", "Kongo", "Korean", "Kurdish", "Kwanyama, Kuanyama", "Latin", "Luxembourgish, Letzeburgesch", "Luganda", "Limburgish, Limburgan, Limburger", "Lingala", "Lao", "Lithuanian", "Luba-Katanga", "Latvian", "Manx", "Macedonian", "Malagasy", "Malay", "Malayalam", "Maltese", "Māori", "Marathi (Marāṭhī)", "Marshallese", "Mongolian", "Nauru", "Navajo, Navaho", "Norwegian Bokmål", "North Ndebele", "Nepali", "Ndonga", "Norwegian Nynorsk", "Norwegian", "Nuosu", "South Ndebele", "Occitan", "Ojibwe, Ojibwa", "Old Slavonic", "Oromo", "Oriya", "Ossetian, Ossetic", "Panjabi, Punjabi", "Pāli", "Persian", "Polish", "Pashto, Pushto", "Portuguese", "Quechua", "Romansh", "Kirundi", "Romanian, Moldavian, Moldovan", "Russian", "Sanskrit (Saṁskṛta)", "Sardinian", "Sindhi", "Northern Sami", "Samoan", "Sango", "Serbian", "Scottish Gaelic; Gaelic", "Shona", "Sinhala, Sinhalese", "Slovak", "Slovene", "Somali", "Southern Sotho", "Spanish; Castilian", "Sundanese", "Swahili", "Swati", "Swedish", "Tamil", "Telugu", "Tajik", "Thai", "Tigrinya", "Tibetan Standard, Tibetan, Central", "Turkmen", "Tagalog", "Tswana", "Tonga (Tonga Islands)", "Turkish", "Tsonga", "Tatar", "Twi", "Tahitian", "Uighur, Uyghur", "Ukrainian", "Urdu", "Uzbek", "Venda", "Vietnamese", "Volapük", "Walloon", "Welsh", "Wolof", "Western Frisian", "Xhosa", "Yiddish", "Yoruba", "Zhuang, Chuang"};
		for (String l : languages) {
			languageDAO.create(l);
		}
	}
}
