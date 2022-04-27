package cloud.fogbow.fhs.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;

public class TestUtils {
    public static <T> List<T> getMockedList(T ... elements) {
        Iterator<T> iterator = Mockito.mock(Iterator.class);
        List<Boolean> hasNextAnswer = new ArrayList<Boolean>();
        
        for (T element : elements) {
            hasNextAnswer.add(true);
        }
        
        hasNextAnswer.add(false);
        
        Mockito.when(iterator.hasNext()).thenAnswer(AdditionalAnswers.returnsElementsOf(hasNextAnswer));
        Mockito.when(iterator.next()).then(AdditionalAnswers.returnsElementsOf(Arrays.asList(elements)));
        
        List<T> list = Mockito.mock(ArrayList.class);
        Mockito.when(list.iterator()).thenReturn(iterator);
        
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
