package cloud.fogbow.fhs.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;

public class TestUtils {
    public static <T> List<T> getMockedList(int loops, T ... elements) {
        List<Boolean> hasNextAnswer = new ArrayList<Boolean>();
        List<Iterator<T>> iterators = new ArrayList<Iterator<T>>();
        
        for (T element : elements) {
            hasNextAnswer.add(true);
        }
        
        hasNextAnswer.add(false);
        
        for (int i = 0; i < loops; i++) {
            Iterator<T> iterator = Mockito.mock(Iterator.class);
            
            Mockito.when(iterator.hasNext()).thenAnswer(AdditionalAnswers.returnsElementsOf(hasNextAnswer));
            Mockito.when(iterator.next()).then(AdditionalAnswers.returnsElementsOf(Arrays.asList(elements)));
            iterators.add(iterator);
        }
        
        List<T> list = Mockito.mock(ArrayList.class);
        Mockito.when(list.iterator()).thenAnswer(AdditionalAnswers.returnsElementsOf(iterators));
        
        return list;
    }
    
    public static <T> List<T> getListWithElements(T ... elements) {
        List<T> list = new ArrayList<T>();
        
        for (T element : elements) {
            list.add(element);
        }
        
        return list;
    }
}
