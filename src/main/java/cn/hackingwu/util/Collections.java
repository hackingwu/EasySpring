package cn.hackingwu.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author hackingwu.
 * @since 2015/11/17
 */
public class Collections {
    public static boolean isEmpty(Collection collection){
        return collection == null || collection.isEmpty();
    }

    public static <T> List<T> copyFrom(List<T> source){
        if (source == null) return source;
        Iterator<T> iterator = source.iterator();
        List<T> destination = new ArrayList<T>(source.size());
        while (iterator.hasNext()){
            destination.add(iterator.next());
        }
        return destination;
    }

    public static List unique(List list){
        List l = new ArrayList(list.size());
        Iterator iterator = list.iterator();
        while (iterator.hasNext()){
            Object item = iterator.next();
            if (!l.contains(item)) l.add(item);
        }
        return l;
    }

    public static List<List> slice(List list,int size){
        Iterator i$ = list.iterator();
        int i = 0 ;
        List<List> result = new ArrayList();
        List l = new ArrayList();
        while(i$.hasNext()){
            l.add(i$.next());
            if(++i == size){
                i = 0 ;
                result.add(l);
                l = new ArrayList();
            }
        }
        if (l.size() > 0) result.add(l);
        return result;
    }
    public static String join(List list, String separate) {
        StringBuilder stringBuilder = new StringBuilder();
        if (separate == null) separate = ",";//给separate设置一个默认值
        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                stringBuilder.append(list.get(i).toString());
                if (i < list.size() - 1) {
                    stringBuilder.append(separate);
                }
            }
        }
        return stringBuilder.toString();
    }


    public static <T,C> List<T> collect(Collection<C> collection,Collector<T,C> collector){
        List<T> list = new ArrayList();
        if(collection!=null && collection.size()>0){
            for (C c : collection){
                list.add(collector.collect(c));
            }
        }
        return list;
    }


    public static <T> void each(Collection<T> collection,Consumer<T> consumer){
        Iterator<T> iterator = collection.iterator();
        while (iterator.hasNext()){
            consumer.accept(iterator.next());
        }
    }

    public static <T> void filter(Collection<T> collection,Filter<T> filter){
        Iterator<T> iterator = collection.iterator();
        while (iterator.hasNext()){
            if (!filter.filter(iterator.next())) iterator.remove();
        }
    }

    public static <T> List<T> find(Collection<T> collection,Filter<T> filter){
        Iterator<T> iterator = collection.iterator();
        List<T> result = new ArrayList<T>();
        while (iterator.hasNext()){
            T t = iterator.next();
            if (filter.filter(t)) result.add(t);
        }
        return result;
    }

    public interface Collector<T,C>{
        public T collect(C c);
    }

    public interface Filter<T>{
        public boolean filter(T t);
    }

    public interface Consumer<T> {

        public void accept(T t);

    }
}
