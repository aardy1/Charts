package org.knowtiphy.charts;

import javafx.geometry.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import org.apache.commons.lang3.tuple.*;
import org.controlsfx.glyphfont.*;

/**
 * Support for font-awesome font loading.
 *
 * @author graham
 */
public class Fonts
{

    public static final Font DEFAULT_FONT_12 = Font.font(12);

    public static final Font DEFAULT_FONT_10 = Font.font(10);

    public static final Font DEFAULT_FONT_9 = Font.font(9);

    public static int DEFAULT_FONT_WIDTH_12 = 0;

    public static int DEFAULT_FONT_WIDTH_10 = 0;

    public static int DEFAULT_FONT_WIDTH_9 = 0;

    static
    {
        var size9 = textSize(DEFAULT_FONT_9);
        DEFAULT_FONT_WIDTH_9 = size9.getLeft().intValue();
        var size10 = textSize(DEFAULT_FONT_10);
        DEFAULT_FONT_WIDTH_10 = size10.getLeft().intValue();
        var size12 = textSize(DEFAULT_FONT_12);
        DEFAULT_FONT_WIDTH_12 = size12.getLeft().intValue();
    }

    private static Text TEXT = new Text();

    public static Pair<Double, Double> textSize(Font font, String s)
    {
        // TODO -- why is this necessary?
        if(TEXT == null)
        {
            TEXT = new Text();
        }

        TEXT.setText(s);
        TEXT.setFont(font);
        return Pair.of(TEXT.getBoundsInLocal().getWidth(), TEXT.getBoundsInLocal().getHeight());
    }

    public static Pair<Double, Double> textSize(Font font)
    {
        return textSize(font, "A");
    }

    public static Bounds textSizeFast(Font font, String s)
    {
        // TODO -- why is this necessary?
        if(TEXT == null)
        {
            TEXT = new Text();
        }

        TEXT.setText(s);
        TEXT.setFont(font);
        return TEXT.getBoundsInLocal();
    }

    private static final GlyphFont FONT_AWESOME = GlyphFontRegistry.font("FontAwesome");

    public static Glyph plus()
    {
        return FONT_AWESOME.create(FontAwesome.Glyph.PLUS);
    }

    public static Glyph minus()
    {
        return FONT_AWESOME.create(FontAwesome.Glyph.MINUS);
    }

    public static Glyph setting()
    {
        return FONT_AWESOME.create(FontAwesome.Glyph.REORDER);
    }

    public static Glyph info(Color color)
    {
        var glyph = FONT_AWESOME.create(FontAwesome.Glyph.INFO_CIRCLE);
        glyph.setColor(color);
        return glyph;
    }

    public static Glyph info()
    {
        return info(Color.RED);
    }

    public static Glyph caution(Color color)
    {
        var glyph = FONT_AWESOME.create(FontAwesome.Glyph.EXCLAMATION);
        glyph.setColor(color);
        return glyph;
    }

    public static Glyph caution()
    {
        return caution(Color.RED);
    }

    public static Glyph jet(Color color)
    {
        var glyph = FONT_AWESOME.create(FontAwesome.Glyph.PLANE);
        glyph.setColor(color);
        return glyph;
    }

    public static Glyph jet()
    {
        return jet(Color.RED);
    }

    public static Glyph bomb(Color color)
    {
        var glyph = FONT_AWESOME.create(FontAwesome.Glyph.BOMB);
        glyph.setColor(color);
        return glyph;
    }

    public static Glyph bomb()
    {
        return bomb(Color.RED);
    }

    public static Glyph lightHouse(Color color)
    {
        var glyph = FONT_AWESOME.create(FontAwesome.Glyph.LIGHTBULB_ALT);
        glyph.setColor(color);
        return glyph;
    }

    public static Glyph lightHouse()
    {
        return lightHouse(Color.YELLOW);
    }

    public static Glyph platform(Color color)
    {
        var glyph = FONT_AWESOME.create(FontAwesome.Glyph.EMPIRE);
        glyph.setColor(color);
        return glyph;
    }

    public static Glyph platform()
    {
        return platform(Color.BLACK);
    }

    public static Glyph resetToOriginalBounds()
    {
        return FONT_AWESOME.create(FontAwesome.Glyph.REPEAT);
    }

    public static Glyph boat()
    {
        return FONT_AWESOME.create(FontAwesome.Glyph.SHIP);
    }

    public static Glyph history()
    {
        return FONT_AWESOME.create(FontAwesome.Glyph.HISTORY);
    }

}