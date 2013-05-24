package ru.vmsoftware.events.linked;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-04-05
 */
public class DoubleLinkedEntryBase<E extends DoubleLinkedEntry<E>> implements DoubleLinkedEntry<E> {
    @Override
    public void setNext(E next) {
        this.next = next;
    }

    @Override
    public void setPrevious(E previous) {
        this.prev = previous;
    }

    @Override
    public E getPrevious() {
        return prev;
    }

    @Override
    public E getNext() {
        return next;
    }

    private E next, prev;

}
