package com.telteltey.dockicon.client;

import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Taskbar;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import javax.imageio.ImageIO;

import com.telteltey.dockicon.DockIconMod;
import com.telteltey.dockicon.config.DockIconConfig;

import net.neoforged.fml.loading.FMLPaths;

public final class DockIconManager {
    private static final String DEFAULT_ICON_FILE = "dock_icon.png";

    private DockIconManager() {
    }

    public static void trySetDockIcon() {
        try {
            if (!isMacOs()) {
                return;
            }
            ensureAwtHeadfulProperty();

            Path iconPath = resolveIconPath();
            if (iconPath == null) {
                return;
            }
            if (!Files.isRegularFile(iconPath)) {
                DockIconMod.LOGGER.info("Dock icon file not found at {}; skipping.", iconPath);
                return;
            }
            if (!isPngFile(iconPath)) {
                DockIconMod.LOGGER.warn("Dock icon file is not a PNG: {}; skipping.", iconPath);
                return;
            }

            BufferedImage image = readImage(iconPath);
            if (image == null) {
                return;
            }

            boolean updated = trySetWithTaskbar(image);
            if (!updated) {
                updated = trySetWithAppleEawt(image);
            }
            if (updated) {
                return;
            }

            DockIconMod.LOGGER.warn("No supported Dock icon API available; leaving default icon.");
        } catch (Throwable t) {
            DockIconMod.LOGGER.error("Unexpected failure while updating the Dock icon.", t);
        }
    }

    private static boolean isMacOs() {
        String osName = System.getProperty("os.name", "");
        return osName.toLowerCase(Locale.ROOT).contains("mac");
    }

    private static void ensureAwtHeadfulProperty() {
        if (System.getProperty("java.awt.headless") == null) {
            System.setProperty("java.awt.headless", "false");
        }
    }

    private static BufferedImage readImage(Path path) {
        try (InputStream input = Files.newInputStream(path)) {
            BufferedImage image = ImageIO.read(input);
            if (image == null) {
                DockIconMod.LOGGER.warn("Unsupported image format for Dock icon file: {}", path);
            }
            return image;
        } catch (IOException e) {
            DockIconMod.LOGGER.warn("Failed to read Dock icon file: {}", path, e);
            return null;
        }
    }

    private static Path resolveIconPath() {
        String configured = DockIconConfig.ICON_PATH.get();
        if (configured == null || configured.isBlank()) {
            return FMLPaths.CONFIGDIR.get().resolve(DEFAULT_ICON_FILE);
        }

        try {
            String expanded = expandTilde(configured.trim());
            Path path = Path.of(expanded);
            if (!path.isAbsolute()) {
                path = FMLPaths.CONFIGDIR.get().resolve(path);
            }
            return path.normalize();
        } catch (Exception e) {
            DockIconMod.LOGGER.warn("Invalid Dock icon path in config: {}", configured, e);
            return null;
        }
    }

    private static String expandTilde(String path) {
        if (path.startsWith("~") && (path.length() == 1 || path.charAt(1) == '/' || path.charAt(1) == '\\')) {
            String home = System.getProperty("user.home");
            if (home == null || home.isBlank()) {
                DockIconMod.LOGGER.warn("Cannot expand '~' in iconPath: user.home is not set.");
                return path;
            }
            return home + path.substring(1);
        }
        return path;
    }

