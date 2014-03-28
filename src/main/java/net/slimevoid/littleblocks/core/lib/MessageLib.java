package net.slimevoid.littleblocks.core.lib;

public class MessageLib {

    private static final String MESSAGE_PREFIX            = "message.lb.";

    private static final String DENY_PREFIX               = MESSAGE_PREFIX
                                                            + "deny.";

    public static final String  DENY_PLACEMENT            = DENY_PREFIX
                                                            + "place";                   // "Sorry, you cannot place that here!"
    public static final String  DENY_USE                  = DENY_PREFIX + "use";         // "Sorry, you cannot use that here!"
    public static final String  DENY_COPY                 = DENY_PREFIX
                                                            + "copy";                    // "Sorry, that feature is only available in Creative Mode!"

    public static final String  DENY_WAND                 = DENY_PREFIX
                                                            + "wand";

    public static final String  COMMAND_BLOCK_DISABLED    = "advMode.notEnabled";
    public static final String  COMMAND_BLOCK_SUCCESS     = "advMode.setCommand.success";
    public static final String  COMMAND_BLOCK_NOT_ALLOWED = "advMode.notAllowed";

}
