package org.stations;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class MainFrame extends JFrame {

    private TrainNetwork network;
    private NetworkVisualizer visualizer;
    private JTextArea logArea;

    private final Color BG_DARK       = new Color(13, 17, 30);
    private final Color BG_CARD       = new Color(22, 28, 48);
    private final Color ACCENT_BLUE   = new Color(59, 130, 246);
    private final Color ACCENT_PURPLE = new Color(139, 92, 246);
    private final Color ACCENT_GREEN  = new Color(16, 185, 129);
    private final Color ACCENT_RED    = new Color(239, 68, 68);
    private final Color TEXT_WHITE    = new Color(241, 245, 249);
    private final Color TEXT_GRAY     = new Color(148, 163, 184);
    private final Color BORDER_COLOR  = new Color(51, 65, 85);

    private void loadDefaultNetwork() {
        network.addStation("Damascus", "DAM");
        network.addStation("Homs",     "HOM");
        network.addStation("Aleppo",   "ALE");
        network.addStation("Daraa",    "DAR");
        network.addStation("Tartous",  "TAR");
        network.addStation("Latakia",  "LAT");

        network.addEdge("Damascus", "Homs",    120);
        network.addEdge("Damascus", "Daraa",    90);
        network.addEdge("Homs",     "Aleppo",  180);
        network.addEdge("Homs",     "Tartous",  95);
        network.addEdge("Aleppo",   "Latakia", 170);
        network.addEdge("Tartous",  "Latakia",  85);
        network.addEdge("Daraa",    "Damascus", 90);
    }

    public MainFrame() {
        network = new TrainNetwork();
        visualizer = new NetworkVisualizer(network);

        loadDefaultNetwork();

        setTitle("نظام إدارة شبكة القطارات");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_DARK);

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(BG_CARD);
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(15, 12, 15, 12));
        controlPanel.setPreferredSize(new Dimension(220, 0));

        JButton btnAddStation    = createMenuButton("إضافة / تعديل محطة", ACCENT_BLUE);
        JButton btnDeleteStation = createMenuButton("حذف محطة", ACCENT_RED);
        JButton btnAddEdge       = createMenuButton("إضافة مسار", ACCENT_BLUE);
        JButton btnDeleteEdge    = createMenuButton("حذف مسار", ACCENT_RED);
        JButton btnImport        = createMenuButton("استيراد (Import)", ACCENT_PURPLE);
        JButton btnExport        = createMenuButton("تصدير (Export)", ACCENT_PURPLE);
        JButton btnExportGraph   = createMenuButton("تصدير رسم الشبكة", ACCENT_PURPLE);
        JButton btnShortestPath  = createMenuButton("أقصر طريق", ACCENT_GREEN);
        JButton btnCheckCycle    = createMenuButton("فحص دورة مغلقة", ACCENT_BLUE);
        JButton btnSortStations  = createMenuButton("ترتيب المحطات", ACCENT_BLUE);
        JButton btnBack = createMenuButton("→ العودة للرئيسية", new Color(100, 116, 139));

        controlPanel.add(btnBack);
        controlPanel.add(Box.createVerticalStrut(15)); // مسافة فاصلة بين زر العودة وباقي القائمة

        controlPanel.add(createSectionLabel("── المحطات ──"));
        controlPanel.add(createSectionLabel("── المحطات ──"));
        controlPanel.add(Box.createVerticalStrut(8));
        controlPanel.add(btnAddStation);
        controlPanel.add(Box.createVerticalStrut(6));
        controlPanel.add(btnDeleteStation);
        controlPanel.add(Box.createVerticalStrut(18));

        controlPanel.add(createSectionLabel("── المسارات ──"));
        controlPanel.add(Box.createVerticalStrut(8));
        controlPanel.add(btnAddEdge);
        controlPanel.add(Box.createVerticalStrut(6));
        controlPanel.add(btnDeleteEdge);
        controlPanel.add(Box.createVerticalStrut(18));

        controlPanel.add(createSectionLabel("── الملفات ──"));
        controlPanel.add(Box.createVerticalStrut(8));
        controlPanel.add(btnImport);
        controlPanel.add(Box.createVerticalStrut(6));
        controlPanel.add(btnExport);
        controlPanel.add(Box.createVerticalStrut(6));
        controlPanel.add(btnExportGraph);
        controlPanel.add(Box.createVerticalStrut(18));

        controlPanel.add(createSectionLabel("── الخوارزميات ──"));
        controlPanel.add(Box.createVerticalStrut(8));
        controlPanel.add(btnShortestPath);
        controlPanel.add(Box.createVerticalStrut(6));
        controlPanel.add(btnCheckCycle);
        controlPanel.add(Box.createVerticalStrut(6));
        controlPanel.add(btnSortStations);

        logArea = new JTextArea(7, 30);
        logArea.setEditable(false);
        logArea.setBackground(new Color(10, 14, 25));
        logArea.setForeground(ACCENT_GREEN);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        logArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        add(controlPanel, BorderLayout.WEST);
        add(visualizer, BorderLayout.CENTER);
        add(logScroll, BorderLayout.SOUTH);

        btnBack.addActionListener(e -> {
            dispose(); // إغلاق شاشة المحطات الحالية وتحرير ذاكرتها
            SwingUtilities.invokeLater(() -> new org.example.SplashScreen().setVisible(true));
        });

        btnAddStation.addActionListener(e -> {
            JTextField nameField = new JTextField(10);
            JTextField codeField = new JTextField(5);
            JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
            panel.add(new JLabel("اسم المحطة:"));
            panel.add(nameField);
            panel.add(new JLabel("رمز المحطة:"));
            panel.add(codeField);

            int result = JOptionPane.showConfirmDialog(this, panel, "إضافة / تعديل محطة", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String code = codeField.getText().trim();
                if (!name.isEmpty() && !code.isEmpty()) {
                    network.addStation(name, code);
                    log("✅ تمت إضافة المحطة: " + name + " (" + code + ")");
                    visualizer.resetPositions();
                }
            }
        });

        btnDeleteStation.addActionListener(e -> {
            String[] stations = network.getStations().keySet().toArray(new String[0]);
            if (stations.length == 0) {
                log("❌ لا توجد محطات لحذفها.");
                return;
            }
            JComboBox<String> stationCombo = new JComboBox<>(stations);
            int result = JOptionPane.showConfirmDialog(this, stationCombo, "اختر المحطة للحذف", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String name = (String) stationCombo.getSelectedItem();
                if (name != null) {
                    boolean removed = network.removeStation(name);
                    if (removed) {
                        log("🗑️ تم حذف المحطة: " + name);
                        visualizer.resetPositions();
                    } else {
                        log("❌ المحطة غير موجودة: " + name);
                    }
                }
            }
        });

        btnAddEdge.addActionListener(e -> {
            String[] stations = network.getStations().keySet().toArray(new String[0]);
            if (stations.length < 2) {
                log("❌ يجب وجود محطتين على الأقل لإضافة مسار.");
                return;
            }
            JComboBox<String> fromCombo = new JComboBox<>(stations);
            JComboBox<String> toCombo = new JComboBox<>(stations);
            JTextField distField = new JTextField(5);

            JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
            panel.add(new JLabel("من محطة:"));
            panel.add(fromCombo);
            panel.add(new JLabel("إلى محطة:"));
            panel.add(toCombo);
            panel.add(new JLabel("المسافة (km):"));
            panel.add(distField);

            int result = JOptionPane.showConfirmDialog(this, panel, "إضافة مسار جديد", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String from = (String) fromCombo.getSelectedItem();
                String to = (String) toCombo.getSelectedItem();
                String dist = distField.getText().trim();
                try {
                    if (from != null && to != null && !dist.isEmpty()) {
                        network.addEdge(from, to, Integer.parseInt(dist));
                        log("✅ مسار من " + from + " → " + to + " بمسافة " + dist + " km");
                        visualizer.repaint();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "الرجاء إدخال مسافة رقمية صالحة.");
                }
            }
        });

        btnDeleteEdge.addActionListener(e -> {
            DefaultComboBoxModel<String> edgeModel = new DefaultComboBoxModel<>();
            for (Station s : network.getStations().values()) {
                for (Station target : s.getConnections().keySet()) {
                    edgeModel.addElement(s.getName() + " → " + target.getName());
                }
            }
            if (edgeModel.getSize() == 0) {
                log("❌ لا توجد مسارات لحذفها.");
                return;
            }
            JComboBox<String> edgeCombo = new JComboBox<>(edgeModel);
            int result = JOptionPane.showConfirmDialog(this, edgeCombo, "اختر المسار للحذف", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String selected = (String) edgeCombo.getSelectedItem();
                if (selected != null) {
                    String[] parts = selected.split(" → ");
                    String from = parts[0];
                    String to = parts[1];
                    boolean removed = network.removeEdge(from, to);
                    if (removed) {
                        log("🗑️ تم حذف المسار من " + from + " إلى " + to);
                        visualizer.repaint();
                    } else {
                        log("❌ المسار غير موجود.");
                    }
                }
            }
        });

        btnImport.addActionListener(e -> {
            try {
                network.importFromFile("network.txt");
                log("📥 تم الاستيراد بنجاح من network.txt (" + network.getStations().size() + " محطات)");
                visualizer.resetPositions();
            } catch (IOException ex) {
                log("❌ فشل الاستيراد: " + ex.getMessage());
            }
        });

        btnExport.addActionListener(e -> {
            try {
                network.exportToFile("network.txt");
                log("📤 تم التصدير بنجاح إلى network.txt");
            } catch (IOException ex) {
                log("❌ فشل التصدير: " + ex.getMessage());
            }
        });

        btnExportGraph.addActionListener(e -> {
            try {
                network.exportGraphToTextFile("graph_map.txt");
                log("🗺️ تم تصدير رسم الشبكة إلى graph_map.txt");
            } catch (IOException ex) {
                log("❌ فشل تصدير الرسم: " + ex.getMessage());
            }
        });

        btnShortestPath.addActionListener(e -> {
            String start = JOptionPane.showInputDialog(this, "محطة الانطلاق:");
            String end   = JOptionPane.showInputDialog(this, "محطة الوصول:");
            if (start == null || end == null) return;
            List<String> path = network.shortestPath(start, end);
            if (path != null) {
                log("🛤️ أقصر طريق: " + String.join(" ← ", path));
            } else {
                log("❌ لا يوجد مسار بين " + start + " و " + end);
            }
        });

        btnCheckCycle.addActionListener(e -> {
            boolean hasCycle = network.hasCycle();
            log(hasCycle
                    ? "🔄 الشبكة تحتوي على دورة مغلقة (Cycle)!"
                    : "✅ الشبكة لا تحتوي على دورة مغلقة.");
        });

        btnSortStations.addActionListener(e -> {
            List<Station> sorted = network.getStationsSortedByConnections();
            log("── ترتيب المحطات حسب الاتصالات ──");
            for (Station s : sorted) {
                log("  " + s.getName() + " → " + s.getConnections().size() + " اتصالات");
            }
        });
    }

    private JLabel createSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT_GRAY);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JButton createMenuButton(String text, Color accent) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean hover = getModel().isRollover();
                boolean press = getModel().isPressed();

                g2.setColor(press ? accent.darker() : (hover ? accent : new Color(30, 41, 59)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(hover ? Color.WHITE : TEXT_WHITE);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setMaximumSize(new Dimension(190, 34));
        btn.setPreferredSize(new Dimension(190, 34));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}