    private static boolean isPngFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return fileName.endsWith(".png");
    }

    private static boolean trySetWithTaskbar(Image image) {
        try {
            if (!Taskbar.isTaskbarSupported()) {
                DockIconMod.LOGGER.info("java.awt.Taskbar is not supported; trying Apple EAWT fallback.");
                return false;
            }
            Taskbar taskbar = Taskbar.getTaskbar();
            if (!taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                DockIconMod.LOGGER.info("Taskbar icon image feature is not supported; trying Apple EAWT fallback.");
                return false;
            }
            taskbar.setIconImage(image);
            DockIconMod.LOGGER.info("Dock icon updated using java.awt.Taskbar.");
            return true;
        } catch (HeadlessException e) {
            DockIconMod.LOGGER.info("Taskbar API not available in headless environment; trying Apple EAWT fallback.");
            return false;
        } catch (Throwable t) {
            DockIconMod.LOGGER.warn("Failed to set Dock icon via java.awt.Taskbar; trying Apple EAWT fallback.", t);
            return false;
        }
    }

    private static boolean trySetWithAppleEawt(Image image) {
        try {
            invokeAppleEawt(image);
            DockIconMod.LOGGER.info("Dock icon updated using com.apple.eawt.Application.");
            return true;
        } catch (ClassNotFoundException e) {
            DockIconMod.LOGGER.info("com.apple.eawt.Application not available; cannot set Dock icon via fallback.");
            return false;
        } catch (IllegalAccessException | InaccessibleObjectException e) {
            DockIconMod.LOGGER.debug("Direct com.apple.eawt access failed; trying unnamed module helper.", e);
            return trySetWithAppleEawtUnnamed(image);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IllegalAccessException || cause instanceof InaccessibleObjectException) {
                DockIconMod.LOGGER.debug("Direct com.apple.eawt access failed; trying unnamed module helper.", cause);
                return trySetWithAppleEawtUnnamed(image);
            }
            DockIconMod.LOGGER.warn("Failed to set Dock icon via com.apple.eawt.Application.", e);
            return false;
        } catch (NoSuchMethodException e) {
            DockIconMod.LOGGER.warn("Failed to set Dock icon via com.apple.eawt.Application.", e);
            return false;
        } catch (Throwable t) {
            DockIconMod.LOGGER.warn("Failed to set Dock icon via com.apple.eawt.Application.", t);
            return false;
        }
    }

    private static void invokeAppleEawt(Image image)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<?> appClass = Class.forName("com.apple.eawt.Application");
        Method getApplication = appClass.getMethod("getApplication");
        Object application = getApplication.invoke(null);
        Method setDockIconImage = appClass.getMethod("setDockIconImage", Image.class);
        setDockIconImage.invoke(application, image);
    }

    private static boolean trySetWithAppleEawtUnnamed(Image image) {
        try {
            Class<?> helperClass = loadEawtHelperClass();
            if (!isAppleEawtExportedTo(helperClass.getModule())) {
                DockIconMod.LOGGER.info(
                        "com.apple.eawt is not exported; add JVM arg --add-exports=java.desktop/com.apple.eawt=ALL-UNNAMED to enable fallback.");
                return false;
            }
            Method setDockIcon = helperClass.getMethod("setDockIcon", Image.class);
            setDockIcon.invoke(null, image);
            DockIconMod.LOGGER.info("Dock icon updated using com.apple.eawt.Application (unnamed module).");
            return true;
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            DockIconMod.LOGGER.warn("Failed to set Dock icon via com.apple.eawt.Application (unnamed module).",
                    cause == null ? e : cause);
            return false;
        } catch (Throwable t) {
            DockIconMod.LOGGER.warn("Failed to set Dock icon via com.apple.eawt.Application (unnamed module).", t);
            return false;
        }
    }

    private static boolean isAppleEawtExportedTo(Module targetModule) {
        try {
            Class<?> appClass = Class.forName("com.apple.eawt.Application");
            return appClass.getModule().isExported("com.apple.eawt", targetModule);
        } catch (Throwable t) {
            DockIconMod.LOGGER.warn("Failed to determine module exports for com.apple.eawt.", t);
            return false;
        }
    }

    private static Class<?> loadEawtHelperClass() throws IOException, ClassNotFoundException {
        String className = "com.telteltey.dockicon.client.eawt.EawtHelper";
        String resourcePath = "/" + className.replace('.', '/') + ".class";
        byte[] classBytes;
        try (InputStream input = DockIconManager.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IOException("Missing helper class resource: " + resourcePath);
            }
            classBytes = input.readAllBytes();
        }

        final byte[] helperBytes = classBytes;
        final String helperName = className;
        ClassLoader loader = new ClassLoader(ClassLoader.getPlatformClassLoader()) {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                if (!helperName.equals(name)) {
                    return super.findClass(name);
                }
                return defineClass(name, helperBytes, 0, helperBytes.length);
            }
        };
        return Class.forName(className, true, loader);
    }
}
