package cc.pe3epwithyou.macosChatFixes.mixins;

import cc.pe3epwithyou.macosChatFixes.MacosChatFixes;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EditBox.class)
public class EditBoxMixin {
    @Unique
    private static boolean isWordChar(final String text, final int index) {
        final char c = text.charAt(index);
        if (Character.isLetterOrDigit(c) || c == '_') {
            return true;
        }

        if (c == '\'' || c == '’') {
            final boolean prevIsWord = index > 0 && isPlainWordChar(text.charAt(index - 1));
            final boolean nextIsWord = index + 1 < text.length() && isPlainWordChar(text.charAt(index + 1));
            return prevIsWord && nextIsWord;
        }

        return false;
    }

    @Unique
    private static boolean isPlainWordChar(final char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    @Unique
    private boolean isSuperDown() {
        Window window = Minecraft.getInstance().getWindow();
        return InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_SUPER) || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_SUPER);
    }

    @Unique
    private boolean isAltDown() {
        Window window = Minecraft.getInstance().getWindow();
        return InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_ALT) || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_ALT);
    }

    @Shadow
    private boolean isEditable() {
        throw new AssertionError();
    }

    @Inject(method = "keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z", at = @At("HEAD"), cancellable = true)
    private void macosChatFixes$onKeyPressed(final KeyEvent event, final CallbackInfoReturnable<Boolean> cir) {
        final EditBox self = (EditBox) (Object) this;
        if (!MacosChatFixes.getInstance().isMac() || !self.isActive() || !self.isFocused()) {
            return;
        }

        final boolean cmd = this.isSuperDown();
        final boolean opt = this.isAltDown();
        if (!cmd && !opt) {
            return;
        }

        final boolean shift = event.hasShiftDown();

        switch (event.key()) {
            case GLFW.GLFW_KEY_LEFT -> {
                if (cmd) {
                    self.moveCursorToStart(shift);
                } else {
                    self.moveCursorTo(this.macWordPosition(self, -1), shift);
                }
                cir.setReturnValue(true);
            }
            case GLFW.GLFW_KEY_RIGHT -> {
                if (cmd) {
                    self.moveCursorToEnd(shift);
                } else {
                    self.moveCursorTo(this.macWordPosition(self, 1), shift);
                }
                cir.setReturnValue(true);
            }
            case GLFW.GLFW_KEY_BACKSPACE -> {
                if (this.isEditable()) {
                    if (cmd) {
                        self.deleteCharsToPos(0);
                    } else {
                        self.deleteCharsToPos(this.macWordPosition(self, -1));
                    }
                }
                cir.setReturnValue(true);
            }
            case GLFW.GLFW_KEY_DELETE -> {
                if (this.isEditable()) {
                    if (cmd) {
                        self.deleteCharsToPos(self.getValue().length());
                    } else {
                        self.deleteCharsToPos(this.macWordPosition(self, 1));
                    }
                }
                cir.setReturnValue(true);
            }
            default -> {
            }
        }
    }

    /**
     * Finds the next word boundary the way macOS text fields do: runs of letters/digits/
     * underscore form one "word", runs of other punctuation/symbols form their own
     * separate "word", and whitespace is skipped over rather than stopped on. So
     * "foo.bar" takes three Option-presses to cross (foo | . | bar), not one - matching
     * Cocoa, unlike vanilla's space-only word jump.
     */
    @Unique
    private int macWordPosition(final EditBox self, final int dir) {
        final String text = self.getValue();
        final int len = text.length();
        int pos = self.getCursorPosition();

        if (dir < 0) {
            while (pos > 0 && Character.isWhitespace(text.charAt(pos - 1))) {
                --pos;
            }
            if (pos > 0) {
                final boolean word = isWordChar(text, pos - 1);
                while (pos > 0 && !Character.isWhitespace(text.charAt(pos - 1)) && isWordChar(text, pos - 1) == word) {
                    --pos;
                }
            }
        } else {
            while (pos < len && Character.isWhitespace(text.charAt(pos))) {
                ++pos;
            }
            if (pos < len) {
                final boolean word = isWordChar(text, pos);
                while (pos < len && !Character.isWhitespace(text.charAt(pos)) && isWordChar(text, pos) == word) {
                    ++pos;
                }
            }
        }

        return pos;
    }
}