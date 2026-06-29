package org.stations;

public class MinHeapPQ {

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

    public void offer(int distance, Station station) {
        heap[size] = new Entry(distance, station);
        int i = size;
        size++;

        bubbleUp(i);
    }

    private void bubbleUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;

            if (heap[parent].distance > heap[i].distance) {
                swap(parent, i);
                i = parent;
            } else {
                break;
            }
        }
    }


    public Entry poll() {
        if (size == 0) return null;

        Entry min = heap[0];

        size--;
        heap[0] = heap[size];
        heap[size] = null;

        bubbleDown(0);

        return min;
    }

    private void bubbleDown(int i) {
        while (true) {
            int left  = 2 * i + 1;
            int right = 2 * i + 2;
            int smallest = i;

            if (left < size && heap[left].distance < heap[smallest].distance) {
                smallest = left;
            }
            if (right < size && heap[right].distance < heap[smallest].distance) {
                smallest = right;
            }

            if (smallest == i) break;

            swap(i, smallest);
            i = smallest;
        }
    }


    public boolean isEmpty() {
        return size == 0;
    }

    private void swap(int i, int j) {
        Entry temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }
}