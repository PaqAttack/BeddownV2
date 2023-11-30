package com.paqattack.gui_template.data;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Rank {
    private static final Logger logger = Logger.getLogger(Rank.class.getName());
    private String displayName;
    private String abbreviation;

    /**
     * Creates a rank object by sending a String to be evaluated.
     * @param input String representing the rank.
     */
    public Rank(String input) {
        translateInput(input.toUpperCase());
    }

    private void translateInput(String input) {
        if (input.equals("AB")) {
            displayName = "Airman Basic";
            abbreviation = "AB";
            return;
        }
        if (input.equals("AMN")) {
            displayName = "Airman";
            abbreviation = "Amn";
            return;
        }
        if (input.equals("A1C")) {
            displayName = "Airman First Class";
            abbreviation = "A1C";
            return;
        }
        if (input.equals("SRA")) {
            displayName = "Senior Airman";
            abbreviation = "SrA";
            return;
        }
        if (input.equals("SSGT") || input.equals("SSG")) {
            displayName = "Staff Sergeant";
            abbreviation = "SSgt";
            return;
        }
        if (input.equals("TSGT") || input.equals("TSG")) {
            displayName = "Technical Sergeant";
            abbreviation = "TSgt";
            return;
        }
        if (input.equals("MSGT") || input.equals("MSG")) {
            displayName = "Master Sergeant";
            abbreviation = "MSgt";
            return;
        }
        if (input.equals("SMSGT")) {
            displayName = "Senior Master Sergeant";
            abbreviation = "SMSgt";
            return;
        }
        if (input.equals("CMSGT")) {
            displayName = "Chief Master Sergeant";
            abbreviation = "CMSgt";
            return;
        }

        if (input.equals("2LT")) {
            displayName = "Second Lieutenant";
            abbreviation = "2LT";
            return;
        }
        if (input.equals("1LT")) {
            displayName = "First Lieutenant";
            abbreviation = "1LT";
            return;
        }
        if (input.equals("CAPT") || input.equals("CPT")) {
            displayName = "Captain";
            abbreviation = "Capt";
            return;
        }
        if (input.equals("MAJ")) {
            displayName = "Major";
            abbreviation = "Maj";
            return;
        }
        if (input.equals("LTCOL") || input.equals("LTC")) {
            displayName = "Lieutenant Colonel";
            abbreviation = "LtCol";
            return;
        }
        if (input.equals("COL")) {
            displayName = "Colonel";
            abbreviation = "Col";
            return;
        }
        if (input.equals("Gen")) {
            displayName = "General";
            abbreviation = "Gen";
            return;
        }

        displayName = "Unknown";
        abbreviation = "Unk";
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public static List<String> getAbbrevRanks() {
        List<String> ranks = new ArrayList<>();
        ranks.add("AB");
        ranks.add("Amn");
        ranks.add("A1C");
        ranks.add("SrA");
        ranks.add("SSgt");
        ranks.add("TSgt");
        ranks.add("MSgt");
        ranks.add("SMSgt");
        ranks.add("CMSgt");
        ranks.add("2LT");
        ranks.add("1LT");
        ranks.add("Capt");
        ranks.add("Maj");
        ranks.add("LtCol");
        ranks.add("Col");
        ranks.add("Gen");
        return ranks;
    }

    public static List<String> getFullRanks() {
        List<String> ranks = new ArrayList<>();
        ranks.add("Airman Basic");
        ranks.add("Airman");
        ranks.add("Airman First Class");
        ranks.add("Senior Airman");
        ranks.add("Staff Sergeant");
        ranks.add("Technical Sergeant");
        ranks.add("Master Sergeant");
        ranks.add("Senior Master Sergeant");
        ranks.add("Chief Master Sergeant");
        ranks.add("Second Lieutenant");
        ranks.add("First Lieutenant");
        ranks.add("Captain");
        ranks.add("Major");
        ranks.add("Lieutenant Colonel");
        ranks.add("Colonel");
        ranks.add("General");
        return ranks;
    }

    public static String getCodeFromRank(String fullRankName) {
        return switch (fullRankName) {
            case "Airman Basic" -> "AB";
            case "Airman" -> "Amn";
            case "Airman First Class" -> "A1C";
            case "Senior Airman" -> "SrA";
            case "Staff Sergeant" -> "SSgt";
            case "Technical Sergeant" -> "TSgt";
            case "Master Sergeant" -> "MSgt";
            case "Senior Master Sergeant" -> "SMSgt";
            case "Chief Master Sergeant" -> "CMSgt";
            case "Second Lieutenant" -> "2LT";
            case "First Lieutenant" -> "1LT";
            case "Captain" -> "Capt";
            case "Major" -> "Maj";
            case "Lieutenant Colonel" -> "LtCol";
            case "Colonel" -> "Col";
            case "General" -> "Gen";
            default -> "Unk";
        };
    }
}
