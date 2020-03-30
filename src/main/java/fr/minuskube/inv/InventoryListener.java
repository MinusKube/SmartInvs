package fr.minuskube.inv;

import java.util.function.Consumer;

/**
 * An event listener for clickable items inside a SmartInvs that returns a generic type. By
 * utilizing generics type, we do not need to do type-casting which in return will remove
 * ClassCastException at runtime. If there is no type provided at the time of creation, the
 * compiler will produce a warning that "GenericsType is a raw type".
 * <p>
 * If you want to suppress these warnings you can use the @SuppressWarnings("rawtypes")
 * annotation to suppress the compiler warning.
 *
 * @param <T> Generic type which should be parameterized. If not type is provided, the type
 *           becomes Object.
 */
public class InventoryListener<T> {

    private Class<T> type;
    private Consumer<T> consumer;

    /**
     * This constructor is used to explicitly declare the class generic type  and consumer
     * generics type,
     *
     * @param type generic type
     * @param consumer generic consumer
     */
    public InventoryListener(Class<T> type, Consumer<T> consumer) {
        this.type = type;
        this.consumer = consumer;
    }

    /**
     * Will insert type-casting if necessary and will provide type-checking at compile time. This
     * will ensure that no new classes are created for parameterized types. This feature is called
     * @see "<a href="https://docs.oracle.com/javase/tutorial/java/generics/erasure.html" target="_top">Type Erasure</a>".
     *
     * @param t Replaces the bounded type parameter T with hte first bound interface.
     */
    public void accept(T t) { consumer.accept(t); }

    /**
     * Apply generics type for return and get the type for the inventory.
     *
     * @return the clicked inventory type
     */
    public Class<T> getType() { return type; }

}
