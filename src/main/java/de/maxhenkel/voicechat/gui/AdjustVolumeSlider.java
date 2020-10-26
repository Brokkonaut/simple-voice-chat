package de.maxhenkel.voicechat.gui;

import de.maxhenkel.voicechat.Main;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class AdjustVolumeSlider extends AbstractSlider {

    private static final float MAXIMUM = 4F;

    private PlayerEntity player;

    protected AdjustVolumeSlider(int xIn, int yIn, int widthIn, int heightIn, PlayerEntity player) {
        super(xIn, yIn, widthIn, heightIn, new StringTextComponent(""), (player == null ? 1D : Main.VOLUME_CONFIG.getVolume(player.getUniqueID(), 1D)) / MAXIMUM);
        this.player = player;
        if (player == null) {
            field_230694_p_ = false;
        }
        func_230979_b_();
    }

    @Override
    protected void func_230979_b_() {
        long amp = Math.round(field_230683_b_ * MAXIMUM * 100F - 100F);
        func_238482_a_(new TranslationTextComponent("message.volume_amplification", (amp > 0F ? "+" : "") + amp + "%"));
    }

    @Override
    protected void func_230972_a_() {
        Main.VOLUME_CONFIG.setVolume(player.getUniqueID(), field_230683_b_ * MAXIMUM);
    }
}