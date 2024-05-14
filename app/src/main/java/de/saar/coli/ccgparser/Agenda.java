package de.saar.coli.ccgparser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Agenda {
    private PriorityQueue<Item> agenda;
    private OutsideEstimator estimator;

    private class AgendaComparator implements Comparator<Item> {
        @Override
        public int compare(Item o1, Item o2) {
            // sort in descending order
            return - Double.compare(o1.getScore() + estimator.estimate(o1), o2.getScore() + estimator.estimate(o2));
        }
    }

    public Agenda(OutsideEstimator estimator) {
        this.estimator = estimator;
        Comparator<Item> agendaComparator = new AgendaComparator();
        agenda = new PriorityQueue<>(agendaComparator);
    }

    public void enqueue(Item item) {
        agenda.add(item);
    }

    public Item dequeue() {
        return agenda.poll();
    }

    public boolean isEmpty() {
        return agenda.isEmpty();
    }

    // This is super slow and should be avoided
    @Override
    public String toString() {
        // move items, in order, from agenda to list
        List<Item> items = new ArrayList<>();
        while (!agenda.isEmpty()) {
            items.add(agenda.poll());
        }

        // copy them back into the agenda
        for( Item item : items ) {
            agenda.offer(item);
        }

        return items.toString();
    }
}
