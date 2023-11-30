package com.paqattack.gui_template.data;

public enum Gender {
    MALE,
    FEMALE;

    /**
     * Retruns a string reflecting a Gender object
     * @param gender Gender object to be converted
     * @return String ("Male" or "Female")
     */
    public static String getGenderStr(Gender gender) {
        return gender.equals(Gender.MALE)? "Male" : "Female";
    }

    /**
     * Returns a Gender object based on a provided String based on startsWith()
     * @param genderStr This will be evaluated based on the first character being M or F
     * @return Gender object
     */
    public static Gender getGenderFromStr(String genderStr) {
        return genderStr.toUpperCase().startsWith("M")? Gender.MALE : Gender.FEMALE;
    }
}
