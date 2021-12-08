/*
 * Copyright (c) 2017 Proton Technologies AG
 *
 * This file is part of ProtonVPN.
 *
 * ProtonVPN is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ProtonVPN is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ProtonVPN.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.protonvpn.android.utils

import android.content.Context
import android.os.Build
import com.PrimeApp
import java.util.Locale

object CountryTools {

    fun getFlagResource(context: Context, flag: String?): Int {
        var desiredFlag = 0
        flag?.let {
            desiredFlag = context.resources.getIdentifier(
                it.toLowerCase(Locale.ROOT), "drawable", context.packageName
            )
        }
        return if (desiredFlag > 0)
            desiredFlag
        else
            context.resources.getIdentifier("zz", "drawable", context.packageName)
    }

    fun getPreferredLocale(context: Context): Locale {
        val configuration = context.resources.configuration
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            configuration.locales[0] else configuration.locale
        return if (locale.language in Constants.AVAILABLE_LOCALES) locale else Locale.US
    }

    fun getFullName(country: String?): String {
        val locale = Locale("", country)
        val localized = locale.getDisplayCountry(
                getPreferredLocale(PrimeApp.getAppContext()))
        return if (localized.length < MAX_LOCALIZED_LENGTH)
            localized
        else
            locale.getDisplayCountry(Locale.US)
    }

    val codeToMapCountryName = mapOf(
        "AF" to "Afghanistan",
        "AO" to "Angola",
        "AL" to "Albania",
        "AE" to "UnitedArabEmirates",
        "AR" to "Argentina",
        "AM" to "Armenia",
        "AU" to "Australia",
        "AT" to "Austria",
        "AZ" to "Azerbaijan",
        "BI" to "Burundi",
        "BE" to "Belgium",
        "BJ" to "Benin",
        "BF" to "BurkinaFaso",
        "BD" to "Bangladesh",
        "BG" to "Bulgaria",
        "BA" to "BosniaandHerzegovina",
        "BY" to "Belarus",
        "BZ" to "Belize",
        "BO" to "Bolivia",
        "BR" to "Brazil",
        "BN" to "BruneiDarussalam",
        "BT" to "Bhutan",
        "BW" to "Botswana",
        "CF" to "CentralAfricanRepublic",
        "CA" to "Canada",
        "CH" to "Switzerland",
        "CL" to "Chile",
        "CN" to "China",
        "CI" to "Côted'Ivoire",
        "CM" to "Cameroon",
        "CD" to "DemocraticRepublicoftheCongo",
        "CG" to "RepublicofCongo",
        "CO" to "Colombia",
        "CR" to "CostaRica",
        "CU" to "Cuba",
        "CZ" to "CzechRep",
        "DE" to "Germany",
        "DJ" to "Djibouti",
        "DK" to "Denmark",
        "DO" to "DominicanRepublic",
        "DZ" to "Algeria",
        "EC" to "Ecuador",
        "EG" to "Egypt",
        "ER" to "Eritrea",
        "EE" to "Estonia",
        "ET" to "Ethiopia",
        "FI" to "Finland",
        "FJ" to "Fiji",
        "GA" to "Gabon",
        "GB" to "UnitedKingdom",
        "UK" to "UnitedKingdom",
        "GE" to "Georgia",
        "GH" to "Ghana",
        "GN" to "Guinea",
        "GM" to "TheGambia",
        "GW" to "Guinea-Bissau",
        "GQ" to "EquatorialGuinea",
        "GR" to "Greece",
        "GL" to "Greenland",
        "GT" to "Guatemala",
        "GY" to "Guyana",
        "HN" to "Honduras",
        "HR" to "Croatia",
        "HT" to "Haiti",
        "HU" to "Hungary",
        "ID" to "Indonesia",
        "IN" to "India",
        "IE" to "Ireland",
        "IR" to "Iran",
        "IQ" to "Iraq",
        "IS" to "Iceland",
        "IL" to "Israel",
        "IT" to "Italy",
        "JM" to "Jamaica",
        "JO" to "Jordan",
        "JP" to "Japan",
        "KZ" to "Kazakhstan",
        "KE" to "Kenya",
        "KG" to "Kyrgyzstan",
        "KH" to "Cambodia",
        "KR" to "SouthKorea",
        "XK" to "Kosovo",
        "KW" to "Kuwait",
        "LA" to "LaoPDR",
        "LB" to "Lebanon",
        "LR" to "Liberia",
        "LY" to "Libya",
        "LK" to "SriLanka",
        "LS" to "Lesotho",
        "LT" to "Lithuania",
        "LU" to "Luxembourg",
        "LV" to "Latvia",
        "MA" to "Morocco",
        "MD" to "Moldova",
        "MG" to "Madagascar",
        "MX" to "Mexico",
        "MK" to "Macedonia",
        "ML" to "Mali",
        "MM" to "Myanmar",
        "ME" to "Montenegro",
        "MN" to "Mongolia",
        "MZ" to "Mozambique",
        "MR" to "Mauritania",
        "MW" to "Malawi",
        "MY" to "Malaysia",
        "NA" to "Namibia",
        "NE" to "Niger",
        "NG" to "Nigeria",
        "NI" to "Nicaragua",
        "NL" to "Netherlands",
        "NO" to "Norway",
        "NP" to "Nepal",
        "NZ" to "NewZealand",
        "OM" to "Oman",
        "PK" to "Pakistan",
        "PA" to "Panama",
        "PE" to "Peru",
        "PH" to "Philippines",
        "PG" to "PapuaNewGuinea",
        "PL" to "Poland",
        "KP" to "DemRepKorea",
        "PT" to "Portugal",
        "PY" to "Paraguay",
        "PS" to "Palestine",
        "QA" to "Qatar",
        "RO" to "Romania",
        "RU" to "Russia",
        "RW" to "Rwanda",
        "EH" to "WesternSahara",
        "SA" to "SaudiArabia",
        "SD" to "Sudan",
        "SS" to "SouthSudan",
        "SN" to "Senegal",
        "SL" to "SierraLeone",
        "SV" to "ElSalvador",
        "RS" to "Serbia",
        "SR" to "Suriname",
        "SK" to "Slovakia",
        "SI" to "Slovenia",
        "SE" to "Sweden",
        "SZ" to "Swaziland",
        "SY" to "Syria",
        "TD" to "Chad",
        "TG" to "Togo",
        "TH" to "Thailand",
        "TJ" to "Tajikistan",
        "TM" to "Turkmenistan",
        "TL" to "Timor-Leste",
        "TN" to "Tunisia",
        "TR" to "Turkey",
        "TW" to "Taiwan",
        "TZ" to "Tanzania",
        "UG" to "Uganda",
        "UA" to "Ukraine",
        "UY" to "Uruguay",
        "US" to "UnitedStatesofAmerica",
        "UZ" to "Uzbekistan",
        "VE" to "Venezuela",
        "VN" to "Vietnam",
        "VU" to "Vanuatu",
        "YE" to "Yemen",
        "ZA" to "SouthAfrica",
        "ZM" to "Zambia",
        "ZW" to "Zimbabwe",
        "SO" to "Somalia",
        "GF" to "France",
        "FR" to "France",
        "ES" to "Spain",
        "AW" to "Aruba",
        "AI" to "Anguilla",
        "AD" to "Andorra",
        "AG" to "AntiguaandBarbuda",
        "BS" to "Bahamas",
        "BM" to "Bermuda",
        "BB" to "Barbados",
        "KM" to "Comoros",
        "CV" to "CapeVerde",
        "KY" to "CaymanIslands",
        "DM" to "Dominica",
        "FK" to "FalklandIslands",
        "FO" to "FaeroeIslands",
        "GD" to "Grenada",
        "HK" to "HongKong",
        "KN" to "SaintKittsandNevis",
        "LC" to "SaintLucia",
        "LI" to "Liechtenstein",
        "MF" to "SaintMartinFrench",
        "MV" to "Maldives",
        "MT" to "Malta",
        "MS" to "Montserrat",
        "MU" to "Mauritius",
        "NC" to "NewCaledonia",
        "NR" to "Nauru",
        "PN" to "PitcairnIslands",
        "PR" to "PuertoRico",
        "PF" to "FrenchPolynesia",
        "SG" to "Singapore",
        "SB" to "SolomonIslands",
        "ST" to "SãoToméandPrincipe",
        "SX" to "SaintMartinDutch",
        "SC" to "Seychelles",
        "TC" to "TurksandCaicosIslands",
        "TO" to "Tonga",
        "TT" to "TrinidadandTobago",
        "VC" to "SaintVincentandtheGrenadines",
        "VG" to "BritishVirginIslands",
        "VI" to "UnitedStatesVirginIslands",
        "CY" to "Cyprus",
        "RE" to "ReunionFrance",
        "YT" to "MayotteFrance",
        "MQ" to "MartiniqueFrance",
        "GP" to "GuadeloupeFrance",
        "CW" to "CuracoNetherlands",
        "IC" to "CanaryIslandsSpain"
    )

    private const val MAX_LOCALIZED_LENGTH = 60

    const val LOCATION_TO_TV_MAP_COORDINATES_RATIO = 0.294f
}
