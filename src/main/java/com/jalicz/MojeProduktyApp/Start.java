package com.jalicz.MojeProduktyApp;

import com.jalicz.MojeProduktyApp.GUI.frames.MenuFrame;
import com.jalicz.MojeProduktyApp.files.FileManager;
import com.jalicz.MojeProduktyApp.files.Log;
import com.jalicz.MojeProduktyApp.files.search.SearchEngine;
import com.jalicz.MojeProduktyApp.model.Produkt;
import com.jalicz.MojeProduktyApp.model.SkladovyObjekt;
import com.jalicz.MojeProduktyApp.model.TypeID;

public class Start {

	public static final String
			appName = "Moje Produkty",
			appVersion = "1.1.0",
			companyWebsite = "jalicz.com",
			copyright = "© 2022 Všechna práva vyhrazena";

	public static void main(String[] args) {
		Log.warn(Start.copyright + " společností " + Start.companyWebsite + "!");
		Log.info("Spouštím aplikaci \"" + Start.appName + "\" (verze " + Start.appVersion + ")...");

		new MenuFrame();
	}
}