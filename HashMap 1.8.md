HashMap 1.8

#### 1. put(K key, V value)

```java
// 看put方法调用流程
public V put(K key, V value) {
    return putVal(hash(key), key, value, false, true);
}
```

```java
/**
 * Implements Map.put and related methods.
 *
 * @param hash hash for key
 * @param key the key
 * @param value the value to put
 * @param onlyIfAbsent if true, don't change existing value
 * @param evict if false, the table is in creation mode.
 * @return previous value, or null if none
 */
final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
               boolean evict) {
    Node<K,V>[] tab; Node<K,V> p; int n, i;
  	// 第一次进行put时，进行resize
    if ((tab = table) == null || (n = tab.length) == 0)
      	// resize后初始化大小为默认大小 16
        n = (tab = resize()).length;
  	// (n - 1) & hash ==> 找到Node在数组中的下标 判断该位置是否有数据，没有则直接创建新			Node，将数据存放在该位置
    if ((p = tab[i = (n - 1) & hash]) == null)
        tab[i] = newNode(hash, key, value, null);
    else {
        Node<K,V> e; K k;
      	// 判断key是否相等，是则直接将新node覆盖原node
        if (p.hash == hash &&
            ((k = p.key) == key || (key != null && key.equals(k))))
            e = p;
      	// 是否是TreeNode，是则在红黑树上插入Node
        else if (p instanceof TreeNode)
            e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
      	// 将Node插入链表
        else {
          	// 遍历链表
            for (int binCount = 0; ; ++binCount) {
              	// 找链表尾部节点
                if ((e = p.next) == null) {
                    // 添加Node到链表尾部
                    p.next = newNode(hash, key, value, null);
                  	// 判断链表个数是否到达阈值 到达阈值链表转红黑树 需要注意的是treeifyBin方法中还会有一个判断，当table长度小于MIN_TREEIFY_CAPACITY时，会继续做resize操作，而不是直接转红黑树
                    if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                        treeifyBin(tab, hash);
                    break;
                }
              	// 比较链表中元素的key是否与插入key相同 相同则新node覆盖原node
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    break;
                p = e;
            }
        }
      	// 存在原始Node，则取出原始值返回
        if (e != null) { // existing mapping for key
            V oldValue = e.value;
            if (!onlyIfAbsent || oldValue == null)
                e.value = value;
            afterNodeAccess(e);
            return oldValue;
        }
    }
    ++modCount;
  	// 判断是否需要resize
    if (++size > threshold)
        resize();
    afterNodeInsertion(evict);
    return null;
}
```

```java
/**
 * Initializes or doubles table size.  If null, allocates in
 * accord with initial capacity target held in field threshold.
 * Otherwise, because we are using power-of-two expansion, the
 * elements from each bin must either stay at same index, or move
 * with a power of two offset in the new table.
 *
 * @return the table
 */
final Node<K,V>[] resize() {
    Node<K,V>[] oldTab = table;
    int oldCap = (oldTab == null) ? 0 : oldTab.length;
    int oldThr = threshold;
    int newCap, newThr = 0;
    if (oldCap > 0) {
        if (oldCap >= MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return oldTab;
        }
      	// 扩容为两倍
        else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                 oldCap >= DEFAULT_INITIAL_CAPACITY)
            newThr = oldThr << 1; // double threshold
    }
    else if (oldThr > 0) // initial capacity was placed in threshold
        newCap = oldThr;
    else {               // zero initial threshold signifies using defaults
        // 通过无参构造创建map，第一次进入resize方法，初始化数组为默认大小
      	newCap = DEFAULT_INITIAL_CAPACITY;
      	// 扩容基准点
        newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
    }
    if (newThr == 0) {
        float ft = (float)newCap * loadFactor;
        newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                  (int)ft : Integer.MAX_VALUE);
    }
    threshold = newThr;
    @SuppressWarnings({"rawtypes","unchecked"})
  	// Node数组初始化
    Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
    table = newTab;
  	// 如果是扩容操作 需要将原数组的数据rehash
    if (oldTab != null) {
        for (int j = 0; j < oldCap; ++j) {
            Node<K,V> e;
          	// 遍历原数组
            if ((e = oldTab[j]) != null) {
              	// for gc？
                oldTab[j] = null;
              	// 如果该位置只有一个Node，则直接rehash
                if (e.next == null)
                  	// 找到该Node在新数组的位置
                    newTab[e.hash & (newCap - 1)] = e;
              	// 红黑树的处理 拆分树
                else if (e instanceof TreeNode)
                    ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
              	// 链表处理
                else { // preserve order
                  	// 定义了两个链表
                    Node<K,V> loHead = null, loTail = null;
                    Node<K,V> hiHead = null, hiTail = null;
                    Node<K,V> next;
                    do {
                        next = e.next;
                      	// 判断rehash后的Node位置是在原位置还是在原位置+oldCap位置															e.hash & oldCap==0 ==> rehash后该Node还是在原位置
                        if ((e.hash & oldCap) == 0) {
                            if (loTail == null)
                                loHead = e;
                            else
                                loTail.next = e;
                            loTail = e;
                        }
                      	// e.hash & oldCap!=0 ==> rehash后该Node在原位置+oldCap位置
                        else {
                            if (hiTail == null)
                                hiHead = e;
                            else
                                hiTail.next = e;
                            hiTail = e;
                        }
                    } while ((e = next) != null);
                    if (loTail != null) {
                        loTail.next = null;
                        newTab[j] = loHead;
                    }
                    if (hiTail != null) {
                        hiTail.next = null;
                        newTab[j + oldCap] = hiHead;
                    }
                }
            }
        }
    }
    return newTab;
}
```