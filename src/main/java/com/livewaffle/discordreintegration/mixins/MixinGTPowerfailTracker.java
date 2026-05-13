package com.livewaffle.discordreintegration.mixins;
import com.livewaffle.discordreintegration.ChatToDiscord;
import com.livewaffle.discordreintegration.DiscordReintegrationMod;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.common.data.GTPowerfailTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value= GTPowerfailTracker.class, remap = false)
public class MixinGTPowerfailTracker {
    @Inject(
        method = "createPowerfailEvent",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/common/network/GTPacketOnPowerfail;<init>(Lgregtech/common/misc/spaceprojects/enums/Powerfail;)V"
        )
    )
    private void discordreintegration$onPowerFail(
        IGregTechTileEntity igte,
        CallbackInfo ci
    ) {
        if (igte == null || igte.getMetaTileEntity() == null) {
            return;
        }
        String Machine = igte.getMetaTileEntity().getLocalName();

        int x = igte.getXCoord();
        int y = igte.getYCoord();
        int z = igte.getZCoord();

        int dim = igte.getWorld().provider.dimensionId;

        ChatToDiscord.createEmbed(
            "Powerfail!",
            "POWER FAIL AT" + x + y + z,
            16755200,
            "d295a929236c1779eab8f57257a86071498a4870196941f4bfe1951e8c6ee21a"
        );
    }


}


