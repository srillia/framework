package net.unsun.infrastructure.common.kit;

public class PlatformUtils {

    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();

    private PlatformUtils() {
        throw new AssertionError("utility class must not be instantiated");
    }

    public static boolean isLinux() {
        return OS_NAME.startsWith("linux");
    }

    public static boolean isMac() {
        return OS_NAME.startsWith("mac");
    }

    public static boolean isWindows() {
        return OS_NAME.startsWith("windows");
    }

}