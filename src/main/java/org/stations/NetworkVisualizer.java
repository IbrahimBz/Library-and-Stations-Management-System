
package org.stations;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class NetworkVisualizer extends JPanel {
    private TrainNetwork network;
    private Map<String, Point> positions;

    public NetworkVisualizer(TrainNetwork network) {
        this.network = network;
        this.positions = new HashMap<>();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // تفعيل ميزة تنعيم الحواف والخطوط
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int radius = 25; // نصف قطر المحطة
        int width = getWidth();
        int height = getHeight();

        // 1. توزيع المحطات بشكل دائري ديناميكي في المنتصف
        int i = 0;
        int total = network.getStations().size();
        for (String name : network.getStations().keySet()) {
            if (!positions.containsKey(name) || positions.size() != total) {
                double angle = 2 * Math.PI * i / (total == 0 ? 1 : total);
                int x = (width / 2) + (int) (Math.min(width, height) * 0.35 * Math.cos(angle));
                int y = (height / 2) + (int) (Math.min(width, height) * 0.35 * Math.sin(angle));
                positions.put(name, new Point(x, y));
            }
            i++;
        }

        // =================================================================
        // مكان الكود الجديد: رسم المسارات (خطوط مستقيمة مزاحة مع أسهم وأوزان)
        // =================================================================
        g2.setColor(Color.BLUE);
        for (Station s : network.getStations().values()) {
            Point p1 = positions.get(s.getName());
            if (p1 == null) continue;

            for (Map.Entry<Station, Integer> entry : s.getConnections().entrySet()) {
                Station targetStation = entry.getKey();
                Point p2 = positions.get(targetStation.getName());
                if (p2 == null) continue;

                // حساب فروق الإحداثيات وطول الخط الأصلي
                double dx = p2.x - p1.x;
                double dy = p2.y - p1.y;
                double len = Math.sqrt(dx * dx + dy * dy);
                if (len == 0) continue; 

                // حساب زاوية الخط المستقيم المباشر
                double angle = Math.atan2(dy, dx);

                // نقاط البداية والنهاية الافتراضية للخط
                int startX = p1.x;
                int startY = p1.y;
                int endX = p2.x;
                int endY = p2.y;

                // التحقق من وجود مسار عكسي لفصل الخطوط المستقيمة المتطابقة
                boolean hasReverse = targetStation.getConnections().containsKey(s);
                if (hasReverse) {
                    int offset = 12; // مقدار الإزاحة الجانبية بالبكسل لجعل الخطوط متوازية
                    startX += (int) (-Math.sin(angle) * offset);
                    startY += (int) (Math.cos(angle) * offset);
                    endX += (int) (-Math.sin(angle) * offset);
                    endY += (int) (Math.cos(angle) * offset);
                }

                // حساب نقطة توقف رأس السهم (عند حافة الدائرة البرتقالية)
                int arrowDist = radius + 2; 
                int arrowX = endX - (int) (arrowDist * Math.cos(angle));
                int arrowY = endY - (int) (arrowDist * Math.sin(angle));

                // أ. رسم الخط المستقيم الفعلي
                g2.drawLine(startX, startY, arrowX, arrowY);

                // ب. رسم رأس السهم (المثلث) عند نهاية الخط المستقيم
                int arrowSize = 8; 
                int[] xPoints = {
                    arrowX,
                    arrowX - (int) (arrowSize * Math.cos(angle - Math.PI / 6)),
                    arrowX - (int) (arrowSize * Math.cos(angle + Math.PI / 6))
 };
                int[] yPoints = {
                    arrowY,
                    arrowY - (int) (arrowSize * Math.sin(angle - Math.PI / 6)),
                    arrowY - (int) (arrowSize * Math.sin(angle + Math.PI / 6))
                };
                g2.fillPolygon(xPoints, yPoints, 3);

                // ج. كتابة المسافة في منتصف الخط المستقيم
                int textX = (startX + endX) / 2;
                int textY = (startY + endY) / 2 - 5; 

                g2.setColor(Color.RED);
                g2.drawString(String.valueOf(entry.getValue()), textX, textY);
                g2.setColor(Color.BLUE);
            }
        }
        // =================================================================

        // 3. رسم الدوائر (المحطات والأسماء) فوق الخطوط
        for (Station s : network.getStations().values()) {
            Point p = positions.get(s.getName());
            if (p == null) continue;

            // رسم جسم الدائرة باللون البرتقالي
            g2.setColor(Color.green);
            g2.fillRect(p.x - radius , p.y - radius, radius * 2, radius * 2);
            
            // رسم التحديد الخارجي بالأسود
            g2.setColor(Color.BLACK);
            g2.drawRect(p.x - radius, p.y - radius, radius * 2, radius * 2);
            
            // كتابة الاسم والرمز فوق الدائرة
            String text = s.getName() + " (" + s.getCode() + ")";
            g2.drawString(text, p.x - radius, p.y - radius - 5);
        }
    }
    
    public void resetPositions() {
        positions.clear();
        repaint();
    }
}