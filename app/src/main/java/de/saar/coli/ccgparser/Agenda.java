package de.saar.coli.ccgparser;

import java.util.Comparator;
import java.util.PriorityQueue;

public class Agenda {
    private PriorityQueue<Item> agenda;
    private OutsideEstimator estimator;

    private class AgendaComparator implements Comparator<Item> {
        @Override
        public int compare(Item o1, Item o2) {
            return Double.compare(o1.getScore() + estimator.estimate(o1), o2.getScore() + estimator.estimate(o2));
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
}
