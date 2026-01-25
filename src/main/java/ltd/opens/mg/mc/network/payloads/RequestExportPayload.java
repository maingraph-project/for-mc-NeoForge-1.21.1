package ltd.opens.mg.mc.network.payloads;

import ltd.opens.mg.mc.MaingraphforMC;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record RequestExportPayload(String name) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RequestExportPayload> TYPE = new CustomPacketPayload.Type<>(Identifier.parse(MaingraphforMC.MODID + ":request_export"));

    public static final StreamCodec<FriendlyByteBuf, RequestExportPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        RequestExportPayload::name,
        RequestExportPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
