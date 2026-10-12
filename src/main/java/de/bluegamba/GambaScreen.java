package de.bluegamba;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.Random;

public class GambaScreen extends Screen {
    private static final Item[] ORES = {
            Items.COAL_ORE,
            Items.IRON_ORE,
            Items.GOLD_ORE,
            Items.DIAMOND_ORE,
            Items.EMERALD_ORE,
            Items.ANCIENT_DEBRIS
    };
    private static final String[] NAMES = {
            "Kohle", "Eisen", "Gold", "Diamant", "Smaragd", "Netherit"
    };
    private static final double[] MULTIPLIERS = {0.5, 1.2, 2.0, 5.0, 10.0, 25.0};

    private final Random random = new Random();
    private long coins = 1000;
    private int bet = 100;
    private int[] shown = {0, 1, 2};
    private int[] result = {0, 1, 2};
    private int spinTicks = 0;
    private int spinDuration = 0;
    private boolean spinning = false;
    private String status = "Gib deinen Einsatz ein und drücke DREHEN.";
    private TextFieldWidget betField;
    private ButtonWidget spinButton;
    private ButtonWidget minusButton;
    private ButtonWidget plusButton;

    public GambaScreen() {
        super(Text.literal("Blue Gamba"));
    }

    @Override
    protected void init() {
        int center = width / 2;
        int panelLeft = center - 145;
        int panelTop = height / 2 - 105;

        betField = new TextFieldWidget(textRenderer, center - 45, panelTop + 130, 90, 20,
                Text.literal("Einsatz"));
        betField.setText(Integer.toString(bet));
        betField.setMaxLength(12);
        addDrawableChild(betField);

        minusButton = addDrawableChild(ButtonWidget.builder(Text.literal("-"), b -> {
            if (!spinning) {
                bet = Math.max(1, bet - 10);
                betField.setText(Integer.toString(bet));
            }
        }).dimensions(center - 82, panelTop + 130, 30, 20).build());

        plusButton = addDrawableChild(ButtonWidget.builder(Text.literal("+"), b -> {
            if (!spinning) {
                bet = Math.min(Integer.MAX_VALUE, bet + 10);
                betField.setText(Integer.toString(bet));
            }
        }).dimensions(center + 52, panelTop + 130, 30, 20).build());

        spinButton = addDrawableChild(ButtonWidget.builder(Text.literal("DREHEN"), b -> spin())
                .dimensions(center - 55, panelTop + 157, 110, 22).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Schließen"), b -> close())
                .dimensions(center - 45, panelTop + 184, 90, 20).build());

        updateControls();
    }

    private void updateControls() {
        boolean disabled = spinning;
        if (spinButton != null) spinButton.active = !disabled;
        if (minusButton != null) minusButton.active = !disabled;
        if (plusButton != null) plusButton.active = !disabled;
        if (betField != null) betField.setEditable(!disabled);
    }

    private void spin() {
        if (spinning) return;

        String raw = betField.getText().trim();
        long entered;
        try {
            if (raw.isEmpty()) throw new NumberFormatException();
            entered = Long.parseLong(raw);
        } catch (NumberFormatException ex) {
            status = "Fehler: Bitte eine gültige ganze Zahl eingeben.";
            return;
        }

        if (entered <= 0) {
            status = "Fehler: Der Einsatz muss größer als 0 sein.";
            BlueGambaClient.message("§cBlue Gamba: Der Einsatz muss größer als 0 sein.");
            return;
        }
        if (entered > coins) {
            status = "Fehler: Du hast nicht genug Coins.";
            BlueGambaClient.message("§cBlue Gamba: Nicht genug Coins. Kontostand: " + coins);
            return;
        }

        bet = (int) entered;
        coins -= entered;
        spinDuration = 40 + random.nextInt(41); // 2–4 Sekunden bei 20 Ticks/s
        spinTicks = 0;
        spinning = true;
        status = "Die Walzen drehen sich ...";
        updateControls();
    }

    @Override
    public void tick() {
        super.tick();
        if (!spinning) return;

        spinTicks++;
        if (spinTicks % 2 == 0) {
            shown = new int[] {
                    random.nextInt(ORES.length),
                    random.nextInt(ORES.length),
                    random.nextInt(ORES.length)
            };
        }

        if (spinTicks >= spinDuration) {
            shown = new int[] {
                    random.nextInt(ORES.length),
                    random.nextInt(ORES.length),
                    random.nextInt(ORES.length)
            };
            result = shown.clone();
            spinning = false;
            finishSpin();
            updateControls();
        }
    }

    private void finishSpin() {
        if (result[0] == result[1] && result[1] == result[2]) {
            int symbol = result[0];
            long payout = (long) Math.floor(bet * MULTIPLIERS[symbol]);
            coins += payout;
            status = "GEWINN! 3x " + NAMES[symbol] + " — Auszahlung: " + payout + " Coins.";
        } else {
            status = "Keine drei gleichen Symbole. Einsatz verloren.";
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        int cx = width / 2;
        int top = height / 2 - 105;
        int left = cx - 145;
        int right = cx + 145;

        // Chest-GUI-inspiriertes blaues Panel und Glasrahmen
        context.fill(left, top, right, top + 210, 0xFF101A2B);
        context.fill(left + 4, top + 4, right - 4, top + 30, 0xFF164B91);
        context.drawCenteredTextWithShadow(textRenderer, title, cx, top + 12, 0xFFFFFFFF);

        // Blaues Glas als Rahmen
        for (int x = left + 5; x < right - 5; x += 18) {
            context.fill(x, top + 35, x + 14, top + 49, 0xFF356FC1);
            context.fill(x, top + 91, x + 14, top + 105, 0xFF356FC1);
        }
        context.fill(left + 5, top + 35, left + 19, top + 105, 0xFF356FC1);
        context.fill(right - 19, top + 35, right - 5, top + 105, 0xFF356FC1);

        context.drawCenteredTextWithShadow(textRenderer, "MITTLERE REIHE ZÄHLT", cx, top + 54, 0xFFB7D9FF);

        int[] positions = {cx - 76, cx - 8, cx + 60};
        for (int i = 0; i < 3; i++) {
            int x = positions[i];
            context.fill(x - 5, top + 62, x + 25, top + 92, 0xFF07101D);
            context.drawItem(new ItemStack(ORES[shown[i]]), x, top + 67);
        }

        context.drawCenteredTextWithShadow(textRenderer,
                "Coins: " + coins + "    Einsatz: " + bet,
                cx, top + 112, 0xFFFFD966);

        context.drawCenteredTextWithShadow(textRenderer,
                "0,5x Kohle | 1,2x Eisen | 2x Gold",
                cx, top + 120, 0xFFE3EFFF);
        context.drawCenteredTextWithShadow(textRenderer,
                "5x Diamant | 10x Smaragd | 25x Netherit",
                cx, top + 130, 0xFFE3EFFF);

        // Widgets werden nach dieser Methode gerendert; Status hier oberhalb der Buttons.
        context.drawCenteredTextWithShadow(textRenderer, status, cx, top + 202, 0xFFFFFFFF);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
