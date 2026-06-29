package org.stations;

import java.io.*;
import java.util.*;

public class TrainNetwork {

    private Map<String, Station> stations;

    public TrainNetwork() {
        this.stations = new LinkedHashMap<>(); // LinkedHashMap للحفاظ على ترتيب الإدخال
    }

    public Map<String, Station> getStations() {
        return stations;
    }

    public void addStation(String name, String code) {
        if (!stations.containsKey(name)) {
            stations.put(name, new Station(name, code));
        }
    }

    public void addEdge(String from, String to, int distance) {
        Station s1 = stations.get(from);
        Station s2 = stations.get(to);
        if (s1 != null && s2 != null) {
            s1.addConnection(s2, distance);
        }
    }

    // =========================================================
    // 2. Export: تصدير الشبكة إلى ملف نصي بما فيها المحطات المعزولة
    // =========================================================
    public void exportToFile(String filename) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(filename));

        // أولاً: كتابة تعريف كل محطة مع رمزها
        out.println("# STATIONS");
        for (Station s : stations.values()) {
            out.println("STATION " + s.getName() + " CODE:" + s.getCode());
        }

        // ثانياً: كتابة المسارات
        out.println("# EDGES");
        for (Station s : stations.values()) {
            if (!s.getConnections().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append(s.getName()).append(" -> ");
                List<String> connStrings = new ArrayList<>();
                for (Map.Entry<Station, Integer> entry : s.getConnections().entrySet()) {
                    connStrings.add(entry.getKey().getName() + "(" + entry.getValue() + ")");
                }
                sb.append(String.join(", ", connStrings));
                out.println(sb.toString());
            }
        }
        out.close();
    }

    // =========================================================
    // 2. Import: استيراد الشبكة من ملف نصي مع حفظ الرمز الأصلي
    // =========================================================
    public void importFromFile(String filename) throws IOException {
        stations.clear();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        List<String[]> tempEdges = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            // قراءة تعريف المحطة مع الرمز
            if (line.startsWith("STATION ")) {
                String[] parts = line.split("CODE:");
                String name = parts[0].replace("STATION ", "").trim();
                String code = (parts.length > 1) ? parts[1].trim() : name.substring(0, Math.min(3, name.length())).toUpperCase();
                addStation(name, code);
                continue;
            }

            // قراءة المسارات
            if (line.contains("->")) {
                String[] parts = line.split("->");
                String fromName = parts[0].trim();

                // إضافة المحطة إذا لم تكن موجودة (للتوافق مع الملفات القديمة)
                if (!stations.containsKey(fromName)) {
                    addStation(fromName, fromName.substring(0, Math.min(3, fromName.length())).toUpperCase());
                }

                String targets = parts[1].trim();
                for (String conn : targets.split(",")) {
                    conn = conn.trim();
                    if (conn.isEmpty()) continue;
                    int s = conn.indexOf('(');
                    int e = conn.indexOf(')');
                    if (s != -1 && e != -1) {
                        String toName = conn.substring(0, s).trim();
                        int dist = Integer.parseInt(conn.substring(s + 1, e));
                        if (!stations.containsKey(toName)) {
                            addStation(toName, toName.substring(0, Math.min(3, toName.length())).toUpperCase());
                        }
                        tempEdges.add(new String[]{fromName, toName, String.valueOf(dist)});
                    }
                }
            }
        }
        reader.close();

        for (String[] edge : tempEdges) {
            addEdge(edge[0], edge[1], Integer.parseInt(edge[2]));
        }
    }

    // =========================================================
    // 3. تصدير رسم الشبكة إلى ملف نصي (ASCII Art)
    // =========================================================
    public void exportGraphToTextFile(String filename) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(filename));
        out.println("========================================");
        out.println("       خريطة شبكة القطارات (Graph)");
        out.println("========================================");
        out.println();

        if (stations.isEmpty()) {
            out.println("  [الشبكة فارغة]");
        } else {
            for (Station s : stations.values()) {
                out.println("[ " + s.getName() + " (" + s.getCode() + ") ]");
                if (s.getConnections().isEmpty()) {
                    out.println("    └── (لا توجد مسارات صادرة)");
                } else {
                    List<Map.Entry<Station, Integer>> conns = new ArrayList<>(s.getConnections().entrySet());
                    for (int i = 0; i < conns.size(); i++) {
                        Map.Entry<Station, Integer> entry = conns.get(i);
                        String prefix = (i == conns.size() - 1) ? "    └──" : "    ├──";
                        out.println(prefix + " ──(" + entry.getValue() + "km)--> [ " + entry.getKey().getName() + " ]");
                    }
                }
                out.println();
            }
        }

        out.println("========================================");
        out.println("إجمالي المحطات: " + stations.size());
        int edgeCount = stations.values().stream().mapToInt(st -> st.getConnections().size()).sum();
        out.println("إجمالي المسارات: " + edgeCount);
        out.println("========================================");
        out.close();
    }

    // =========================================================
    // 4. أقصر طريق - Dijkstra (مُصحَّح)
    // =========================================================
    public List<String> shortestPath(String start, String end) {
        if (!stations.containsKey(start) || !stations.containsKey(end)) return null;

        Map<Station, Integer> distances   = new HashMap<>();
        Map<Station, Station> predecessors = new HashMap<>();

        for (Station s : stations.values()) {
            distances.put(s, Integer.MAX_VALUE);
        }

        Station startStation = stations.get(start);
        distances.put(startStation, 0);

        // ✅ استخدام الـ Heap اليدوي
        MinHeapPQ pq = new MinHeapPQ(stations.size() * 10);
        pq.offer(0, startStation);

        Set<Station> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            MinHeapPQ.Entry curr = pq.poll(); // ← يسحب الأقل مسافة
            Station current = curr.station;

            if (visited.contains(current)) continue;
            visited.add(current);

            if (current.getName().equals(end)) break;

            for (Map.Entry<Station, Integer> entry : current.getConnections().entrySet()) {
                Station neighbor = entry.getKey();
                int newDist = curr.distance + entry.getValue();

                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    predecessors.put(neighbor, current);
                    pq.offer(newDist, neighbor); // ← نضيف بالمسافة الجديدة
                }
            }
        }

        // إعادة بناء المسار
        Station endStation = stations.get(end);
        if (distances.get(endStation) == Integer.MAX_VALUE) return null;

        LinkedList<String> path = new LinkedList<>();
        Station step = endStation;
        while (step != null) {
            path.addFirst(step.getName());
            step = predecessors.get(step);
        }
        path.addLast("المسافة الإجمالية: " + distances.get(endStation) + " km");
        return path;
    }

    // =========================================================
    // 5. فحص الدورة المغلقة - DFS
    // =========================================================
    public boolean hasCycle() {
        Set<Station> visited = new HashSet<>();
        Set<Station> recStack = new HashSet<>();
        for (Station s : stations.values()) {
            if (hasCycleUtil(s, visited, recStack)) return true;
        }
        return false;
    }

    private boolean hasCycleUtil(Station s, Set<Station> visited, Set<Station> recStack) {
        if (recStack.contains(s)) return true;
        if (visited.contains(s)) return false;

        visited.add(s);
        recStack.add(s);

        for (Station neighbor : s.getConnections().keySet()) {
            if (hasCycleUtil(neighbor, visited, recStack)) return true;
        }

        recStack.remove(s);
        return false;
    }


    // =========================================================
    // حذف محطة: يزيلها من القائمة ويزيل كل المسارات المتصلة بها
    // =========================================================
    public boolean removeStation(String name) {
        Station toRemove = stations.remove(name);
        if (toRemove == null) return false;
        for (Station s : stations.values()) {
            s.getConnections().remove(toRemove);
        }
        return true;
    }

    // =========================================================
    // حذف مسار بين محطتين
    // =========================================================
    public boolean removeEdge(String from, String to) {
        Station s1 = stations.get(from);
        Station s2 = stations.get(to);
        if (s1 == null || s2 == null) return false;
        return s1.getConnections().remove(s2) != null;
    }

    // =========================================================
    // 6. ترتيب المحطات تنازلياً حسب عدد الاتصالات
    // =========================================================
    public List<Station> getStationsSortedByConnections() {
        List<Station> list = new ArrayList<>(stations.values());
        list.sort((s1, s2) -> Integer.compare(s2.getConnections().size(), s1.getConnections().size()));
        return list;
    }
}
