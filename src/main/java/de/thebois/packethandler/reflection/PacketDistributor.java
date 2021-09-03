package de.thebois.packethandler.reflection;

import de.thebois.packethandler.PacketHandler;
import de.thebois.packethandler.PacketSignature;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.*;

public class PacketDistributor {

    private final List<PacketHandler> catchAllHandlers = new ArrayList<>();
    private final HashMap<ByteArrayWrapper, PacketHandler> specificHandlers = new HashMap<>();


    private PacketDistributor() {
        getPacketHandlers().forEach(handlerClass -> {
            try {
                PacketHandler instance = handlerClass.newInstance();
                Method handleMethod = handlerClass.getDeclaredMethod("handle", byte[].class, boolean.class);
                if (handleMethod.isAnnotationPresent(PacketSignature.class)) {
                    ByteArrayWrapper key = new ByteArrayWrapper(handleMethod.getAnnotation(PacketSignature.class).value());
                    specificHandlers.put(key, instance);
                } else {
                    catchAllHandlers.add(instance);
                }
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    public boolean distribute(byte[] packet, boolean useColors) {
        ByteArrayWrapper key = new ByteArrayWrapper(new byte[]{packet[0], packet[1]});
        catchAllHandlers.forEach(h -> h.handle(packet, useColors));
        if (specificHandlers.containsKey(key)) {
            specificHandlers.get(key).handle(packet, useColors);
            return true;
        }
        return false;
    }

    public static PacketDistributor getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static Set<Class<? extends PacketHandler>> getPacketHandlers() {
        return new Reflections("de.thebois.packethandler").getSubTypesOf(PacketHandler.class);
    }

    private static class ByteArrayWrapper {
        private byte[] value;

        public ByteArrayWrapper(byte[] value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ByteArrayWrapper that = (ByteArrayWrapper) o;
            return Arrays.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(value);
        }
    }

    private static class InstanceHolder {
        private static final PacketDistributor INSTANCE = new PacketDistributor();
    }

}
