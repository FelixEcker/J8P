package de.felixeckert.j8p;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

public class J8V {
    public static boolean color = false;
    public static byte[] memory = new byte[60 * 1024];
    private static DisplayWindow window;

    public static boolean init = false;

    public static void init() {
        window = new DisplayWindow();
        init   = true;
    }

    public static void step() {
        if (window.isVisible() && window.isDisplayable()) {
            window.render();
        }
    }

    public static void stop() {
        window.setVisible(false);
        window.dispose();
    }

    private static class DisplayWindow extends JFrame {
        private Canvas canvas;

        public DisplayWindow() {
            super("J8P | Digital 8bit Processor | J8V | Digital 8bit video chip");

            setSize(160 * 10, 120 * 10);
            setResizable(false);
            setLocationRelativeTo(null);

            canvas = new Canvas();
            canvas.setSize(160 * 10, 120 * 10);

            add(canvas);
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            setVisible(true);
        }

        public void render() {
            BufferStrategy bs = canvas.getBufferStrategy();

            if (bs == null) {
                canvas.createBufferStrategy(3);
                return;
            }

            Graphics g = bs.getDrawGraphics();

            g.setColor(Color.BLACK);
            g.clearRect(0, 0, 160*10, 120*10);

            for (int x = 0; x < 160; x++) {
                for (int y = 0; y < 120; y++) {
                    if (color) {
                        g.setColor(
                                new Color((memory[x + 160 * y] & 0xFF), (memory[(x + (160 * y)) + 1] & 0xFF), ((memory[(x + (160 * y)) + 2] & 0xFF & 0xFF)))
                        );
                    } else {
                        g.setColor(memory[x+160*y] == 0 ? Color.BLACK : Color.WHITE);
                        //if (memory[x+160*y] != 0) System.out.println("COLOR IS NOT BLACK (VALUE: "+memory[x+160*y]+")"); // <-- DEBUG
                    }
                    g.fillRect(x*10, y*10, 10, 10);
                }
            }

            g.dispose();
            bs.show();
        }
    }
}
