package pt.sapo.labs;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.sapo.labs.api.services.StatusAdapter;
import pt.sapo.labs.api.services.StatusMetadataHandler;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 8/22/13
 * Time: 7:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class ApplicationServiceLoader {

    private static Logger logger = LoggerFactory.getLogger(ApplicationServiceLoader.class);

    public static <T> List<T> loadAdapters(Class<T> api) {

        System.out.println(api);

        List<T> result = new ArrayList<T>();

        ServiceLoader<T> impl = ServiceLoader.load(api);

        for (T loadedImpl : impl) {
            if(loadedImpl != null){
                result.add(loadedImpl);
            }
        }

        if ( result.size() == 0 ) {
            System.out.println("Cannot find implementation for: " + api);
//            throw new RuntimeException("Cannot find implementation for: " + api);


        }

        return result;
    }

//    public static final List<StatusAdapter> myAdapters = loadAdapters(StatusAdapter.class);

    public static void main(String[] args) {

        ServiceLoader<StatusAdapter> adaptersImpl = ServiceLoader.load(StatusAdapter.class);
        for (StatusAdapter impl : adaptersImpl) {
            System.out.println("class: " + impl.getClass());
        }

        ServiceLoader<StatusMetadataHandler> handlersImpl = ServiceLoader.load(StatusMetadataHandler.class);
        for (StatusMetadataHandler impl : handlersImpl) {
            System.out.println("class: " + impl.getClass());
        }
    }
}
