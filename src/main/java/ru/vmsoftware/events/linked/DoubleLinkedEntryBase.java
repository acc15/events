package ru.vmsoftware.events.linked;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-04-05
 */
public class DoubleLinkedEntryBase<E extends DoubleLinkedEntry<E>> implements DoubleLinkedEntry<E> {
    public void setNext(E next) {
        this.next = next;
    }

    public void setPrevious(E previous) {
        this.prev = previous;
    }

    public E getPrevious() {
        return prev;
    }

    public E getNext() {
        return next;
    }

    private E next, prev;

}
