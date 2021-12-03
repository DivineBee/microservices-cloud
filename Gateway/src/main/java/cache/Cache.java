package cache;

import java.util.HashMap;
import java.util.Map;

public class Cache<K, V> {
    private Map<K, CacheItem> map;
    private CacheItem first, last;
    private int size;
    private final int CAPACITY;
    private int hitCount = 0;
    private int missCount = 0;

    public Cache(int capacity) {
        CAPACITY = capacity;  //
        map = new HashMap<>(CAPACITY);
    }

    /**
     * Insert data to cache
     * @param key
     * @param value
     */
    public void put(K key, V value) {
        // if key exists then update the value
        CacheItem node = new CacheItem(key, value);

        if (map.containsKey(key) == false) {
            // if capacity is exceeded then delete node using the method's strategy
            if (size() >= CAPACITY) {
                deleteNode(first);
            }
            // else just add the new node at the top
            addNodeToLast(node);
        }
        map.put(key, node);
    }

    /**
     * retrieve data from cache
     * @param key
     * @return
     */
    public V get(K key) {
        // if there is no such key then return
        if (map.containsKey(key) == false) {
            return null;
        }
        CacheItem node = (CacheItem) map.get(key);
        reorder(node);
        return (V) node.getValue();
    }

    /**
     * delete data from cache
     * @param node
     */
    public void deleteNode(CacheItem node) {
        // check if passed node is null
        if (node == null) {
            return;
        }
        //check neighbours of the node
        if (last == node) {
            last = node.getPrev();
        }
        if (first == node) {
            first = node.getNext();
        }
        // remove the node itself
        map.remove(node.getKey());
        node = null; // Optional, collected by GC
        // decrease sie
        size--;
    }

    /**
     * logic of adding the item to the cache at the end
     * @param node
     */
    // last pointer will reference to the last inserted node
    private void addNodeToLast(CacheItem node) {
        // set the order of nodes, one after another
        if (last != null) {
            last.setNext(node);
            node.setPrev(last);
        }
        // last node becomes the current one
        last = node;
        // if this is new record then this node automatically becomes first
        if (first == null) {
            first = node;
        }
        //increase size
        size++;
    }

    /**
     * reorder method which works by Least Recently Used LRU - delete candidate is the oldest used entry.
     * latest accessed node will be at the last end along with newly added items.
     * In this way, we can delete from the first easily.
     * @param node
     */
    private void reorder(CacheItem node) {
        if (last == node) {
            return;
        }
        if (first == node) {
            first = node.getNext();
        } else {
            node.getPrev().setNext(node.getNext());
        }
        last.setNext(node);
        node.setPrev(last);
        node.setNext(null);
        last = node;
    }

    /**
     * Get the size of the cache
     * @return
     */
    public int size() {
        return size;
    }

    public Map<K, CacheItem> getMap() {
        return map;
    }
}
