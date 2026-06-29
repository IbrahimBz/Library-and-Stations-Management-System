package org.stations;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class MainFrame extends JFrame {

    private TrainNetwork network;
    private NetworkVisualizer visualizer;
    private JTextArea logArea;

    public MainFrame() {
        network = new TrainNetwork();
        visualizer = new NetworkVisualizer(network);

        setTitle("نظام إدارة شبكة القطارات");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // اللوحة الجانبية للأزرار
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controlPanel.setPreferredSize(new Dimension(200, 0));

        // --- أزرار المحطات ---
        JLabel lblStations = new JLabel("── المحطات ──");
        lblStations.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnAddStation    = new JButton("إضافة / تعديل محطة");
        JButton btnDeleteStation = new JButton("حذف محطة");          // ✅ جديد

        // --- أزرار المسارات ---
        JLabel lblEdges = new JLabel("── المسارات ──");
        lblEdges.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnAddEdge    = new JButton("إضافة مسار");
        JButton btnDeleteEdge = new JButton("حذف مسار");             // ✅ جديد

        // --- أزرار الملفات ---
        JLabel lblFiles = new JLabel("── الملفات ──");
        lblFiles.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnImport         = new JButton("استيراد (Import)");
        JButton btnExport         = new JButton("تصدير (Export)");
        JButton btnExportGraph    = new JButton("تصدير رسم الشبكة");  // ✅ جديد - النقطة 3

        // --- أزرار الخوارزميات ---
        JLabel lblAlgo = new JLabel("── الخوارزميات ──");
        lblAlgo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnShortestPath  = new JButton("أقصر طريق");
        JButton btnCheckCycle    = new JButton("فحص دورة مغلقة");
        JButton btnSortStations  = new JButton("ترتيب المحطات");

        // إضافة كل العناصر
        Dimension btnSize = new Dimension(180, 35);
        for (JButton btn : new JButton[]{btnAddStation, btnDeleteStation,
                btnAddEdge, btnDeleteEdge,
                btnImport, btnExport, btnExportGraph,
                btnShortestPath, btnCheckCycle, btnSortStations}) {
            btn.setMaximumSize(btnSize);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        controlPanel.add(lblStations);
        controlPanel.add(Box.createVerticalStrut(5));
        controlPanel.add(btnAddStation);
        controlPanel.add(Box.createVerticalStrut(5));
        controlPanel.add(btnDeleteStation);
        controlPanel.add(Box.createVerticalStrut(15));

        controlPanel.add(lblEdges);
        controlPanel.add(Box.createVerticalStrut(5));
        controlPanel.add(btnAddEdge);
        controlPanel.add(Box.createVerticalStrut(5));
        controlPanel.add(btnDeleteEdge);
        controlPanel.add(Box.createVerticalStrut(15));

        controlPanel.add(lblFiles);
        controlPanel.add(Box.createVerticalStrut(5));
        controlPanel.add(btnImport);
        controlPanel.add(Box.createVerticalStrut(5));
        controlPanel.add(btnExport);
        controlPanel.add(Box.createVerticalStrut(5));
        controlPanel.add(btnExportGraph);
        controlPanel.add(Box.createVerticalStrut(15));

        controlPanel.add(lblAlgo);
        controlPanel.add(Box.createVerticalStrut(5));
        controlPanel.add(btnShortestPath);
        controlPanel.add(Box.createVerticalStrut(5));
        controlPanel.add(btnCheckCycle);
        controlPanel.add(Box.createVerticalStrut(5));
        controlPanel.add(btnSortStations);

        // منطقة الإخراج
        logArea = new JTextArea(8, 30);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane logScroll = new JScrollPane(logArea);

        add(controlPanel, BorderLayout.WEST);
        add(visualizer, BorderLayout.CENTER);
        add(logScroll, BorderLayout.SOUTH);

        // ===================== أحداث الأزرار =====================

        // إضافة محطة
        btnAddStation.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "أدخل اسم المحطة:");
            String code = JOptionPane.showInputDialog(this, "أدخل رمز المحطة:");
            if (name != null && !name.isEmpty() && code != null && !code.isEmpty()) {
                network.addStation(name, code);
                log("✅ تمت إضافة المحطة: " + name + " (" + code + ")");
                visualizer.resetPositions();
            }
        });

        // حذف محطة ✅ جديد
        btnDeleteStation.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "أدخل اسم المحطة للحذف:");
            if (name != null && !name.isEmpty()) {
                boolean removed = network.removeStation(name);
                if (removed) {
                    log("🗑️ تم حذف المحطة: " + name);
                    visualizer.resetPositions();
                } else {
                    log("❌ المحطة غير موجودة: " + name);
                }
            }
        });

        // إضافة مسار
        btnAddEdge.addActionListener(e -> {
            String from = JOptionPane.showInputDialog(this, "من محطة:");
            String to   = JOptionPane.showInputDialog(this, "إلى محطة:");
            String dist = JOptionPane.showInputDialog(this, "المسافة (km):");
            try {
                if (from != null && to != null && dist != null) {
                    network.addEdge(from, to, Integer.parseInt(dist));
                    log("✅ مسار من " + from + " → " + to + " بمسافة " + dist + " km");
                    visualizer.repaint();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "الرجاء إدخال مسافة رقمية صالحة.");
            }
        });

        // حذف مسار ✅ جديد
        btnDeleteEdge.addActionListener(e -> {
            String from = JOptionPane.showInputDialog(this, "من محطة:");
            String to   = JOptionPane.showInputDialog(this, "إلى محطة:");
            if (from != null && to != null) {
                boolean removed = network.removeEdge(from, to);
                if (removed) {
                    log("🗑️ تم حذف المسار من " + from + " إلى " + to);
                    visualizer.repaint();
                } else {
                    log("❌ المسار غير موجود.");
                }
            }
        });

        // استيراد
        btnImport.addActionListener(e -> {
            try {
                network.importFromFile("network.txt");
                log("📥 تم الاستيراد بنجاح من network.txt (" + network.getStations().size() + " محطات)");
                visualizer.resetPositions();
            } catch (IOException ex) {
                log("❌ فشل الاستيراد: " + ex.getMessage());
            }
        });

        // تصدير
        btnExport.addActionListener(e -> {
            try {
                network.exportToFile("network.txt");
                log("📤 تم التصدير بنجاح إلى network.txt");
            } catch (IOException ex) {
                log("❌ فشل التصدير: " + ex.getMessage());
            }
        });

        // تصدير رسم الشبكة ✅ جديد - النقطة 3
        btnExportGraph.addActionListener(e -> {
            try {
                network.exportGraphToTextFile("graph_map.txt");
                log("🗺️ تم تصدير رسم الشبكة إلى graph_map.txt");
            } catch (IOException ex) {
                log("❌ فشل تصدير الرسم: " + ex.getMessage());
            }
        });

        // أقصر طريق
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

        // فحص الدورة
        btnCheckCycle.addActionListener(e -> {
            boolean hasCycle = network.hasCycle();
            log(hasCycle
                ? "🔄 الشبكة تحتوي على دورة مغلقة (Cycle)!"
                : "✅ الشبكة لا تحتوي على دورة مغلقة.");
        });

        // ترتيب المحطات
        btnSortStations.addActionListener(e -> {
            List<Station> sorted = network.getStationsSortedByConnections();
            log("── ترتيب المحطات حسب الاتصالات ──");
            for (Station s : sorted) {
                log("  " + s.getName() + " → " + s.getConnections().size() + " اتصالات");
            }
        });
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
