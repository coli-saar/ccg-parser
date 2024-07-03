package de.saar.coli.ccgparser;

import com.codemelon.util.MinHeap;

import java.util.*;

public class Agenda {
    private MinHeap<Item> agenda;
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
        agenda = new MinHeap<>(agendaComparator);
    }

    public boolean enqueue(Item item) {
        return agenda.add(item);
    }

    public Item dequeue() {
        return agenda.poll();
    }

    public boolean isEmpty() {
        return agenda.isEmpty();
    }

    /**
     * Replaces a previous instance of this item in the agenda with an
     * updated instance. This method does not actually check whether
     * the new item improves over the score of the old item - check
     * this yourself before calling the method.
     *
     * @param item
     */
    public void decreaseKey(Item item) {
        agenda.decreaseKey(item, itit -> itit.setScore(item.getScore()));
//        agenda.remove(item);
//        agenda.add(item);
    }

    @Override
    public String toString() {
        // This hack should no longer be needed for the TreeSet.

//        // move items, in order, from agenda to list
//        List<Item> items = new ArrayList<>();
//        while (!agenda.isEmpty()) {
//            items.add(agenda.poll());
//        }
//
//        // copy them back into the agenda
//        for( Item item : items ) {
//            agenda.offer(item);
//        }

        StringBuffer buf = new StringBuffer();
        for( Item item : agenda ) {
            buf.append(item.toString(estimator));
            buf.append("\n");
        }

        return buf.toString();
    }
}
