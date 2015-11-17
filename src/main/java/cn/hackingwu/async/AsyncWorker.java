package cn.hackingwu.async;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author hackingwu.
 * @since 2015/9/18
 */
public class AsyncWorker {
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    public static List<Object> on(AsyncTask... asyncTasks) throws ExecutionException, InterruptedException {
        List<Object> result = new ArrayList();
        List<Future> futures = new ArrayList();
        for (int i = 0 ;i < asyncTasks.length ;i++){
            Future future = executorService.submit(asyncTasks[i]);
            futures.add(future);
        }
        Iterator<Future> iterator = futures.iterator();
        while (iterator.hasNext()){
            result.add((Object)(iterator.next().get()));
        }
        return result;
    }

    public static List<Object> on(AsyncInnerTask... asyncInnerTasks) throws ExecutionException, InterruptedException {
        List<Object> result = new ArrayList();
        List<Future> futures = new ArrayList();
        for (int i = 0 ;i < asyncInnerTasks.length ;i++){
            Future future = executorService.submit(asyncInnerTasks[i]);
            futures.add(future);
        }
        Iterator<Future> iterator = futures.iterator();
        while (iterator.hasNext()){
            result.add((Object)(iterator.next().get()));
        }
        return result;
    }
}
