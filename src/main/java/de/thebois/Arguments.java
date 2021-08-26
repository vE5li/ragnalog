package de.thebois;

import com.beust.jcommander.Parameter;

public class Arguments {

    @Parameter(names = { "-s", "--server" }, description = "server ip address")
    String serverAddress = "54.39.177.104";

    @Parameter(names = { "-d", "--device" }, description = "network device name")
    String deviceName = "enp0s31f6";

    @Parameter(names = { "-t", "--timeout" }, description = "network timeout")
    int timeout = 10;

    @Parameter(names = { "-u", "--unknown" }, description = "show unknown packets")
    boolean showUnknown = false;

    @Parameter(names = { "-i", "--incoming" }, description = "show incoming packets")
    boolean showIncoming = true;

    @Parameter(names = { "-o", "--outgoing" }, description = "show outgoing packets")
    boolean showOutgoing = true;

    @Parameter(names = { "-c", "--colors" }, description = "use colored output")
    boolean useColors = false;

    @Parameter(names = { "-p", "--ping" }, description = "show ping")
    boolean showPing = false;
}