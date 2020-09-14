
package java.util;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
/**
 * 提供了三个静态的内部接口OfInt,OfLong,OfDouble分别对应int,long和double
 * @param <T>
 * @param <T_CONS>
 */
public interface PrimitiveIterator<T, T_CONS> extends Iterator<T> {

    /**
     * Performs the given action for each remaining element, in the order
     * elements occur when iterating, until all elements have been processed
     * or the action throws an exception.  Errors or runtime exceptions
     * thrown by the action are relayed to the caller.
     *
     * @param action The action to be performed for each element
     * @throws NullPointerException if the specified action is null
     */
    @SuppressWarnings("overloads")
    void forEachRemaining(T_CONS action);

    /**
     * An Iterator specialized for {@code int} values.
     * @since 1.8
     */
    public static interface OfInt extends PrimitiveIterator<Integer, IntConsumer> {

        /**
         * Returns the next {@code int} element in the iteration.
         *
         * @return the next {@code int} element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        int nextInt();

        default void forEachRemaining(IntConsumer action) {
            Objects.requireNonNull(action);
            while (hasNext())
                action.accept(nextInt());
        }
        @Override
        default Integer next() {
            if (Tripwire.ENABLED)
                Tripwire.trip(getClass(), "{0} calling PrimitiveIterator.OfInt.nextInt()");
            return nextInt();
        }

        @Override
        default void forEachRemaining(Consumer<? super Integer> action) {
            if (action instanceof IntConsumer) {
                forEachRemaining((IntConsumer) action);
            }
            else {
                // The method reference action::accept is never null
                Objects.requireNonNull(action);
                if (Tripwire.ENABLED)
                    Tripwire.trip(getClass(), "{0} calling PrimitiveIterator.OfInt.forEachRemainingInt(action::accept)");
                forEachRemaining((IntConsumer) action::accept);
            }
        }

    }

    /**
     * An Iterator specialized for {@code long} values.
     * @since 1.8
     */
    public static interface OfLong extends PrimitiveIterator<Long, LongConsumer> {

        /**
         * Returns the next {@code long} element in the iteration.
         *
         * @return the next {@code long} element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        long nextLong();

        default void forEachRemaining(LongConsumer action) {
            Objects.requireNonNull(action);
            while (hasNext())
                action.accept(nextLong());
        }

    
        @Override
        default Long next() {
            if (Tripwire.ENABLED)
                Tripwire.trip(getClass(), "{0} calling PrimitiveIterator.OfLong.nextLong()");
            return nextLong();
        }
        @Override
        default void forEachRemaining(Consumer<? super Long> action) {
            if (action instanceof LongConsumer) {
                forEachRemaining((LongConsumer) action);
            }
            else {
                // The method reference action::accept is never null
                Objects.requireNonNull(action);
                if (Tripwire.ENABLED)
                    Tripwire.trip(getClass(), "{0} calling PrimitiveIterator.OfLong.forEachRemainingLong(action::accept)");
                forEachRemaining((LongConsumer) action::accept);
            }
        }
    }

    /**
     * An Iterator specialized for {@code double} values.
     * @since 1.8
     */
    public static interface OfDouble extends PrimitiveIterator<Double, DoubleConsumer> {

        /**
         * Returns the next {@code double} element in the iteration.
         *
         * @return the next {@code double} element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        double nextDouble();

        default void forEachRemaining(DoubleConsumer action) {
            Objects.requireNonNull(action);
            while (hasNext())
                action.accept(nextDouble());
        }
        @Override
        default Double next() {
            if (Tripwire.ENABLED)
                Tripwire.trip(getClass(), "{0} calling PrimitiveIterator.OfDouble.nextLong()");
            return nextDouble();
        }

        @Override
        default void forEachRemaining(Consumer<? super Double> action) {
            if (action instanceof DoubleConsumer) {
                forEachRemaining((DoubleConsumer) action);
            }
            else {
                // The method reference action::accept is never null
                Objects.requireNonNull(action);
                if (Tripwire.ENABLED)
                    Tripwire.trip(getClass(), "{0} calling PrimitiveIterator.OfDouble.forEachRemainingDouble(action::accept)");
                forEachRemaining((DoubleConsumer) action::accept);
            }
        }
    }
}
