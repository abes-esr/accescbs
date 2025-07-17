package fr.abes.cbs.utilitaire;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnumUtils {
    public static <E extends Enum<E>> List<E> getEnumList(Class<E> enumClass) {
        return Arrays.asList(enumClass.getEnumConstants());
    }

    public static <E extends Enum<E>> Map<String, E> getEnumMap(Class<E> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .collect(Collectors.toMap(Enum::name, e -> e));
    }

    public static <E extends Enum<E>> boolean isValidEnum(Class<E> enumClass, String enumName) {
        try {
            Enum.valueOf(enumClass, enumName);
            return true;
        } catch (IllegalArgumentException | NullPointerException ex) {
            return false;
        }
    }
}
