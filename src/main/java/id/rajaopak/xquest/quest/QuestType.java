package id.rajaopak.xquest.quest;

import java.util.Arrays;

public enum QuestType {

    BREAK,
    PLACE,
    FISHING,
    BRING,
    CRAFT,
    VISIT_WORLD,
    VISIT_NPC,
    VISIT_SERVER;

    public static boolean matches(String value) {
        return Arrays.stream(values()).anyMatch(questType -> value.equalsIgnoreCase(questType.name()));
    }

    public static QuestType parseString(String value) {
        return Arrays.stream(values()).filter(questType -> value.equalsIgnoreCase(questType.name())).findAny().orElse(null);
    }
}