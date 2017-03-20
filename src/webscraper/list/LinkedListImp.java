package webscraper.list;

/**
 * Created by matematik on 3/11/17.
 */
public class LinkedListImp <T>{

    private ListObject<T> head;
    private ListObject<T> tail;
    private int count;

    //TODO add an Iterator implementation;
    //for(T elem: linkedListImp)

    public LinkedListImp(){
        count = 0;
    }

    /**
     * Initializes the List with one element
     * @param elem
     */
    public LinkedListImp(T elem){
        head = new ListObject<T>(elem);
        tail = head;
        count = 1;
    }

    /**
     * Adds an element to the end of the list
     * @param elem
     */
    public void add(T elem){
        if(elem == null)return;
        if(tail != null) {
            ListObject<T> tmp = tail;
            tail = new ListObject<T>(elem);
            tmp.nextObject = tail;
            tail.prevObject = tmp;
        }else{
            tail = new ListObject<T>(elem);
            head = tail;
        }
        count++;
    }

    /**
     * Adds the provided LinkedListImp to this list
     * @param list
     */
    public void addAll(LinkedListImp<T> list){
        if(list.isEmpty())return;
        if(tail != null) {
            ListObject<T> tmp = tail;
            tail = list.head;
            tmp.nextObject = tail;
            tail.prevObject = tmp;
            count += list.size();
        }else if(head == null) {
            head = list.head;
            tail = list.tail;
            count = list.count;
        }else {
            System.err.println("ERROR in LinkedListImp::addAll should not be legal");
        }
    }

    /**
     * Pops the first element of the list
     * @return
     */
    public T pop(){
        count -= 1;
        if(head == null){
            count = 0;
            return null;
        }
        T tmp = head.data;
        head = head.nextObject;
        if(head == null)
            count = 0;
        return tmp;
    }

    public ListObject<T> getHead(){
        return head;
    }

    /**
     * Removes all Elements equaling this element
     * Really expensive operation
     * Should only be used if really necessary
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

    /**
     * returns the size of the list
     * @return
     */
    public int size(){
        return count;
    }

    /**
     * clears the content of the list
     */
    public void clear(){
        count = 0;
        head = null;
        tail = null;
    }

    /**
     * returns true if the list is empty
     * @return
     */
    public boolean isEmpty(){
        return head == null;
    }

}
