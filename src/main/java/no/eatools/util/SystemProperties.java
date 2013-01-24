package no.eatools.util;

import java.util.Properties;

/**
 * @author AB22273
 * @date 14.nov.2008
 * @since 14.nov.2008 10:35:03
 */
public enum SystemProperties {
    JAVA_RUNTIME_NAME,
    SUN_BOOT_LIBRARY_PATH,
    JAVA_VM_VERSION,
    JAVA_VM_VENDOR,
    JAVA_VENDOR_URL,
    PATH_SEPARATOR,
    IDEA_LAUNCHER_PORT,
    JAVA_VM_NAME,
    FILE_ENCODING_PKG,
    SUN_JAVA_LAUNCHER,
    USER_COUNTRY,
    SUN_OS_PATCH_LEVEL,
    JAVA_VM_SPECIFICATION_NAME,
    USER_DIR,
    JAVA_RUNTIME_VERSION,
    JAVA_AWT_GRAPHICSENV,
    JAVA_ENDORSED_DIRS,
    OS_ARCH,
    JAVA_IO_TMPDIR,
    LINE_SEPARATOR,
    JAVA_VM_SPECIFICATION_VENDOR,
    USER_VARIANT,
    OS_NAME,
    SUN_JNU_ENCODING,
    JAVA_LIBRARY_PATH,
    JAVA_SPECIFICATION_NAME,
    JAVA_CLASS_VERSION,
    SUN_MANAGEMENT_COMPILER,
    OS_VERSION,
    USER_HOME,
    USER_TIMEZONE,
    JAVA_AWT_PRINTERJOB,
    IDEA_LAUNCHER_BIN_PATH,
    FILE_ENCODING,
    JAVA_SPECIFICATION_VERSION,
    JAVA_CLASS_PATH,
    USER_NAME,
    JAVA_VM_SPECIFICATION_VERSION,
    JAVA_HOME,
    SUN_ARCH_DATA_MODEL,
    USER_LANGUAGE,
    JAVA_SPECIFICATION_VENDOR,
    AWT_TOOLKIT,
    JAVA_VM_INFO,
    JAVA_VERSION,
    JAVA_EXT_DIRS,
    SUN_BOOT_CLASS_PATH,
    JAVA_VENDOR,
    FILE_SEPARATOR,
    JAVA_VENDOR_URL_BUG,
    SUN_IO_UNICODE_ENCODING,
    SUN_CPU_ENDIAN,
    SUN_DESKTOP,
    SUN_CPU_ISALIST;

    private static Properties systemProperties = System.getProperties();

    public String value() {
        return systemProperties.getProperty(key(),"");
    }

    public String key() {
        return Camel.toPropertyName(super.toString());
    }
}
