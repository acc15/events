package ru.vmsoftware.events.collections;

/**
 * <p>This is simple replacement of JDK {@link java.util.Iterator} interface.
 * It was introduced to allow clients of concurrent collections receive only live (read - existing) values from
 * them. </p>
 *
 * <p>The main cause - a contract of standard {@link java.util.Iterator Iterator} interface.
 * It requires that if {@link java.util.Iterator#hasNext() Iterator#hasNext()} returns {@code true}
 * then consecutive call to {@link java.util.Iterator#next() Iterator#next()} should always return an value.
 * But this is not always possible in case of concurrent collections because between calls of
 * {@link java.util.Iterator#hasNext() Iterator#hasNext()} and {@link java.util.Iterator#next() Iterator#next()}
 * might be an removal of the last node.
 * In this case we have next choices: <ul>
 *  <li>Hold a "to-be-next" value inside iterator</li>
 *  <li>{@link java.util.Iterator#next() Iterator#next()} returns
 *    {@code null} which breaks {@link java.util.Iterator Iterator} contract</li>
 *  <li>Block until expected value will be available which is even worst than other variants</li>
 * </ul></p>
 *
 * <p>So an {@link java.util.Iterator Iterator} contract forces implementations to hold an reference
 * to value which is "to-be-next" and return him when {@link java.util.Iterator#next() Iterator#next()} is called.
 * But until iterator holds this reference it may be changed or even deleted which results in that iterator will return
 * a dead value</p>
 *
 * <p>In single threaded implementations this problem is solved by checking for modifications of iterable
 * collection and throwing an {@link java.util.ConcurrentModificationException} if any modification detected
 * which doesn't made by iterator itself</p>
 * <p>In {@code java.util.concurrent} collections this isn't solved and iterators may return dead nodes.
 *  For example, if you look at {@link java.util.concurrent.ConcurrentLinkedQueue} -
 *  try to obtain an {@link java.util.concurrent.ConcurrentLinkedQueue#iterator() iterator},
 *  {@link java.util.concurrent.ConcurrentLinkedQueue#remove() delete first node},
 *  wait for any amount of time, and call {@link java.util.Iterator#next() Iterator#next()} -
 *  it will return an old dead value
 * </p>
 *
 * <p>An iterator without {@link java.util.Iterator#hasNext() Iterator#hasNext()} doesn't have those restrictions
 *  and may return {@code null} if there no more items to iterate.
 *  But in this case what to return when collection holds {@code null} as a correct value
 *  (permit {@code null}'s). The answer probably is simple - don't use {@code null}'s for collections -
 *  yes sometimes it's very convenient and cheap, but there too much disadvantages for using {@code null}'s.
 *  Please, read following
 *  <a href="https://code.google.com/p/guava-libraries/wiki/UsingAndAvoidingNullExplained">Guava article</a>
 *  for more information about this topic</p>
 *
 * @author Vyacheslav Mayorov
 * @since 2013-28-09
 */
public interface SimpleIterator<T> {

    /**
     * Returns next value or {@code null} if there is no more values to iterate
     * @return next value or {@code null}
     */
    T next();

    /**
     * Removes value returned by previous {@link #next()} call
     * @return <p>{@code true} if value was removed from collection</p>
     *  <p>{@code false} if it doesn't. This may happen if specified value
     *  has already been removed and doesn't exists in collection</p>
     * @throws IllegalStateException if {@link #next()} never called or
     *  last call to {@link #next()} returns {@code null}
     * @throws UnsupportedOperationException if iterator doesn't support this action
     */
    boolean remove();
}
