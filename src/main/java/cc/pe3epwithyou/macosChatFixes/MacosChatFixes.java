package cc.pe3epwithyou.macosChatFixes;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacosChatFixes implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MacosChatFixes.class);
    private static MacosChatFixes instance;

    public static MacosChatFixes getInstance() {
        return instance;
    }

    @Override
    public void onInitialize() {
        instance = this;
        LOGGER.info("MacosChatFixes has been initialized, macOS is {}", isMac() ? "detected" : "not detected");
    }

    public boolean isMac() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }
}
