package ru.vmsoftware.events.linked;

/**
 * @author Vyacheslav Mayorov
 * @since 2013-03-05
 */
interface DoubleLinkedEntry<E extends DoubleLinkedEntry<E>> {
    void setNext(E next);
    void setPrevious(E previous);
    E getPrevious();
    E getNext();
}
