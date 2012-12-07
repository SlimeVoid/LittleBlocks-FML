package littleblocks.core;

import eurysmods.data.Logger;

public class LoggerLittleBlocks extends Logger {

	@Override
	protected String getLoggerName() {
		return "LittleBlocksMod";
	}
	
	public static Logger getInstance(String name) {
		if (instance == null)
			instance = new LoggerLittleBlocks();

		instance.setName(name);

		return instance;
	}
}
