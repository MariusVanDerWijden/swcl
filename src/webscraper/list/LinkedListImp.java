package webscraper.list;

/**
 * Created by matematik on 3/11/17.
 */
public class LinkedListImp <T>{

    private ListObject<T> head;
    private ListObject<T> tail;
    private int count;

    public LinkedListImp(){
        count = 0;
    }

    public LinkedListImp(T elem){
        head = new ListObject<T>(elem);
        tail = head;
        count = 1;
    }

    public void add(T elem){
        ListObject<T> tmp = tail;
        tail = new ListObject<T>(elem);
        tmp.nextObject = tail;
        tail.prevObject = tmp;
        count++;
    }

    /**
     * Adds the provided LinkedListImp to this list
     * @param list
     */
    public void addAll(LinkedListImp<T> list){
        ListObject<T> tmp = tail;
        tail = list.head;
        tmp.nextObject = tail;
        tail.prevObject = tmp;
        count += list.size();
    }

    public T pop(){
        if(head == null)return null;
        T tmp = head.data;
        head = head.nextObject;
        head.prevObject = null;
        count -= 1;
        return tmp;
    }

    public ListObject<T> getHead(){
        return head;
    }

    /**
     * Really expensive operation
     * Do not use
     * @return returns the number of removed Elements
     */
    public int remove(T elem){
        int cnt = 0;
        ListObject<T> tmp = head;
        while(tmp!=null){
            if(tmp.data.equals(elem)){
                tmp.prevObject.nextObject = tmp.nextObject;
                tmp.nextObject.prevObject = tmp.prevObject;
                tmp.data = null;
                cnt++;
                tmp = tmp.prevObject;
            }
            tmp = tmp.nextObject;
        }
        return cnt;
    }

    public int size(){
        return count;
    }

    public void clear(){
        count = 0;
        head = null;
        tail = null;
    }

    public boolean isEmpty(){
        return count == 0;
    }

}
