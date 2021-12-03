package cache;

public class CacheItem<K, V> {
    private K key;
    private V value;
    private CacheItem prev, next;

    public CacheItem(K key, V value) {
        this.value = value;
        this.key = key;
    }

    // Getters and setters
    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public CacheItem getPrev() {
        return prev;
    }

    public void setPrev(CacheItem prev) {
        this.prev = prev;
    }

    public CacheItem getNext() {
        return next;
    }

    public void setNext(CacheItem next) {
        this.next = next;
    }
}
