package io.github.tehsenaus.knockout4j;

import java.util.*;


public class KO {
    public interface Subscriber<T> {
        void onChanged(T value);
    }
    public interface Subscription<T> {
        Subscribable<T> getTarget();
        Subscriber<? super T> getSubscriber();
        void dispose();
    }
    public interface Subscribable<T> {
        Subscription<T> subscribe(Subscriber<? super T> subscriber);
    }
    public interface Readable<T> extends Subscribable<T> {
        T get();
    }
    public interface Writable<T> {
        void set(T value);
    }


    public static class BasicSubscription<T> implements Subscription<T> {
        final Subscribable<T> target;
        final Subscriber<? super T> subscriber; // XXX: should this be a weak ref?
        private boolean disposed = false;

        public BasicSubscription(Subscribable<T> target, Subscriber<? super T> subscriber) {
            this.target = target;
            this.subscriber = subscriber;
        }

        public Subscribable<T> getTarget() {
            return target;
        }

        public Subscriber<? super T> getSubscriber() {
            return subscriber;
        }

        @Override
        public void dispose() {
            disposed = true;
        }

        public boolean isDisposed() {
            return disposed;
        }
    }

    public static class SubscriptionCollection<T> implements Subscribable<T> {
        final WeakHashMap<BasicSubscription<T>, Object> subscriptions = new WeakHashMap<BasicSubscription<T>, Object>();

        @Override
        public Subscription<T> subscribe(final Subscriber<? super T> subscriber) {
            BasicSubscription<T> subscription = new BasicSubscription<T>(this, subscriber);

            subscriptions.put(subscription, null);

            return subscription;
        }

        public void notifySubscribers(T value) {
            Collection<BasicSubscription<T>> ss = new ArrayList<BasicSubscription<T>>(subscriptions.keySet());
            try {
                // Begin suppressing dependency detection
                dependencyDetection.begin(null);

                for ( BasicSubscription<T> s : ss ) {
                    if (!s.isDisposed()) s.getSubscriber().onChanged(value);
                }
            } finally {
                dependencyDetection.end();
            }
        }
    }

    public static class Observable<T> extends SubscriptionCollection<T> implements Readable<T>, Writable<T> {
        T value;

        public Observable(T value) {
            this.value = value;
        }

        public T get() {
            dependencyDetection.registerDependency(this);
            return value;
        }

        public T peek() {
            return value;
        }

        public void set(T value) {
            this.value = value;
            valueHasMutated();
        }

        public void valueHasMutated() {
            notifySubscribers(value);
        }
    }


    public static class ObservableCollection<T> extends Observable<Collection<T>> implements Collection<T> {
        public ObservableCollection(Collection<T> value) {
            super(value);
        }

        @Override
        public int size() {
            return get().size();
        }

        @Override
        public boolean isEmpty() {
            return get().isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return get().contains(o);
        }

        @Override
        public Iterator<T> iterator() {
            return get().iterator();
        }

        @Override
        public Object[] toArray() {
            return get().toArray();
        }

        public <T1> T1[] toArray(T1[] a) {
            return get().toArray(a);
        }

