package cache;

import java.util.*;

import static http.RequestParser.*;

public class ConsistentHashing {

    //List of caches to be added to the Hash ring
    public static String[] caches = { "Cache.01", "Cache.3" };

    //key represents the hash value of the cache and value represents the cache
    private static SortedMap<Integer, String> sortedMap = new TreeMap<Integer, String>();

    //Program initialization, put all caches into sortedMap
    static {
        for (int i=0; i<caches.length; i++) {
            int hash = getHash(caches[i]);
            System.out.println("[" + caches[i] + "] Join the collection, his Hash The value is " + hash);
            sortedMap.put(hash, caches[i]);
        }
        System.out.println();
    }

    // Get the node that should be routed to
    public static String getCache(String key) {
        // Get the hash value of the key
        int hash = getHash(key);
       // System.out.println("CACHE - " + hash);
        //Get all Maps that are larger than the Hash value
        SortedMap<Integer, String> subMap = sortedMap.tailMap(hash);
        if (subMap.isEmpty()) {
            //If there is no one larger than the hash value of the key, start with the first node
            Integer i = sortedMap.firstKey();
            //Return to the corresponding cache
            return sortedMap.get(i);
        } else {
            //The first Key is the nearest node clockwise past the node.
            Integer i = subMap.firstKey();
            //Return to the corresponding cache
            return subMap.get(i);
        }
    }

    // FNV1_32_HASH algorithm to calculate the Hash value of the cache.
    public static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        // -----------------ATTENTION NEXT------------->
        hash = hash%360; // for demonstration purposes
        // If the calculated value is negative, take its absolute value.
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }

    public static void replicate(Cache<String, HashMap<String, String>> toCache,
                                 String resultHash, HashMap<String, String> result){
        if (!(toCache.getMap().containsValue(result.get(resultHash)))){
            toCache.put(resultHash, result);
        }
    }

    public static void propagateChange(ArrayList<Cache<String, HashMap<String, String>>> cacheArrayList,
                                    String resultHash, HashMap<String, String> result){
        for (Cache<String, HashMap<String, String>> cache : cacheArrayList){
            replicate(cache, resultHash, result);
            System.out.println("Replication to caches was done " + cache.getMap().toString());
        }
    }

//    public static void main(String[] args) {
//        String[] keys = {"ygeuk faukagyf akud fuasdyg kf", "dfsgvs gs f s gs r fvf",
//                "dadf sd a fa dsfad f asdaf", "jsdghf udsh  iusdfgs ds", "dasheys"};
//        for(int i=0; i<keys.length; i++)
//            System.out.println("[" + keys[i] + "] Of hash The value is " + getHash(keys[i])
//                    + ", Routed to Node [" + getCache(keys[i]) + "]");
//    }
}
