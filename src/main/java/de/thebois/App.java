package de.thebois;

import com.beust.jcommander.JCommander;

import static de.thebois.util.PrintUtils.printField;

public class App {



    public static void main(String[] args) throws Exception {
        Arguments arguments = new Arguments();
        JCommander.newBuilder().addObject(arguments).build().parse(args);

        PacketListener pcap = new PacketListener(arguments);
        pcap.runAsync();

        new Ui().run();


    }
}
