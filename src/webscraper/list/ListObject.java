package webscraper.list;

import java.util.Objects;

/**
 * Created by matematik on 3/11/17.
 */
public class ListObject<T> {
    public ListObject<T> nextObject;
    public ListObject<T> prevObject;
    public T data;

    public ListObject(T elem){
        data = elem;
        nextObject = null;
        prevObject = null;
    }
}
