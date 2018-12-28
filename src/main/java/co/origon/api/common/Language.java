package co.origon.api.common;

public enum Language {
    ENGLISH("en"),
    GERMAN("de"),
    NORWEGIAN("nb");

    private final String languageCoode;

    Language(String languageCoode) {
        this.languageCoode = languageCoode;
    }

    public static Language fromCode(String languageCoode) {
        for (Language language : Language.values()) {
            if (language.languageCoode.equals(languageCoode)) {
                return language;
            }
        }

        throw new IllegalArgumentException("Unknown or unsupported language: " + languageCoode);
    }
}
