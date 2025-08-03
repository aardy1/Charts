package org.knowtiphy.charts;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

/**
 * Support for font-awesome font loading.
 *
 * @author graham
 */
public class Fonts {

    public static final Font DEFAULT_FONT_12 = Font.font(12);

    public static final Font DEFAULT_FONT_10 = Font.font(10);

    public static final Font DEFAULT_FONT_9 = Font.font(9);

    private static final GlyphFont FONT_AWESOME = GlyphFontRegistry.font("FontAwesome");

    public static Glyph plus() {
        return FONT_AWESOME.create(FontAwesome.Glyph.PLUS);
    }

    public static Glyph minus() {
        return FONT_AWESOME.create(FontAwesome.Glyph.MINUS);
    }

    public static Glyph setting() {
        return FONT_AWESOME.create(FontAwesome.Glyph.REORDER);
    }

    public static Glyph info(Color color) {
        var glyph = FONT_AWESOME.create(FontAwesome.Glyph.INFO_CIRCLE);
        glyph.setColor(color);
        return glyph;
    }

    public static Glyph info() {
        return info(Color.RED);
    }

    public static Glyph caution(Color color) {
        var glyph = FONT_AWESOME.create(FontAwesome.Glyph.EXCLAMATION);
        glyph.setColor(color);
        return glyph;
    }

    public static Glyph caution() {
        return caution(Color.RED);
    }

    public static Glyph jet(Color color) {
        var glyph = FONT_AWESOME.create(FontAwesome.Glyph.PLANE);
        glyph.setColor(color);
        return glyph;
    }

    public static Glyph jet() {
        return jet(Color.RED);
    }

    public static Glyph bomb(Color color) {
        var glyph = FONT_AWESOME.create(FontAwesome.Glyph.BOMB);
        glyph.setColor(color);
        return glyph;
    }

    public static Glyph bomb() {
        return bomb(Color.RED);
    }

    public static Glyph lightHouse(Color color) {
        var glyph = FONT_AWESOME.create(FontAwesome.Glyph.LIGHTBULB_ALT);
        glyph.setColor(color);
        return glyph;
    }

    public static Glyph lightHouse() {
        return lightHouse(Color.YELLOW);
    }

    public static Glyph platform(Color color) {
        var glyph = FONT_AWESOME.create(FontAwesome.Glyph.EMPIRE);
        glyph.setColor(color);
        return glyph;
    }

    public static Glyph platform() {
        return platform(Color.BLACK);
    }

    public static Glyph units() {
        return FONT_AWESOME.create(FontAwesome.Glyph.UNIVERSITY);
    }

    public static Glyph units(int size) {
        var glyph = units();
        glyph.setFontSize(size);
        return glyph;
    }

    public static Glyph resetToOriginalBounds() {
        return FONT_AWESOME.create(FontAwesome.Glyph.REPEAT);
    }

    public static Glyph boat() {
        return FONT_AWESOME.create(FontAwesome.Glyph.SHIP);
    }

    public static Glyph boat(int size) {
        var glyph = FONT_AWESOME.create(FontAwesome.Glyph.SHIP);
        glyph.setFontSize(size);
        return glyph;
    }

    public static Glyph history() {
        return FONT_AWESOME.create(FontAwesome.Glyph.HISTORY);
    }
}