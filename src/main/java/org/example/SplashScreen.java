package org.example;

import org.library.LibraryGUI;
import org.stations.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SplashScreen extends JFrame {

    public SplashScreen() {
        setTitle("نظام الإدارة الرقمي");
        setSize(800, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true); // بدون شريط العنوان للمظهر الجميل
        setResizable(false);

        SplashPanel panel = new SplashPanel();
        add(panel);

        // زر إغلاق في الزاوية
        addMouseListener(new MouseAdapter() {});
    }

    // =====================================================
    // اللوحة الرئيسية — كل الرسم هنا
    // =====================================================
    class SplashPanel extends JPanel {

        // ألوان النظام
        private final Color BG_DARK       = new Color(13, 17, 30);
        private final Color BG_CARD       = new Color(22, 28, 48);
        private final Color ACCENT_BLUE   = new Color(59, 130, 246);
        private final Color ACCENT_PURPLE = new Color(139, 92, 246);
        private final Color ACCENT_GREEN  = new Color(16, 185, 129);
        private final Color TEXT_WHITE    = new Color(241, 245, 249);
        private final Color TEXT_GRAY     = new Color(148, 163, 184);
        private final Color BORDER_COLOR  = new Color(51, 65, 85);

        // للأنيميشن
        private float animProgress = 0f;
        private Timer animTimer;

        SplashPanel() {
            setBackground(BG_DARK);
            setLayout(null);

            // أنيميشن دخول
            animTimer = new Timer(16, e -> {
                animProgress = Math.min(animProgress + 0.03f, 1f);
                repaint();
                if (animProgress >= 1f) animTimer.stop();
            });
            animTimer.start();

            buildButtons();
        }

        private void buildButtons() {
            // =====================
            // زر المكتبة
            // =====================
            JButton btnLibrary = createStyledButton(
                "📚  Library System",
                "Manage books, borrowing & reports",
                ACCENT_BLUE,
                new Color(37, 99, 235)
            );
            btnLibrary.setBounds(90, 340, 270, 90);
            btnLibrary.addActionListener(e -> {
                dispose();
                SwingUtilities.invokeLater(() -> new LibraryGUI().setVisible(true));
            });

            // =====================
            // زر المحطات
            // =====================
            JButton btnStations = createStyledButton(
                "🚂  Train Network",
                "Stations, routes & shortest path",
                ACCENT_PURPLE,
                new Color(109, 40, 217)
            );
            btnStations.setBounds(440, 340, 270, 90);
            btnStations.addActionListener(e -> {
                dispose();
                SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
            });

            // زر الإغلاق
            JButton btnClose = new JButton("✕");
            btnClose.setBounds(755, 10, 35, 35);
            btnClose.setForeground(TEXT_GRAY);
            btnClose.setBackground(new Color(0, 0, 0, 0));
            btnClose.setBorderPainted(false);
            btnClose.setContentAreaFilled(false);
            btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnClose.addActionListener(e -> System.exit(0));
            btnClose.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { btnClose.setForeground(TEXT_WHITE); }
                public void mouseExited(MouseEvent e)  { btnClose.setForeground(TEXT_GRAY); }
            });

            add(btnLibrary);
            add(btnStations);
            add(btnClose);
        }

        // =====================================================
        // بناء الزر المخصص
        // =====================================================
        private JButton createStyledButton(String title, String subtitle, Color accent, Color accentDark) {
            JButton btn = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    boolean hover = getModel().isRollover();
                    boolean press = getModel().isPressed();

                    // خلفية الزر
                    Color bg = press ? accentDark : hover ? accent.darker() : BG_CARD;
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                    // حدود ملونة
                    g2.setStroke(new BasicStroke(hover ? 2f : 1.5f));
                    g2.setColor(hover ? accent : BORDER_COLOR);
                    g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 16, 16);

                    // شريط علوي ملون
                    if (hover) {
                        GradientPaint gp = new GradientPaint(0, 0, accent, getWidth(), 0, accentDark);
                        g2.setPaint(gp);
                        g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                    }

                    // النص الرئيسي
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    g2.setColor(hover ? Color.WHITE : TEXT_WHITE);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(title, 20, 38);

                    // النص الثانوي
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    g2.setColor(hover ? new Color(200, 220, 255) : TEXT_GRAY);
                    g2.drawString(subtitle, 20, 60);

                    // سهم
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                    g2.setColor(hover ? Color.WHITE : accent);
                    g2.drawString("→", getWidth() - 35, 45);

                    g2.dispose();
                }
            };

            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return btn;
        }

        // =====================================================
        // رسم الخلفية والعناصر الثابتة
        // =====================================================
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            // خلفية متدرجة
            GradientPaint bgGrad = new GradientPaint(0, 0, BG_DARK, w, h, new Color(20, 10, 40));
            g2.setPaint(bgGrad);
            g2.fillRect(0, 0, w, h);

            // دوائر زخرفية في الخلفية
            drawGlowCircle(g2, -60, -60, 220, ACCENT_BLUE, 0.06f);
            drawGlowCircle(g2, w + 40, h + 40, 260, ACCENT_PURPLE, 0.05f);
            drawGlowCircle(g2, w / 2, h + 80, 180, ACCENT_GREEN, 0.04f);

            float ease = easeOutCubic(animProgress);

            // أيقونة مركزية
            int iconY = (int) (80 - (1 - ease) * 30);
            float iconAlpha = ease;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, iconAlpha));

            // دائرة خلف الأيقونة
            int cx = w / 2, cy = iconY + 45;
            GradientPaint iconBg = new GradientPaint(cx - 45, cy - 45, ACCENT_BLUE, cx + 45, cy + 45, ACCENT_PURPLE);
            g2.setPaint(iconBg);
            g2.fillOval(cx - 45, cy - 45, 90, 90);

            g2.setColor(new Color(255, 255, 255, 30));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(cx - 48, cy - 48, 96, 96);

            // رمز الأيقونة
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            String icon = "⚡";
            g2.drawString(icon, cx - fm.stringWidth(icon) / 2, cy + 13);

            // العنوان الرئيسي
            int titleY = (int) (210 - (1 - ease) * 25);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ease));

            g2.setFont(new Font("Segoe UI", Font.BOLD, 30));
            GradientPaint titleGrad = new GradientPaint(0, 0, TEXT_WHITE, w, 0, new Color(180, 180, 255));
            g2.setPaint(titleGrad);
            FontMetrics fmTitle = g2.getFontMetrics();
            String title = "Digital Management System";
            g2.drawString(title, (w - fmTitle.stringWidth(title)) / 2, titleY);

            // العنوان الفرعي
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            g2.setColor(TEXT_GRAY);
            FontMetrics fmSub = g2.getFontMetrics();
            String sub = "Library & Train Network  •  Choose your module";
            g2.drawString(sub, (w - fmSub.stringWidth(sub)) / 2, titleY + 32);

            // خط فاصل
            int lineY = titleY + 55;
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(BORDER_COLOR);
            g2.drawLine(90, lineY, w - 90, lineY);

            // نقاط زخرفية على الخط
            g2.setColor(ACCENT_BLUE);
            g2.fillOval(w / 2 - 4, lineY - 4, 8, 8);

            // نص "Select Module"
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ease * 0.7f));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(TEXT_GRAY);
            FontMetrics fmSelect = g2.getFontMetrics();
            String sel = "SELECT MODULE";
            // رسم حروف متباعدة
            int selX = (w - fmSelect.stringWidth(sel) - 30) / 2;
            for (char c : sel.toCharArray()) {
                g2.drawString(String.valueOf(c), selX, 325);
                selX += fmSelect.charWidth(c) + 3;
            }

            // حدود النافذة
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2.setColor(BORDER_COLOR);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);

            g2.dispose();
        }

        private void drawGlowCircle(Graphics2D g2, int x, int y, int r, Color color, float alpha) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(color);
            g2.fillOval(x - r, y - r, r * 2, r * 2);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        private float easeOutCubic(float t) {
            return 1 - (float) Math.pow(1 - t, 3);
        }
    }
}