        @Override
        public boolean add(T t) {
            if ( value.add(t) ) {
                valueHasMutated();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean remove(Object o) {
            if ( value.remove(o) ) {
                valueHasMutated();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return get().containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            if ( value.addAll(c)) {
                valueHasMutated();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            if ( value.removeAll(c)) {
                valueHasMutated();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            if ( value.retainAll(c)) {
                valueHasMutated();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void clear() {
            if ( !value.isEmpty() ) {
                value.clear();
                valueHasMutated();
            }
        }
    }

    public static class ObservableList<T> extends AbstractList<T> implements Readable<List<T>>, Writable<List<T>> {
        final Observable<List<T>> observable;

        public ObservableList(List<T> value) {
            observable = new Observable<List<T>>(value);
        }

        @Override
        public List<T> get() {
            return observable.get();
        }

        public void set(List<T> value) {
            observable.set(value);
        }

        public Subscription<List<T>> subscribe(Subscriber<? super List<T>> subscriber) {
            return observable.subscribe(subscriber);
        }

        @Override
        public int size() {
            return observable.get().size();
        }

        @Override
        public T get(int index) {
            return get().get(index);
        }

        @Override
        public T set(int index, T element) {
            T prev = observable.value.set(index, element);
            if ( prev != element ) observable.valueHasMutated();
            return prev;
        }

        @Override
        public void add(int index, T element) {
            observable.value.add(index, element);
            observable.valueHasMutated();
        }

        public T remove(int index) {
            T prev = observable.value.remove(index);
            observable.valueHasMutated();
            return prev;
        }
    }




    public abstract static class Computed<T> extends SubscriptionCollection<T> implements Readable<T>, Subscriber<Object>, DependencyDetection.Collector {
        public final static int DEFER_EVAL = 1;

        protected T value;
        private HashMap<Subscribable, Subscription> subscriptions = null;
        private boolean needsEvaluation = true, isBeingEvaluated = false;

        // During evaluation
        private HashMap<Subscribable, Subscription> disposalCandidates;
        private int disposalCount;

        public Computed() {
            this(0);
        }
        public Computed(int flags) {
            if ( (flags & DEFER_EVAL) == 0 )
                evaluateImmediate();
        }

        @Override
        public T get() {
            if ( needsEvaluation ) {
                evaluateImmediate();
            }
            dependencyDetection.registerDependency(this);
            return value;
        }

        @Override
        public void onChanged(Object value) {
            evaluatePossiblyAsync();
        }

        protected abstract T evaluate();

        protected void evaluatePossiblyAsync() {
            // TODO: throttle / rate limit
            evaluateImmediate();
        }

        protected void evaluateImmediate() {
            if ( isBeingEvaluated ) return;

            isBeingEvaluated = true;
            try {
                disposalCandidates = subscriptions;
                disposalCount = disposalCandidates != null ? disposalCandidates.size() : 0;
                subscriptions = null;

                dependencyDetection.begin(this);

                T newValue;
                try {
                    newValue = evaluate();
                } finally {
                    dependencyDetection.end();

                    // Dispose subscriptions
                    if ( disposalCount > 0 ) {
                        for ( Subscription s : disposalCandidates.values() ) {
                            s.dispose();
                        }
                    }

                    needsEvaluation = false;
                }

                value = newValue;
                notifySubscribers(newValue);
            } finally {
                isBeingEvaluated = false;
            }
        }

        @Override
        public void collectDependency(Subscribable<?> subscribable) {
            assert isBeingEvaluated;
            if ( disposalCount > 0 && disposalCandidates.containsKey(subscribable) ) {
                // Don't want to dispose this subscription, as it's still being used
                addSubscriptionToDependency(subscribable, disposalCandidates.get(subscribable));
                disposalCandidates.remove(subscribable);
                disposalCount--;
            } else {
                // Brand new subscription - add it
                addSubscriptionToDependency(subscribable);
            }
        }

        protected void addSubscriptionToDependency(Subscribable<?> s) {
            if ( subscriptions == null || !subscriptions.containsKey(s) )
                addSubscriptionToDependency(s, s.subscribe(this));
        }
        protected void addSubscriptionToDependency(Subscribable<?> s, Subscription<?> subscription) {
            if ( subscriptions == null ) subscriptions = new HashMap<Subscribable, Subscription>();
            subscriptions.put(s, subscription);
        }
    }


    public static abstract class WritableComputed<ReadT, WriteT> extends Computed<ReadT> implements Writable<WriteT> {

    }


    // Dependency detection

    interface DependencyDetection {
        interface Collector {
            void collectDependency(Subscribable<?> subscribable);
        }
        void begin(Collector collector);
        void end();
        void registerDependency(Subscribable s);
    }
    static class ThreadLocalDependencyDetection implements DependencyDetection {
        final ThreadLocal<Collector> currentSet = new ThreadLocal<Collector>();
        final ThreadLocal<Stack<Collector>> savedSets = new ThreadLocal<Stack<Collector>>();

        @Override
        public void begin(Collector collector) {
            Stack<Collector> savedSets = this.savedSets.get();
            if ( savedSets == null ) {
                savedSets = new Stack<Collector>();
                this.savedSets.set(savedSets);
            }

            savedSets.push(currentSet.get());
            currentSet.set(collector);
        }

        @Override
        public void end() {
            Stack<Collector> savedSets = this.savedSets.get();
            currentSet.set( savedSets != null && !savedSets.isEmpty() ? savedSets.pop() : null );
        }

        @Override
        public void registerDependency(Subscribable s) {
            Collector collector = currentSet.get();
            if ( collector != null ) {
                collector.collectDependency(s);
            }
        }
    }

    protected final static DependencyDetection dependencyDetection = new ThreadLocalDependencyDetection();
}
