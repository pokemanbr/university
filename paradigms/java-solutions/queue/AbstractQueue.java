package queue;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractQueue implements Queue {

    protected int size = 0;

    protected abstract void enqueueImpl(Object element);

    @Override
    public void enqueue(Object element) {
        assert element != null : "You can't enqueue element null";
        enqueueImpl(element);
        this.size++;
    }

    protected abstract Object elementImpl();

    @Override
    public Object element() {
        assert this.size > 0 : "You can't get element when queue is empty";
        return elementImpl();
    }

    protected abstract Object dequeueImpl();

    @Override
    public Object dequeue() {
        assert this.size > 0 : "You can't dequeue when queue is empty";
        this.size--;
        return dequeueImpl();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    protected abstract void clearImpl();

    @Override
    public void clear() {
        size = 0;
        clearImpl();
    }

    protected abstract Queue getQueue();

    private Queue cycle(Predicate<Object> predicate, Function<Object, Object> function) {
        Queue result = getQueue();
        for (int i = 0; i < size; i++) {
            if (predicate.test(element())) {
                result.enqueue(function.apply(element()));
            }
            enqueue(dequeue());
        }
        return result;
    }

    @Override
    public Queue filter(Predicate<Object> predicate) {
        assert predicate != null : "Predicate can't be null";
        Function<Object, Object> function = x -> x;
        return cycle(predicate, function);
    }

    @Override
    public Queue map(Function<Object, Object> function) {
        assert function != null : "Function can't be null";
        Predicate<Object> predicate = x -> true;
        return cycle(predicate, function);
    }
}
