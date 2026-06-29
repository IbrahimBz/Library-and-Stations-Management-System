package org.stations;

public class MinHeapPQ {

    // الخلية: تحمل المسافة والمحطة معاً
    static class Entry {
        int distance;
        Station station;

        Entry(int distance, Station station) {
            this.distance = distance;
            this.station = station;
        }
    }

    private Entry[] heap;
    private int size;

    public MinHeapPQ(int capacity) {
        heap = new Entry[capacity];
        size = 0;
    }

    // ============================================
    // إضافة عنصر — offer()
    // ============================================
    public void offer(int distance, Station station) {
        // 1. أضف في آخر المصفوفة
        heap[size] = new Entry(distance, station);
        int i = size;
        size++;

        // 2. Bubble Up: ارفع العنصر لمكانه الصحيح
        bubbleUp(i);
    }

    private void bubbleUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;

            // لو الأب أكبر من الابن → بدّلهم
            if (heap[parent].distance > heap[i].distance) {
                swap(parent, i);
                i = parent;
            } else {
                break; // وصل لمكانه الصحيح
            }
        }
    }

    // ============================================
    // سحب الأصغر — poll()
    // ============================================
    public Entry poll() {
        if (size == 0) return null;

        // 1. احفظ الجذر (الأصغر) لترجعه
        Entry min = heap[0];

        // 2. انقل آخر عنصر للجذر
        size--;
        heap[0] = heap[size];
        heap[size] = null;

        // 3. Bubble Down: انزل العنصر لمكانه الصحيح
        bubbleDown(0);

        return min;
    }

    private void bubbleDown(int i) {
        while (true) {
            int left  = 2 * i + 1;
            int right = 2 * i + 2;
            int smallest = i;

            // شوف أيهم أصغر: الأب، اليسار، اليمين
            if (left < size && heap[left].distance < heap[smallest].distance) {
                smallest = left;
            }
            if (right < size && heap[right].distance < heap[smallest].distance) {
                smallest = right;
            }

            // لو الأب هو الأصغر → وقفنا
            if (smallest == i) break;

            // بدّل الأب مع الأصغر وكمّل للأسفل
            swap(i, smallest);
            i = smallest;
        }
    }

    // ============================================
    // دوال مساعدة
    // ============================================
    public boolean isEmpty() {
        return size == 0;
    }

    private void swap(int i, int j) {
        Entry temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }
}