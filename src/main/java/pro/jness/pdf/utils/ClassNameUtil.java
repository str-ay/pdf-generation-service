package pro.jness.pdf.utils;

/**
 * @author Aleksandr Streltsov
 *         2016.03.31
 */
public final class ClassNameUtil {

    private ClassNameUtil() {
    }

    public static String getCurrentClassName() {
        try {
            throw new RuntimeException();
        } catch (RuntimeException e) {
            return e.getStackTrace()[1].getClassName();
        }
    }
}