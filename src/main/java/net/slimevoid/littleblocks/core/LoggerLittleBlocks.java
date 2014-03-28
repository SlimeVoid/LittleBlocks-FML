package net.slimevoid.littleblocks.core;

import net.slimevoid.library.data.Logger;

public class LoggerLittleBlocks extends Logger {

    private static LoggerLittleBlocks instance;

    @Override
    protected String getLoggerName() {
        return "LittleBlocksMod";
    }

    public static Logger getInstance(String name) {
        if (instance == null) {
            instance = new LoggerLittleBlocks();
        }

        instance.setName(name);

        return instance;
    }
}
