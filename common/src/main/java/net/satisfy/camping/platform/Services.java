package net.satisfy.camping.platform;

import net.satisfy.camping.Constants;
import net.satisfy.camping.platform.services.IConfigHelper;
import net.satisfy.camping.platform.services.IPlatformHelper;
import net.satisfy.camping.platform.services.IRegisterHelper;

import java.util.ServiceLoader;

public class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final IRegisterHelper REGISTER = load(IRegisterHelper.class);
    public static final IConfigHelper CONFIG = load(IConfigHelper.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}